package hu.bnorbi.costtracker.service.impl;

import hu.bnorbi.costtracker.dto.user.UserDTO;
import hu.bnorbi.costtracker.dto.user.UserPasswordDTO;
import hu.bnorbi.costtracker.dto.user.UserRegistrationDTO;
import hu.bnorbi.costtracker.dto.user.UserUpdateDTO;
import hu.bnorbi.costtracker.entity.User;
import hu.bnorbi.costtracker.exception.ApiException;
import hu.bnorbi.costtracker.exception.InvalidDataException;
import hu.bnorbi.costtracker.exception.NotFoundException;
import hu.bnorbi.costtracker.repository.UserRepository;
import hu.bnorbi.costtracker.util.JwtTokenUtil;
import hu.bnorbi.costtracker.util.Validators;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * User szervíz osztály, mely új felhasználók felvételére, törlésére és szerkesztésére alkalmas.
 * Az osztály implemeltálja a UserDetailsService, mely során felülirásra kerül a loadUserByUsername.
 *
 * @author Norbert Balogh
 * @version 1.0
 * @since 2022-06-13
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    /**
     * Paraméterben megadott felhasználónév alapján vissza adja a keresett felhasználót,
     * valamint beállitja a felhasználóhoz tartozo jogosultságokat, és a felhasználói előnézetett.
     *
     * @throws {@code InternalAuthenticationServiceException} Nem létező felhasználó esetén
     *
     * @param username {@code String} Keresett felhasználóhoz tartozó felhasználónév
     * @return {@code User} UserDetails objektum
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        LOGGER.info("Find user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            LOGGER.info("User with username {} not found", username);
            throw new InternalAuthenticationServiceException(String.format("User with username %s not found", username));
        }

        LOGGER.info("User was found");
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), Collections.emptyList());
    }

    /**
     * Aktiv bejelentkezett felhasználó adatainak visszaadása
     *
     * @return {@code UserDTO} UserDTO objektum
     */
    public UserDTO loadAuthenticatedUser() {
        LOGGER.info("Get logged in user by token");
        Optional<User> activeUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (activeUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        return activeUser.get().toUserDTO();
    }

    /**
     * Paraméterben megadott UserRegistrationDTO objektum alapján léterhoz egy új felhasználót, ha megfelel
     * a következő validálásoknak:
     * - Összes kötelező mező kitöltve
     * - Valid E-mail cím formátum
     * - A felhasználónév minimum 5 karakterből áll
     * - A felhasználónév nem foglalt
     * - Az E-mail cím nem foglalt
     * - A jelszó egyezőség
     *
     * @param userRegistrationDTO {@code UserRegistrationDTO} regisztrációhoz szükséges adatok
     * @return {@code UserRegistrationDTO} létrehozott felhasználó objektum
     */
    public UserRegistrationDTO create(UserRegistrationDTO userRegistrationDTO) {
        LOGGER.info("Check userRegistrationDTO validations!");
        List<String> errorFields = new ArrayList<>();

        if (Objects.isNull(userRegistrationDTO)) {
            for (Field field : UserRegistrationDTO.class.getDeclaredFields()) {
                errorFields.add(String.format("%s is required", field.getName()));
            }
            throw new InvalidDataException("Fields is required", errorFields);
        }

        if (!Validators.isValidEmailAddress(userRegistrationDTO.getEmail())) {
            errorFields.add("email format is invalid!");
        }

        if (userRegistrationDTO.getUsername().length() < 5) {
            errorFields.add("The username must be at least 5 characters long!");
        }

        if (userRepository.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
            errorFields.add("A felhasználónév már foglalt!");
        }

        if (userRepository.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            errorFields.add("Az email cím már foglalt!");
        }

        if (!Objects.equals(userRegistrationDTO.getPassword(), userRegistrationDTO.getConfirmPassword())) {
            errorFields.add("The passwords do not match!");
        }

        if (!errorFields.isEmpty()) {
            throw new InvalidDataException("Invalid fields", errorFields);
        }
        LOGGER.info("userRegistrationDTO is valid!");
        LOGGER.info("Passwords encryption in progress");

        userRegistrationDTO.setPassword(DigestUtils
                .sha512Hex(JwtTokenUtil.SAUCE + userRegistrationDTO.getPassword()));
        userRegistrationDTO.setConfirmPassword(DigestUtils
                .sha512Hex(JwtTokenUtil.SAUCE + userRegistrationDTO.getConfirmPassword()));

        LOGGER.info("Passwords encryption done");
        LOGGER.info("Save userRegistrationDTO");

        userRepository.save(userRegistrationDTO.toEntity());

        LOGGER.info("userRegistrationDTO saved");

        return userRegistrationDTO;
    }

    /**
     * Paraméterben megadott userUpdateDTO alapján frissiti az aktiv felhasználó adatait. Az aktiv felhasználó a Header-ben
     * lévő token alapján kerül azonosításra.
     *
     * Az adatok frissitésének a követekző validálásoknak kell megfelelniük:
     * - Aktiv felhasználó sikeres azonosítása
     * - Összes kötelező mező kitöltve
     * - Valid E-mail cím formátum
     * - A felhasználónév minimum 5 karakterből áll
     * - A felhasználónév nem foglalt
     * - Az E-mail cím nem foglalt
     * - A jelszó egyezőség
     *
     * @param userUpdateDTO {@code UserUpdateDTO} szerkesztéshez szükséges adatok
     * @return {@code UserUpdateDTO}
     */
    public UserUpdateDTO update(UserUpdateDTO userUpdateDTO) {
        LOGGER.info("Check userUpdateDTO validations");
        List<String> errorFields = new ArrayList<>();

        if (Objects.isNull(userUpdateDTO)) {
            for (Field field : UserUpdateDTO.class.getDeclaredFields()) {
                errorFields.add(String.format("%s is required", field.getName()));
            }
            throw new InvalidDataException("Fields is required", errorFields);
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> activeUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (activeUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        // Felhasználónév hosszának ellenörzése
        if (userUpdateDTO.getUsername().length() < 5) {
            errorFields.add("A felhasználónévnek legalább 5 karakterből kell állnia!");
        }

        // Felhasználónév ellenörzése, hogy foglalt-e már
        if (!Objects.equals(activeUser.get().getUsername(), userUpdateDTO.getUsername())
                && userRepository.findByUsername(userUpdateDTO.getUsername()).isPresent()) {
            errorFields.add("A felhasználónév már foglalt!");
        }

        if (userUpdateDTO.getFirstName().length() < 3) {
            errorFields.add("A keresztnévnek legalább 3 karakterből kell állnia!");
        }

        if (userUpdateDTO.getLastName().length() < 3) {
            errorFields.add("A vezetéknévnek legalább 3 karakterből kell állnia!");
        }

        // E-mail cím validálása
        if (!Validators.isValidEmailAddress(userUpdateDTO.getEmail())) {
            errorFields.add("Az e-mail cím formátum helytelen!");
        }

        // E-mail cím ellenörzése, hogy foglalt-e már
        if (!Objects.equals(activeUser.get().getEmail(), userUpdateDTO.getEmail())
                && userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()) {
            errorFields.add("Az e-mail cím már foglalt!");
        }

        if (!errorFields.isEmpty()) {
            throw new InvalidDataException("invalid fields", errorFields);
        }

        LOGGER.info("userUpdateDTO is valid");
        LOGGER.info("Save userUpdateDTO");
        activeUser.get().setUsername(userUpdateDTO.getUsername());
        activeUser.get().setEmail(userUpdateDTO.getEmail());
        activeUser.get().setLastName(userUpdateDTO.getLastName());
        activeUser.get().setFirstName(userUpdateDTO.getFirstName());
        LOGGER.info("userUpdateDTO was saved");
        return userUpdateDTO;
    }

    /**
     * Paraméterben megadott userPasswordDTO alapján frissite az aktiv felhasználó jelszavát. Az aktiv felhasználó a Header-ben
     * lévő token alapján kerül azonosításra.
     *
     * Az adatok frissitéséhez a követekző validálásoknak kell megfelelniük:
     * - Aktiv felhasználó sikeres azonosítása
     * - Összes kötelező mező kitöltve
     * - Jelszó egyezőségek
     *
     * A jelszavak titkosítása DigestUtils-ban lévő statikus sha512Hex metódussal történik.
     *
     * @param userPasswordDTO {@code UserPasswordDTO} a szerkesztéshez szükséges jelszavak
     * @return {@code UserPasswordDTO} vissza adja a változtatott jelszavakat.
     */
    public UserPasswordDTO updatePassword(UserPasswordDTO userPasswordDTO) {
        LOGGER.info("Check userPasswordDTO validations");
        List<String> errorFields = new ArrayList<>();

        if (Objects.isNull(userPasswordDTO)) {
            for (Field field : UserPasswordDTO.class.getDeclaredFields()) {
                errorFields.add(String.format("%s is required", field.getName()));
            }
            throw new InvalidDataException("Fields is required", errorFields);
        }

        LOGGER.info("Get logged in user by token");
        Optional<User> activeUser = userRepository.findByUsername(JwtTokenUtil.getUsernameByToken());

        if (activeUser.isEmpty()) {
            throw new ApiException("Failed when search logged in user");
        }

        LOGGER.info("Passwords encryption in progress");
        // Régi jelszó titksitása
        userPasswordDTO.setOldPassword(
                DigestUtils.sha512Hex(JwtTokenUtil.SAUCE + userPasswordDTO.getOldPassword()));

        // Új jelszó titkositása
        userPasswordDTO.setNewPassword(
                DigestUtils.sha512Hex(JwtTokenUtil.SAUCE + userPasswordDTO.getNewPassword())
        );

        // Új jelszó megerősítésének titkosítása
        userPasswordDTO.setConfirmNewPassword(
                DigestUtils.sha512Hex(JwtTokenUtil.SAUCE + userPasswordDTO.getConfirmNewPassword())
        );
        LOGGER.info("Passwords encryption done");

        // Régi jelszó egyezőség ellenörzése
        if (!Objects.equals(userPasswordDTO.getOldPassword(), activeUser.get().getPassword())) {
            errorFields.add("A régi jelszó nem egyezik meg a jelenlegi jelszóval!");
        }

        // Új jelszó egyezőség ellenörzése
        if (!Objects.equals(userPasswordDTO.getNewPassword(), userPasswordDTO.getConfirmNewPassword())) {
            errorFields.add("The passwords do not match!");
        }

        if (!errorFields.isEmpty()) {
            throw new InvalidDataException("invalid fields", errorFields);
        }

        LOGGER.info("userPasswordDTO is valid");
        LOGGER.info("Update password");
        activeUser.get().setPassword(userPasswordDTO.getNewPassword());
        LOGGER.info("Password was updated");

        return userPasswordDTO;
    }
}