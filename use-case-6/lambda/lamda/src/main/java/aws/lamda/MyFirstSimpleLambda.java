package aws.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyFirstSimpleLambda implements RequestHandler<Object, Object>{
	
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
    public FinalResponse handleRequest(Object input, Context context) {
        context.getLogger().log("Input is : " + input);
        final ResponseEntity res = new ResponseEntity();
        res.setCode(200);
        res.setDescription("Sucessfully 200 OK");
        res.setMessge("Hello from Lambda!");
        final FinalResponse fr = new FinalResponse();
        fr.body = res;
        fr.statusCode=200;
        context.getLogger().log("finaly response is : " + fr);
        context.getLogger().log("finaly response is : " + gson.toJson(fr));
        return fr;
    }
}

class FinalResponse{
	int statusCode;
	ResponseEntity body;
}

