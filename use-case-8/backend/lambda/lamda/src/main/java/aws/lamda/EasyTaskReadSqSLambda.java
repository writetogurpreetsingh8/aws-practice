package aws.lamda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * This lambda receive event from SQS
 */
public class EasyTaskReadSqSLambda implements RequestHandler<SQSEvent, LambdaResponse> {


	LambdaLogger logger = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public LambdaResponse handleRequest(SQSEvent input, Context context) {

		logger = context.getLogger();
		logger.log("handleRequest is invoked...........\n");
		
		SecretProperties db = retrieveDBSecretsFromSecretsManager();
		LambdaResponse lambdaResponse = new LambdaResponse();
		logger.log("sqs data "+gson.toJson(input.getRecords()));
		
		if(Objects.isNull(input) || Objects.isNull(input.getRecords()) 
				|| input.getRecords().isEmpty()) {
			
			lambdaResponse.setResponseCode(400);
			lambdaResponse.setResponseMessage("empty record received");
			return lambdaResponse;
		}
		if(Objects.isNull(input.getRecords().get(0).getBody())) {
			lambdaResponse.setResponseCode(400);
			lambdaResponse.setResponseMessage("empty body received");
			return lambdaResponse;
		}
		
		List<String> list = gson.fromJson(input.getRecords().get(0).getBody(), List.class);
		logger.log("list is "+list);
		
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
		 
		 logger.log("making database connection for the given url "+db.getUrl());
		 StringBuilder updateQuery = new StringBuilder("UPDATE task_tlb SET STATUS = 'done' WHERE id IN ( ");
		 list.forEach(v -> {
			 updateQuery.append("'").append(v).append("'").append(",");
		 });
		 updateQuery.append("'0')");
		 logger.log("final query:- "+updateQuery.toString());
		 try (Connection connection = DriverManager.getConnection(db.getUrl(), db.getUserName(), db.getPwd());
	             PreparedStatement statement = connection.prepareStatement(updateQuery.toString())) {
			 	
			 	int result = statement.executeUpdate();
	            lambdaResponse.setResponseCode(200);
	            lambdaResponse.setResponseMessage(result+" number of row(s) successfully updated!");
	            logger.log("Success");
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
		        // For a list of exceptions thrown, see
		        // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
		        throw e;
		    }

		    String secret = getSecretValueResponse.secretString();
		    logger.log("secret "+secret);
		    Map<String,String> secretMap = gson.fromJson(secret, Map.class);
	        String dbUser = secretMap.get("db_user");
	        String dbUrl = secretMap.get("db_url");
	        String dbPwd = secretMap.get("db_pwd");

	        logger.log("dbUser: " + dbUser);
	        logger.log("dbUrl: " + dbUrl);
		    
		  
	        SecretProperties dbProperties = new SecretProperties();
	        dbProperties.setUserName(dbUser);
	        dbProperties.setPwd(dbPwd);
	        dbProperties.setUrl(dbUrl);
		    // Your code goes here.
		   return dbProperties;
	}
}
