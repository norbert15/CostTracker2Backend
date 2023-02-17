package hu.bnorbi.costtracker.exception;

import hu.bnorbi.costtracker.enums.ErrorCodeType;

public class BaseException extends RuntimeException {

    private ErrorCodeType errorCode = ErrorCodeType.OPERATION_FAILED;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(ErrorCodeType errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(ErrorCodeType errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCodeType getErrorCode() {
        return errorCode;
    }
}
