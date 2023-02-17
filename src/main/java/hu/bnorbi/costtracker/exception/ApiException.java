package hu.bnorbi.costtracker.exception;

public class ApiException extends BaseException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
