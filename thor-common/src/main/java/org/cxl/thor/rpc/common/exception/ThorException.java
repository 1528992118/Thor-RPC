package org.cxl.thor.rpc.common.exception;

public class ThorException extends RuntimeException{

    public static final int NOT_FOUNT_SERVICE = 0;
    public static final int JDK_REFLECT_ERROR = 1;
    public static final int NO_PROVIDER_ERROR = 2;

    private int code;

    public ThorException() {
        super();
    }

    public ThorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThorException(String message) {
        super(message);
    }

    public ThorException(Throwable cause) {
        super(cause);
    }

    public ThorException(int code) {
        super();
        this.code = code;
    }

    public ThorException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ThorException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ThorException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

}
