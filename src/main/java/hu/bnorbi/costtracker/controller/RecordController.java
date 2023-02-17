package hu.bnorbi.costtracker.controller;

import hu.bnorbi.costtracker.dto.record.RecordDTO;
import hu.bnorbi.costtracker.dto.record.RecordWithCategoryDTO;
import hu.bnorbi.costtracker.entity.Record;
import hu.bnorbi.costtracker.responses.Response;
import hu.bnorbi.costtracker.service.RecordService;
import hu.bnorbi.costtracker.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/records")
public class RecordController {

    private final RecordService recordService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response<Record>> create(@RequestBody @Valid RecordDTO recordDTO) {
        Record record = recordService.create(recordDTO);

        return ResponseEntity.ok(
                Response.<Record>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message("Új rekord rögzítve")
                        .data(record)
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @RequestMapping(path = "/month/{month}", method = RequestMethod.GET)
    public ResponseEntity<Response<List<RecordWithCategoryDTO>>> findAllByPeriodAndType(
            @PathVariable String month,
            @RequestParam(name = "type") Long type
    ) {
        List<RecordWithCategoryDTO> recordWithCategoryDTOList = recordService.findAllByMonthAndTypeWithCategories(month, type);
        return ResponseEntity.ok(
                Response.<List<RecordWithCategoryDTO>>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message("Rekordok kategóriákba csoportositva visszaadva!")
                        .data(recordWithCategoryDTOList)
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @RequestMapping(path = "/year/{year}", method = RequestMethod.GET)
    public ResponseEntity<Response<List<Record>>> findAllByYearAndType(
            @PathVariable Long year,
            @RequestParam(name = "type") Long type
    ) {
        List<Record> records = recordService.findAllByTypeAndYear(type, year);
        return ResponseEntity.ok(
                Response.<List<Record>>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message("Rekordok visszaadva")
                        .data(records)
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
}
