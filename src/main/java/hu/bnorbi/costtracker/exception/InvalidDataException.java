package hu.bnorbi.costtracker.exception;

import java.util.ArrayList;
import java.util.List;

public class InvalidDataException extends BaseException {

    private List<String> errorFields = new ArrayList<>();

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, List<String> errorFields) {
        super(message);
        this.errorFields = errorFields;
    }

    public List<String> getErrorFields() {
        return errorFields;
    }
}
