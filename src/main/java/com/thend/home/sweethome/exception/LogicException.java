package com.thend.home.sweethome.exception;

public class LogicException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private LogicExpStatus status;
	
	public LogicException(LogicExpStatus status) {
		super(status.name());
		this.status = status;
	}
	
	public LogicException(LogicExpStatus status, Throwable e) {
		super(status.name(), e);
		this.status = status;
	}
	
	public int getErrorCode() {
		return status.getErrorCode();
	}

	public static enum LogicExpStatus {
		BAD_REQUEST(400),
		SERVER_ERROR(500),
		LACK_OF_MONEY(10001);
		private LogicExpStatus(int errorCode) {
			this.errorCode = errorCode;
		}
		private int errorCode;
		public int getErrorCode() {
			return errorCode;
		}
	}

}
