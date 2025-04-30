package aws.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class ProcessDLQLambda implements RequestHandler<SNSEvent, Boolean>{

	@Override
	public Boolean handleRequest(SNSEvent input, Context context) {
		LambdaLogger logger =  context.getLogger();
		logger.log("invoked handleRequest()............ ");
		logger.log("input is "+input);
		return true;
	}

}
