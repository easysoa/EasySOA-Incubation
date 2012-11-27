package org.easysoa.registry;

import org.nuxeo.ecm.core.api.RecoverableClientException;

public class ModelIntegrityClientException extends RecoverableClientException {

	public ModelIntegrityClientException(String message, Throwable cause) {
		super(message, message, null, cause);
	}

	public ModelIntegrityClientException(String message) {
		super(message, message, null);
	}

	private static final long serialVersionUID = 1L;

}
