package hu.bnorbi.costtracker.dto.record;

import hu.bnorbi.costtracker.entity.Category;
import hu.bnorbi.costtracker.entity.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordWithCategoryDTO {

    private Category category;

    private List<Record> records;

    private Long sum;
}
