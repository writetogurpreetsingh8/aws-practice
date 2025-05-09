package aws.lamda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * This Lambda is responsible to receive S3 Event that hold the uploaded CSV file information
 * read the CSV file store it into database and send Notification
 */
public class EasyTaskProcessCSVLambda implements RequestHandler<S3Event, LambdaResponse> {


	LambdaLogger logger = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public LambdaResponse handleRequest(S3Event input, Context context) {

		logger = context.getLogger();
		logger.log("handleRequest is invoked...........\n");
		
		logger.log("s3 data record "+gson.toJson(input.getRecords()));
		logger.log("retrieving secrets from SecretsManager for db");
		
		SecretProperties secret = retrieveDBSecretsFromSecretsManager();
		LambdaResponse lambdaResponse = new LambdaResponse();
		
		if(Objects.isNull(input) || Objects.isNull(input.getRecords())
				|| input.getRecords().isEmpty()) {
			lambdaResponse.setResponseCode(400);
			lambdaResponse.setResponseMessage("empty record received");
			return lambdaResponse;
		}
		
		if(Objects.isNull(input.getRecords().get(0).getS3())
				|| Objects.isNull(input.getRecords().get(0).getS3().getBucket())) {
			lambdaResponse.setResponseCode(400);
			lambdaResponse.setResponseMessage("empty bucket name received");
			return lambdaResponse;
		}
		
		if(Objects.isNull(input.getRecords().get(0).getS3().getObject())
				|| Objects.isNull(input.getRecords().get(0).getS3().getObject().getKey())) {
			lambdaResponse.setResponseCode(400);
			lambdaResponse.setResponseMessage("empty key received");
			return lambdaResponse;
		}
		
		String bucketKey = input.getRecords().get(0).getS3().getObject().getKey();
		String bucketName = input.getRecords().get(0).getS3().getBucket().getName();
		List<CSVData> csvData = Collections.emptyList();
		try {
			logger.log("bucket name:- "+bucketName);
			logger.log("bucket key:- "+bucketKey);
			csvData = connectToAwsS3(bucketName,bucketKey);
		}
		catch(Exception e) {
			logger.log("ERROR: occurred while fetching csv file from aws s3 bucket");
			e.printStackTrace();
			lambdaResponse.setResponseCode(500);
            lambdaResponse.setResponseMessage("Internal Server Error, "+e.getLocalizedMessage());
		}
		
		 try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            System.out.println("MySQL JDBC Driver not found.");
	            logger.log("ERROR occurred while loading driver class");
	            e.printStackTrace();
	            lambdaResponse.setResponseCode(500);
	            lambdaResponse.setResponseMessage("Internal Server Error, "+e.getLocalizedMessage());
	            return lambdaResponse;
	        }
		 
		 logger.log("making database connection for the given url "+secret.getUrl());
		 
