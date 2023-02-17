package hu.bnorbi.costtracker.util;

import hu.bnorbi.costtracker.dto.exception.BaseFault;
import hu.bnorbi.costtracker.enums.ErrorCodeType;
import hu.bnorbi.costtracker.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RestUtil {

    public static <T extends BaseFault> T createBaseFault(Supplier<T> baseFaultSupplier) {
        T baseFault = baseFaultSupplier.get();
        baseFault.setTimestamp(DateUtil.getOffsetDateTimeNowInUTC());
        return baseFault;
    }

    public static <T extends  BaseFault> T createBaseFault(Supplier<T> baseFaultSupplier, BaseException e) {
        T baseFault = createBaseFault(baseFaultSupplier);
        baseFault.setErrorMessage(e.getMessage());
        baseFault.setErrorCode(e.getErrorCode());
        return baseFault;
    }

    public static <T extends  BaseFault> T createBaseFault(Supplier<T> baseFaultSupplier, Exception e) {
        T baseFault = createBaseFault(baseFaultSupplier);
        baseFault.setErrorMessage(e.getMessage());
        return baseFault;
    }

    public static <T extends BaseFault> T createBaseFault(Supplier<T> baseFaultSupplier, Consumer<T> baseFaultConsumer) {
        T baseFault = createBaseFault(baseFaultSupplier);
        baseFaultConsumer.accept(baseFault);
        return baseFault;
    }

    public static <T extends BaseFault> T createBaseFaultWithBadRequest(Supplier<T> baseFaultSupplier, BaseException e) {
        T baseFault = createBaseFault(baseFaultSupplier);
        baseFault.setErrorMessage(e.getMessage());
        baseFault.setErrorCode(ErrorCodeType.INVALID_REQUEST);
        baseFault.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return baseFault;
    }

    public static <T extends BaseFault> T createBaseFaultWithBadRequest(Supplier<T> baseFaultSupplier) {
        T baseFault = createBaseFault(baseFaultSupplier);
        baseFault.setErrorMessage("invalid fields");
        baseFault.setErrorCode(ErrorCodeType.INVALID_REQUEST);
        baseFault.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return baseFault;
    }

}
