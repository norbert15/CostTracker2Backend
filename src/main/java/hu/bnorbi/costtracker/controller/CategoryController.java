package hu.bnorbi.costtracker.controller;

import hu.bnorbi.costtracker.responses.ListResponse;
import hu.bnorbi.costtracker.responses.Response;
import hu.bnorbi.costtracker.dto.category.CategoryDTO;
import hu.bnorbi.costtracker.entity.Category;
import hu.bnorbi.costtracker.service.CategoryService;
import hu.bnorbi.costtracker.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response<List<Category>>> findAllCategoriesByActiveUser() {
        List<Category> categoryList = categoryService.findAllByActiveUser();
        return ResponseEntity.ok(
                Response.<List<Category>>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message("Kategóriák visszaadva")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(categoryList)
                        .build()
        );
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response<Category>> create(@RequestBody @Valid CategoryDTO categoryDTO) {
        Category savedCategory = categoryService.create(categoryDTO);
        return ResponseEntity.ok(
                Response.<Category>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .statusCode(HttpStatus.CREATED.value())
                        .status(HttpStatus.CREATED)
                        .message("Új kategória létrehozva")
                        .data(savedCategory)
                        .build()
        );
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Response<Category>> update(@RequestBody @Valid CategoryDTO categoryDTO, @PathVariable Long id) {
        Category savedCategory = categoryService.update(categoryDTO, id);
        return ResponseEntity.ok(
                Response.<Category>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .data(savedCategory)
                        .message("Kategória sikeresen szerkesztve")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(
                Response.<String>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message(String.format("Az %s azonosítójú kategória törlésre került", id))
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build()
        );
    }
}
