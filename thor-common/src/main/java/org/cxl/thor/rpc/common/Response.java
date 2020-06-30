package org.cxl.thor.rpc.common;

import java.io.Serializable;

public class Response implements Serializable {

    private static final long serialVersionUID = 6313260401594053637L;

    private String requestId;

    private Status status;

    private String message;

    private Object result;

    public Response(Status status) {
        this.status = status;
    }

    public Response(String requestId, Status status) {
        this.requestId = requestId;
        this.status = status;
    }

    public Response(String requestId, Status status, Object result) {
        this.requestId = requestId;
        this.status = status;
        this.result = result;
    }


    public Response(String requestId, Status status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }

    public Response(String requestId, Status status, String message, Object result) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
