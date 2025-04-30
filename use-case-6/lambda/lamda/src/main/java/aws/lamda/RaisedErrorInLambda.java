package aws.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RaisedErrorInLambda implements RequestHandler<Object, Object> {

	@Override
	public Object handleRequest(Object input, Context context) {
		LambdaLogger logger =  context.getLogger();
		logger.log("invked handledRequest()...........");
		logger.log("input is "+input);
		logger.log("raising exception.............");
		throw new RuntimeException("erron in Lambda");
	}

}
