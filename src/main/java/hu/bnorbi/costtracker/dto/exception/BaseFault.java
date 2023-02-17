package hu.bnorbi.costtracker.dto.exception;

import hu.bnorbi.costtracker.enums.ErrorCodeType;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BaseFault {

    private OffsetDateTime timestamp;

    private int statusCode;

    private String errorMessage;

    private ErrorCodeType errorCode;

}
