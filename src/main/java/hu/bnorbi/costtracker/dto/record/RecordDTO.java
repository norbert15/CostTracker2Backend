package hu.bnorbi.costtracker.dto.record;

import hu.bnorbi.costtracker.entity.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {

    @NotNull(message = "categoryId is required!")
    private Long categoryId;

    @NotNull(message = "value is required!")
    @Min(value = -1000000000, message = "the minimum allowed value is -1000000000!")
    @Max(value = 1000000000, message = "the maximum allowed value is -1000000000!")
    private Long value;

    @NotBlank(message = "month is required!")
    private String month;

    private String comment;

    public Record toEntity() {
        Record record = new Record();
        record.setCategoryId(categoryId);
        record.setValue(value);
        record.setMonth(month);
        record.setComment(comment);

        return record;
    }
}
