package com.minipulse.exception;

public class MiniPulseBadArgumentException extends Exception {
    public MiniPulseBadArgumentException() {
        super();
    }

    public MiniPulseBadArgumentException(String message) {
        super(message);
    }

    public MiniPulseBadArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MiniPulseBadArgumentException(Throwable cause) {
        super(cause);
    }

    protected MiniPulseBadArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
