package hu.bnorbi.costtracker.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    protected OffsetDateTime timeStamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String message;
    protected T data;
}