package hu.bnorbi.costtracker.service;

import hu.bnorbi.costtracker.dto.record.RecordDTO;
import hu.bnorbi.costtracker.dto.record.RecordWithCategoryDTO;
import hu.bnorbi.costtracker.entity.Category;
import hu.bnorbi.costtracker.entity.Record;
import hu.bnorbi.costtracker.entity.User;
import hu.bnorbi.costtracker.exception.ApiException;
import hu.bnorbi.costtracker.exception.InvalidDataException;
import hu.bnorbi.costtracker.repository.CategoryRepository;
import hu.bnorbi.costtracker.repository.RecordRepository;
import hu.bnorbi.costtracker.repository.UserRepository;
import hu.bnorbi.costtracker.util.DateUtil;
import hu.bnorbi.costtracker.util.JwtTokenUtil;
import hu.bnorbi.costtracker.util.Validators;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Rekord szervíz osztály, mely új rekordok felvételére és listázására alkalmas.
 *
 * @author Norbert Balogh
 * @version 1.0
 * @since 2022-06-13
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final Logger LOGGER = LoggerFactory.getLogger(RecordService.class);
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<Record> findAllByTypeAndYear(Long type, Long year) {
        LOGGER.info("Check year is valid");
        if (year < 2020 || year > DateUtil.getOffsetDateTimeNowInUTC().getYear()) {
            LOGGER.info("Not a valid year");
            throw new InvalidDataException("bad year", List.of("The year must be between 2020 and " + DateUtil.getOffsetDateTimeNowInUTC().getYear()));
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        Long userId = loggedInUser.get().getId();

        LOGGER.info("Find all record by userId {} and year {} and type {} - {}", userId, year, type, DateUtil.getOffsetDateTimeNowInUTC());
        List<Record> records = recordRepository.findAllByUserIdAndCategoryTypeAndYear(userId, type, year);
        LOGGER.info("Records was found - " + DateUtil.getOffsetDateTimeNowInUTC());

        return records;
    }

    public List<RecordWithCategoryDTO> findAllByMonthAndTypeWithCategories(String month, Long type) {
        LOGGER.info("Check month is valid");
        if (!DateUtil.isValidDate(month, "yyyy-MM") || !Validators.isValidMonthDate(month)) {
            LOGGER.info("Not a valid month");
            throw new InvalidDataException("bad date format", List.of("Month must be a valid date (dateFormat: yyyy-MM)"));
        }

        if (!List.of(1L, 2L).contains(type)) {
            throw new InvalidDataException("Hibásan megadott kategória tipus", List.of("A kategória tipus kizárólag 1-es vagy 2-es lehet csak!"));
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        Long userId = loggedInUser.get().getId();

        LOGGER.info("Find all category by userId {} and type {} - " + DateUtil.getOffsetDateTimeNowInUTC(), userId, type);
        List<Category> categories = categoryRepository.findByTypeAndUserId(userId, type);

        List<RecordWithCategoryDTO> recordWithCategoryDTOList = new ArrayList<>();

        for (Category category : categories) {
            List<Record> records = recordRepository.findAllByUserIdAndMonthAndCategoryId(userId, month, category.getId());
            recordWithCategoryDTOList.add(
                    new RecordWithCategoryDTO(
                            category,
                            records,
                            records.stream()
                                    .map(Record::getValue)
                                    .reduce(0L, Long::sum)
                    )
            );
         }

        return recordWithCategoryDTOList;
    }

    /**
     * Paraméterben megadott RecordDTO alapján létrehoz egy új rekodott, ha megfelel a következő validálásoknak:
     * - Összes kötelező mező kitöltve
     * - Valid kategória azonositó
     * - A value nem 0-a
     * - Az adott hónap megfelelő
     *
     * @param recordDTO {@code Record} Egy rekordot reprezentáló objektum
     * @return {@code Record} a létrehozott rekord objektum
     */
    public Record create(RecordDTO recordDTO) {
        LOGGER.info("Create a new record -" + DateUtil.getOffsetDateTimeNowInUTC());
        List<String> errorFields = new ArrayList<>();

        LOGGER.info("Check recordDTO validations");
        if (Objects.isNull(recordDTO)) {
            LOGGER.info("recordDTO object was null");
            for (Field field : RecordDTO.class.getDeclaredFields()) {
                errorFields.add(field.getName());
            }
            throw new InvalidDataException("invalid fields", errorFields);
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        LOGGER.info("Check exist category by categoryId");
        if (categoryRepository.findById(recordDTO.getCategoryId()).isEmpty()) {
            LOGGER.info("Category with id {} not exist", recordDTO.getCategoryId());
            errorFields.add(String.format("Category with id %s not exist", recordDTO.getCategoryId()));
        }

        LOGGER.info("Check value is not 0");
        if (recordDTO.getValue() == 0) {
            LOGGER.info("Value is 0");
            errorFields.add("Value cannot be 0!");
        }

        LOGGER.info("Check month is valid");
        if (!DateUtil.isValidDate(recordDTO.getMonth(), "yyyy-MM") || !Validators.isValidMonthDate(recordDTO.getMonth())) {
            LOGGER.info("Not a valid month");
            errorFields.add("Month must be a valid date (dateFormat: yyyy-MM)");
        }

        LOGGER.info("Check any errors");
        if (!errorFields.isEmpty()) {
            LOGGER.info("Return errors!");
            throw new InvalidDataException("invalid fields", errorFields);
        }

        Record record = recordDTO.toEntity();
        record.setUserId(loggedInUser.get().getId());
        LOGGER.info("Save record...");
        Record savedRecord = recordRepository.save(record);
        LOGGER.info("Record was saved - " + DateUtil.getOffsetDateTimeNowInUTC());
        return savedRecord;
    }
}
