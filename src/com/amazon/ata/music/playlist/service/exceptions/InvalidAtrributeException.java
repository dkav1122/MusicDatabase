package com.amazon.ata.music.playlist.service.exceptions;

public class InvalidAtrributeException extends RuntimeException {
    private static final long serialVersionUID = -2640817734410724500L;

    /**
     * Exception with no message or cause.
     */
    public InvalidAtrributeException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public InvalidAtrributeException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public InvalidAtrributeException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public InvalidAtrributeException(String message, Throwable cause) {
        super(message, cause);
    }


}
