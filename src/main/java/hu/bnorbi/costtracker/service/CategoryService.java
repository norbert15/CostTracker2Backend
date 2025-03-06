package hu.bnorbi.costtracker.service;

import hu.bnorbi.costtracker.dto.category.CategoryDTO;
import hu.bnorbi.costtracker.entity.Category;
import hu.bnorbi.costtracker.entity.User;
import hu.bnorbi.costtracker.exception.ApiException;
import hu.bnorbi.costtracker.exception.InvalidDataException;
import hu.bnorbi.costtracker.exception.NotFoundException;
import hu.bnorbi.costtracker.repository.CategoryRepository;
import hu.bnorbi.costtracker.repository.UserRepository;
import hu.bnorbi.costtracker.util.DateUtil;
import hu.bnorbi.costtracker.util.JwtTokenUtil;
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
 * Kategória szervíz osztály, mely új kategóriák felvételére, törlésére és listázásra alkalmas.
 *
 * @author Norbert Balogh
 * @version 1.0
 * @since 2022-06-13
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Egy Category listát ad vissza az alapértelmezett kategóriákkal
     *
     * @return {@code List<Category>} Kategóriák listája
     */
    private List<Category> findAllDefault() {
        LOGGER.info("Find all default categories - " + DateUtil.getOffsetDateTimeNowInUTC());
        List<Category> categoryList = categoryRepository.findAllByUserId(0L);
        LOGGER.info("Categories was found - " + DateUtil.getOffsetDateTimeNowInUTC());
        return categoryList;
    }

    /**
     * Egy Category listát ad vissza az aktív felhasználó számára. Az aktiv felhasználó a Headerben lévő token
     * alapján kerül azonosításra.
     *
     * @return {@code List<Category>} Egy Categry lista
     */
    public List<Category> findAllByActiveUser() {
        LOGGER.info("Get logged in user by token");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        LOGGER.info("Find all categories by user id - " + DateUtil.getOffsetDateTimeNowInUTC());
        List<Category> categoryList = categoryRepository.findAllByUserId(loggedInUser.get().getId());
        LOGGER.info("Categories was found - " + DateUtil.getOffsetDateTimeNowInUTC());
        return categoryList;
    }

    /**
     * Paraméterben megadott categoryDTO alapján létrehoz egy új kategóriát, ha megfelel a következő validálásoknak:
     * - Összes kötelező mező kitöltve
     * - Bejelentkezet felhasználó ellenörzése
     *
     * @param categoryDTO {@code CategoryDTO} mely egy kategória objektumot reprezentál
     * @return {@code Category} A létrehozott kategória objektum
     */
    public Category create(CategoryDTO categoryDTO) {
        LOGGER.info("Új kategória létrehozása - " + DateUtil.getOffsetDateTimeNowInUTC());
        LOGGER.info("Kategória validálások folyamatban..");

        if (Objects.isNull(categoryDTO)) {
            List<String> errorFields = new ArrayList<>();
            for (Field field : Category.class.getDeclaredFields()) {
                errorFields.add(String.format("A(z) %s megadása kötelező!", field.getName()));
            }

            throw new InvalidDataException("Hibásan kitöltött mezők!", errorFields);
        }

        if (categoryDTO.getType() < 1 || categoryDTO.getType() > 2) {
            throw new InvalidDataException("Hibásan kitöltött mezők!", List.of("A kategória tipusa 1 és 2 lehet!"));
        }

        LOGGER.info("Aktív felhasználó ellenörzése");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Nem található aktív felhasználó!");
        }

        Category category = categoryDTO.toEntity();
        category.setUserId(loggedInUser.get().getId());

        LOGGER.info("Kategória mentése");
        Category savedCategory = categoryRepository.save(category);
        LOGGER.info("Kategória sikeresen mentve - " + DateUtil.getOffsetDateTimeNowInUTC());

        return savedCategory;
    }

    public Category update(CategoryDTO categoryDTO, Long id) {
        LOGGER.info("{}. Kategória szerkesztése - {}", id, DateUtil.getOffsetDateTimeNowInUTC());
        LOGGER.info("Kategória validálások folyamatban..");

        if (isDefaultCategory(id)) {
            throw new InvalidDataException("Helytelen azonosító", List.of("Alapértelmezett kategóriát nem lehet szerkeszteni"));
        }

        if (Objects.isNull(categoryDTO)) {
            List<String> errorFields = new ArrayList<>();
            for (Field field : Category.class.getDeclaredFields()) {
                errorFields.add(String.format("A(z) %s megadása kötelező!", field.getName()));
            }

            throw new InvalidDataException("Hibásan kitöltött mezők!", errorFields);
        }

        Optional<Category> findCategory = categoryRepository.findById(id);

        if (findCategory.isEmpty()) {
            throw new NotFoundException("Nem található {}-os id-val rendelkező kategória");
        }

        if (categoryDTO.getType() < 1 || categoryDTO.getType() > 2) {
            throw new InvalidDataException("Hibásan kitöltött mezők!", List.of("A kategória tipusa 1 és 2 lehet!"));
        }

        LOGGER.info("Aktív felhasználó ellenörzése");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Nem található aktív felhasználó!");
        }

        LOGGER.info("Kategória mentése");
        findCategory.get().setIcon(categoryDTO.getIcon());
        findCategory.get().setName(categoryDTO.getName());
        findCategory.get().setColor(categoryDTO.getColor());
        findCategory.get().setType(categoryDTO.getType());
        LOGGER.info("Kategória sikeresen mentve - " + DateUtil.getOffsetDateTimeNowInUTC());

        return categoryDTO.toEntity();
    }

    /**
     * Paraméterben megadott kategória azonosító alapján eltávolitja az aktiv felhasználóhoz tartozó kategiriát.
     *
     * @param id {@code Long} kategória azonosítója
     */
    public void delete(Long id) {
        LOGGER.info("Delete category - " + DateUtil.getOffsetDateTimeNowInUTC());
        if (isDefaultCategory(id)) {
            throw new InvalidDataException("Invalid data", List.of("Category id cannot be default category!"));
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> loggedInUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (loggedInUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        Long userId = loggedInUser.get().getId();

        LOGGER.info("Find category with id {} and userId {}", id, userId);
        if (categoryRepository.findByIdAndUserId(id, userId).isEmpty()) {
            throw new NotFoundException(String.format("Category with id %s and userId %s not found", id, userId));
        }

        categoryRepository.deleteById(id);
        LOGGER.info("Category was deleted - " + DateUtil.getOffsetDateTimeNowInUTC());
    }

    /**
     * Ellenörzi, hogy a paraméterben megadott kategoria id egy alapértelmezett kategóriához tartozik-e
     *
     * @param id {@code Long} kategória azonositó
     * @return
     *  {@code true} abban az esetben ha az id egy alapértelmezett kategória id-ja
     *  {@code false} máskülönben
     */
    private boolean isDefaultCategory(Long id) {
        List<Category> categoryList = findAllDefault();

        LOGGER.info(String.format("Check id is default category %s", id));
        for (Category category : categoryList) {
            if (Objects.equals(category.getId(), id)) {
                LOGGER.info(String.format("Category with id %s is default", id));
                return true;
            }
        }
        return false;
    }
}
