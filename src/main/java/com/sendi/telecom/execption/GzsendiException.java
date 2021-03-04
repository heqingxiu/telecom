package com.sendi.telecom.execption;

/**
 * @author hqx
 */
public class GzsendiException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public GzsendiException(String message){
		super(message);
	}
	
	public GzsendiException(Throwable cause)
	{
		super(cause);
	}
	
	public GzsendiException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
