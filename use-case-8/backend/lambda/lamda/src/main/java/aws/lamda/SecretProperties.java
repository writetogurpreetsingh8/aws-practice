package aws.lamda;

public class SecretProperties {

	private String url;
	private String userName;
	private String pwd;
	private String dbName;
	private String sqsUrl;
	private String snsTopic;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getSqsUrl() {
		return sqsUrl;
	}
	public void setSqsUrl(String sqsUrl) {
		this.sqsUrl = sqsUrl;
	}
	public String getSnsTopic() {
		return snsTopic;
	}
	public void setSnsTopic(String snsTopic) {
		this.snsTopic = snsTopic;
	}
}
