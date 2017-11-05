package com.innolux.common.base;

public class ResponseBase<T> {
	private String Status;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	private T Message;
	
	public T getMessage() {
		return Message;
	}

	public void setMessage(T Message) {
		this.Message = Message;
	}


	@Override
	public String toString() {
		return " Status: " + Status + " Message: " + Message;
	}
}
