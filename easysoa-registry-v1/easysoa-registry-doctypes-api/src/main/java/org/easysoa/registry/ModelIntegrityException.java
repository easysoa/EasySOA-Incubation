package org.easysoa.registry;

public class ModelIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ModelIntegrityException() {
		super();
	}
	
	public ModelIntegrityException(String message) {
		super(message);
	}

	public ModelIntegrityException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ModelIntegrityException(Throwable throwable) {
		super(throwable);
	}
	
}
