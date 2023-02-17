package hu.bnorbi.costtracker.dto.exception;

import lombok.Data;

import java.util.List;

@Data
public class InvalidFieldsFault extends BaseFault {

    private List<String> errorFields;

}