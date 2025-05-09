package aws.lamda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/**
 * This lambda is responsible to retrieve data from database
 * and send it to SQS
 */
public class EasyTaskReadRDSLambda implements RequestHandler<Void, LambdaResponse> {


	LambdaLogger logger = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public LambdaResponse handleRequest(Void input, Context context) {

		logger = context.getLogger();
		logger.log("handleRequest is invoked...........\n");
		
		List<String> ids = new ArrayList<>();
		logger.log("retrieving secrets from SecretsManager for db");
		SecretProperties secret = retrieveDBSecretsFromSecretsManager();
		LambdaResponse lambdaResponse = new LambdaResponse();
		
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
	             PreparedStatement statement = connection.prepareStatement("SELECT id FROM task_tlb WHERE is_schedule = true and status='schedule' and STR_TO_DATE(due_date, '%d-%m-%Y %H:%i') <= NOW()");
	             ResultSet resultSet = statement.executeQuery()) {

	            while (resultSet.next()) {
	                String id = resultSet.getString(1); // 1st column
	                ids.add(id);
	            }
	            logger.log("data retrieve from database "+ids);
	            lambdaResponse.setResponseCode(200);
	            lambdaResponse.setResponseMessage("Success");
	            logger.log("Success");
	            if(Objects.nonNull(ids) && !ids.isEmpty()) {
	            	sendDataToSQS(ids,secret.getSqsUrl());
	            }
	        } 
		 	catch (SQLException e) {
	        	 logger.log("ERROR occurred while fetching data from database");
	        	 lambdaResponse.setResponseCode(500);
	             lambdaResponse.setResponseMessage("Internal Server Error, "+e.getLocalizedMessage());
	             e.printStackTrace();
	             return lambdaResponse;
	        }
		return lambdaResponse;
	}

	private void sendDataToSQS(List<String> data, String sqsUrl) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

	    final SqsClient sqsClient = SqsClient.builder()
	            .region(software.amazon.awssdk.regions.Region.US_EAST_1)
	            .build();

	        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
	                .queueUrl(sqsUrl)
	                .messageBody(gson.toJson(data))
	                .messageGroupId("Lambda-DB-Fetch")
	                .messageDeduplicationId(UUID.randomUUID().toString())
	                .build();
	        logger.log("sending data to SQS "+gson.toJson(data));
	        SendMessageResponse sendMsgResponse = sqsClient.sendMessage(sendMsgRequest);
	        logger.log("data successfully sent to SQS, Message ID: " + sendMsgResponse.messageId());
	}
	
	private SecretProperties retrieveDBSecretsFromSecretsManager() {
		 
		String secretName = "keys";
		 Region region = Region.of("us-east-1");
		    
		    // Create a Secrets Manager client
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
	        String sqsUrl = secretMap.get("sqs.fifo_url");

	        logger.log("dbUser: " + dbUser);
	        logger.log("dbUrl: " + dbUrl);
	        logger.log("sqsUrl: " + sqsUrl);
		    
		  
	        SecretProperties properties = new SecretProperties();
	        properties.setUserName(dbUser);
	        properties.setPwd(dbPwd);
	        properties.setUrl(dbUrl);
	        properties.setSqsUrl(sqsUrl);
		    // Your code goes here.
		   return properties;
	}

}
