package aws.lamda;

public interface MessageTemplate {
	
	String HEADER = "Your uploaded CSV file **@** has been processed successfully, from respective S3 Bucket:- 	@";
	String FOOTER = "Thank you for using our system. No further action is required at this time";
	String BODY_FILE_NAME = "📄 File Name		:- 	@";
	String BODY_DATABASE_NAME = "🗄️ Database Name	:- 	@";
	String BODY_TABLE_NAME = "📋 Table Name		:- 	@";
	String BODY_RECORD_INSERTED = "✅ Records Inserted	:- 	@";

}
