package aws.lamda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

public class EasyTaskSchedulerLambda implements RequestHandler<Void, LambdaResponse> {


	LambdaLogger logger = null;
	
	@Override
	public LambdaResponse handleRequest(Void input, Context context) {

		logger = context.getLogger();
		logger.log("handleRequest is invoked...........\n");
		
		List<String> ids = new ArrayList<>();
		logger.log("retrieving secrets from SecretsManager for db");
		DBProperties db = retrieveDBSecretsFromSecretsManager();
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
		 
		 logger.log("making database connection for the given url "+db.getUrl());
		 
		 try (Connection connection = DriverManager.getConnection(db.getUrl(), db.getUserName(), db.getPwd());
	             PreparedStatement statement = connection.prepareStatement("SELECT id FROM task_tlb WHERE is_schedule = TRUE AND STATUS = 'schedule' AND DATE_FORMAT(due_date, '%Y-%m-%d %H:%i') <= DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i')");
	             ResultSet resultSet = statement.executeQuery()) {

	            while (resultSet.next()) {
	                String id = resultSet.getString(1); // 1st column
	                ids.add(id);
	            }
	            logger.log("data retrieve from database "+ids);
	            lambdaResponse.setResponseCode(200);
	            lambdaResponse.setResponseMessage("Success");
	            logger.log("Success");
	            sendDataToSQS(ids);
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
	
	private void sendDataToSQS(List<String> data) {
		final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/your-account-id/your-queue-name";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

	    final SqsClient sqsClient = SqsClient.builder()
	            .region(software.amazon.awssdk.regions.Region.US_EAST_1)
	            .build();

	        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
	                .queueUrl(QUEUE_URL)
	                .messageBody(gson.toJson(data))
	                .build();

	        logger.log("sending data to SQS "+gson.toJson(data));
	        SendMessageResponse sendMsgResponse = sqsClient.sendMessage(sendMsgRequest);

	        logger.log("data successfully sent to SQS, Message ID: " + sendMsgResponse.messageId());
	
	}
	
	private DBProperties retrieveDBSecretsFromSecretsManager() {
		 
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

		    // Your code goes here.
		   return new DBProperties();
	}

}
