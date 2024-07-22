package com.minipulse.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
}
