package com.easy.task;

public class Response<T> {
	
	private int responseCode;
	private T reponseBody;
	private int scheduleTask;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public T getReponseBody() {
		return reponseBody;
	}
	public void setReponseBody(T reponseBody) {
		this.reponseBody = reponseBody;
	}
	public int getScheduleTask() {
		return scheduleTask;
	}
	public void setScheduleTask(int scheduleTask) {
		this.scheduleTask = scheduleTask;
	}

}