		 try (Connection connection = DriverManager.getConnection(secret.getUrl(), secret.getUserName(), secret.getPwd());
	             PreparedStatement statement = connection.prepareStatement("INSERT INTO task_tlb (id,title,summary,due_date,user_id_fk,is_schedule,status) VALUES (?,?,?,?,?,?,?)")) {
			 	
			 	for(CSVData csvData2:csvData) {
			 		statement.setString(1, csvData2.getId());
			 		statement.setString(2, csvData2.getTitle());
			 		statement.setString(3, csvData2.getSummary());
			 		statement.setString(4, csvData2.getDueDate());
			 		statement.setString(5, csvData2.getUserId());
			 		statement.setString(6, csvData2.getIsSchedule());
			 		statement.setString(7, csvData2.getStatus());
			 		statement.addBatch();
			 	}
			 	int[] result = statement.executeBatch();
	            logger.log("Inserted rows: " + result.length);
	            lambdaResponse.setResponseCode(200);
	            lambdaResponse.setResponseMessage("Success");
	            logger.log("Success");
	            
	            logger.log("sending notification to SNS ...");
	            if(result.length !=0) {
	            	sendNotification(secret.getSnsTopic(),result.length, bucketName,bucketKey,"easy_task_db","task_tlb");	            	
	            }
	           
	        } 
		 	catch (Exception e) {
	        	 logger.log("ERROR occurred while fetching data from database");
	        	 lambdaResponse.setResponseCode(500);
	             lambdaResponse.setResponseMessage("Internal Server Error, "+e.getLocalizedMessage());
	             e.printStackTrace();
	             return lambdaResponse;
	        }
		return lambdaResponse;
	}
		
	private void sendNotification(String snsTopic,int length, String bucketName, String bucketKey, String dbName, String tableName) {
		
		String symbol = "@";
		String header = MessageTemplate.HEADER.replaceFirst(symbol, bucketKey).replace(symbol, (bucketName+"/"+bucketKey));
		String bodyFileName = MessageTemplate.BODY_FILE_NAME.replace(symbol, bucketKey);
		String bodyDataBaseName = MessageTemplate.BODY_DATABASE_NAME.replace(symbol, dbName);
		String bodyTableName = MessageTemplate.BODY_TABLE_NAME.replace(symbol, tableName);
		String recordInserted = MessageTemplate.BODY_RECORD_INSERTED.replace(symbol, String.valueOf(length));
		
		StringBuilder message = new StringBuilder(header).append("\n\n");
		message.append(bodyFileName).append("\n")
		.append(bodyDataBaseName).append("\n")
		.append(bodyTableName).append("\n")
		.append(recordInserted).append("\n");
		
		try (SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            PublishRequest request = PublishRequest.builder()
                    .topicArn(snsTopic)
                    .message(message.toString())
                    .build();

            PublishResponse result = snsClient.publish(request);
            logger.log("SNS message sent. MessageId: " + result.messageId());

        } catch (Exception e) {
        	logger.log("ERROR:- occurred while sending notification to AWS SNS Service");
            e.printStackTrace();
            throw e;
        }
	}

	private SecretProperties retrieveDBSecretsFromSecretsManager() {
		 
		String secretName = "keys";
		 Region region = Region.of("us-east-1");
		    
		    SecretsManagerClient client = SecretsManagerClient.builder()
		            .region(region)
		            .build();

		    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
		            .secretId(secretName)
		            .build();

		    GetSecretValueResponse getSecretValueResponse;

		    try {
		        getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
		    } catch (Exception e) {
		        throw e;
		    }

		    String secret = getSecretValueResponse.secretString();
		    logger.log("secret "+secret);
		    Map<String,String> secretMap = gson.fromJson(secret, Map.class);
	        String dbUser = secretMap.get("db_user");
	        String dbUrl = secretMap.get("db_url");
	        String dbPwd = secretMap.get("db_pwd");
	        String snsTopic = secretMap.get("sns_topic");

	        logger.log("dbUser: " + dbUser);
	        logger.log("dbUrl: " + dbUrl);
	        logger.log("snsTopic: " + snsTopic);
		    
		  
	        SecretProperties properties = new SecretProperties();
	        properties.setUserName(dbUser);
	        properties.setPwd(dbPwd);
	        properties.setUrl(dbUrl);
	        properties.setSnsTopic(snsTopic);
		   return properties;
	}
	
	private List<CSVData> connectToAwsS3(String bucketName,String bucketKey) throws Exception {
		
		S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // replace with your bucket's region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(bucketKey)
                .build();
        
        List<CSVData> csvData = new ArrayList<>();

        try(ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        		BufferedReader br = new BufferedReader(new InputStreamReader(s3Object))){
        		
        		String line;
        		while((line = br.readLine()) !=null ) {
        			System.out.println("CSV Line: " + line);
        			
        			if(!line.startsWith("title") && !line.startsWith("Title")) {
        				CSVData csvData2 = new CSVData();
        				String[] str = line.split(",");
        				csvData2.setId(UUID.randomUUID().toString());
        				csvData2.setTitle(str[0]);
        				csvData2.setSummary(str[1]);
        				csvData2.setDueDate(str[2]);
        				csvData2.setUserId(str[3]);
        				csvData2.setIsSchedule(str[4]);
        				csvData2.setStatus(str[5]);
        				csvData.add(csvData2);
        			}
        		}
        }
        catch (Exception e) {
			throw e;
		}
		return csvData;
	}

}
