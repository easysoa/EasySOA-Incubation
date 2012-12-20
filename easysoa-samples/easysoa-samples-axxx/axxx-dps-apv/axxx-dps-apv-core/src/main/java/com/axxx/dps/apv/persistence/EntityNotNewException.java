package com.axxx.dps.apv.persistence;

public class EntityNotNewException extends RuntimeException {

    private static final long serialVersionUID = -7621865071891108900L;

    public EntityNotNewException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotNewException(String message) {
        super(message);
    }

}
