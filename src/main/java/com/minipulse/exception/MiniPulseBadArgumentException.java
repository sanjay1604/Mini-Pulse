package com.minipulse.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class MiniPulseBadArgumentException extends WebApplicationException {
    public MiniPulseBadArgumentException() {
        super();
    }

    public MiniPulseBadArgumentException(String message) {
        super(message, Response.status(400).build());
    }

    public MiniPulseBadArgumentException(String message, Throwable cause) {
        super(message, cause, Response.status(400).build());
    }

    public MiniPulseBadArgumentException(Throwable cause) {
        super(cause, Response.status(400).build());
    }
}
