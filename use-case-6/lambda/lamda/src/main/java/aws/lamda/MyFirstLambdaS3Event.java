package aws.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyFirstLambdaS3Event implements RequestHandler<S3Event, ResponseEntity> {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public ResponseEntity handleRequest(S3Event input, Context context) {
		System.out.println("handleRequest is invoked...........");

		final LambdaLogger logger = context.getLogger();

		logger.log("from logger -> handleRequest is invoked...........\n");
		logger.log("getAwsRequestId " + context.getAwsRequestId() + "\n");
		logger.log("getFunctionName " + context.getFunctionName() + "\n");
		logger.log("getFunctionVersion " + context.getFunctionVersion() + "\n");
		logger.log("getInvokedFunctionArn " + context.getInvokedFunctionArn() + "\n");
		logger.log("getLogGroupName " + context.getLogGroupName() + "\n");
		logger.log("getLogStreamName " + context.getLogStreamName() + "\n");
		logger.log("getAwsRequestId " + context.getAwsRequestId() + "\n");
		logger.log("getMemoryLimitInMB " + context.getMemoryLimitInMB() + "\n");
		logger.log("getRemainingTimeInMillis " + context.getRemainingTimeInMillis() + "\n");

		logger.log("getClientContext " + gson.toJson(context.getClientContext()) + "\n");
		logger.log("context " + gson.toJson(context) + "\n");
		logger.log("getIdentity " + gson.toJson(context.getIdentity()) + "\n");
		ResponseEntity response = new ResponseEntity();
		response.setCode(200);
		logger.log("dumping request " + input + "\n");
		logger.log("request body "+input);
		logger.log("s3 event is "+gson.toJson(input));
		logger.log("get-records "+input.getRecords());
		
				logger.log("response is " + gson.toJson(response) + "\n");

		return response;
	}

}
