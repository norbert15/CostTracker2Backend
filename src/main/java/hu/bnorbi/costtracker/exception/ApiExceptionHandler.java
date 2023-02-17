package hu.bnorbi.costtracker.exception;

import hu.bnorbi.costtracker.dto.exception.ApiFault;
import hu.bnorbi.costtracker.dto.exception.InvalidFieldsFault;
import hu.bnorbi.costtracker.enums.ErrorCodeType;
import hu.bnorbi.costtracker.util.RestUtil;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiException e) {
        ApiFault apiFault = RestUtil.createBaseFault(ApiFault::new, e);
        apiFault.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiFault, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidDataException.class})
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException e) {
        InvalidFieldsFault invalidFieldsFault = RestUtil.createBaseFaultWithBadRequest(InvalidFieldsFault::new, e);
        invalidFieldsFault.setErrorFields(e.getErrorFields());
        return new ResponseEntity<>(invalidFieldsFault, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        ApiFault apiFault = RestUtil.createBaseFault(ApiFault::new, e);
        apiFault.setStatusCode(HttpStatus.FORBIDDEN.value());
        apiFault.setErrorCode(ErrorCodeType.AUTHENTICATION_FAILED);
        return new ResponseEntity<>(apiFault, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        ApiFault apiFault = RestUtil.createBaseFault(ApiFault::new, e);
        apiFault.setStatusCode(HttpStatus.NOT_FOUND.value());
        apiFault.setErrorCode(ErrorCodeType.ENTITY_NOT_FOUND);
        return new ResponseEntity<>(apiFault, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleNoSuchFieldException(MethodArgumentNotValidException e) {
        InvalidFieldsFault invalidFieldsFault = RestUtil.createBaseFaultWithBadRequest(InvalidFieldsFault::new);
        invalidFieldsFault.setErrorFields(e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
        return new ResponseEntity<>(invalidFieldsFault, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        ApiFault apiFault = RestUtil.createBaseFault(ApiFault::new, e);
        apiFault.setStatusCode(HttpStatus.BAD_REQUEST.value());
        apiFault.setErrorCode(ErrorCodeType.OPERATION_FAILED);
        return new ResponseEntity<>(apiFault, HttpStatus.BAD_REQUEST);
    }


}
