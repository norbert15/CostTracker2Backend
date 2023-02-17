package hu.bnorbi.costtracker.controller;

import hu.bnorbi.costtracker.dto.user.UserDTO;
import hu.bnorbi.costtracker.dto.user.UserPasswordDTO;
import hu.bnorbi.costtracker.dto.user.UserRegistrationDTO;
import hu.bnorbi.costtracker.dto.user.UserUpdateDTO;
import hu.bnorbi.costtracker.responses.Response;
import hu.bnorbi.costtracker.service.impl.UserServiceImpl;
import hu.bnorbi.costtracker.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response<UserRegistrationDTO>> create(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) throws NoSuchFieldException, IllegalAccessException {
        UserRegistrationDTO savedUser = userServiceImpl.create(userRegistrationDTO);
        return ResponseEntity.ok(
                Response.<UserRegistrationDTO>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .data(savedUser)
                        .message("Új felhasználó létrehozva!")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @RequestMapping(path = "/active", method = RequestMethod.GET)
    public ResponseEntity<Response<UserDTO>> getLoggedInUser() {
        UserDTO userDTO = userServiceImpl.loadAuthenticatedUser();
        return ResponseEntity.ok(
                Response.<UserDTO>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .data(userDTO)
                        .message("Bejelentkezett felhasználó visszaadva")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @RequestMapping(path = "/profile", method = RequestMethod.PUT)
    public ResponseEntity<Response<UserUpdateDTO>> updateProfile(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserUpdateDTO savedUserData = userServiceImpl.update(userUpdateDTO);
        return ResponseEntity.ok(
                Response.<UserUpdateDTO>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .data(savedUserData)
                        .message("Felhasználó adatai szerkesztve!")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @RequestMapping(path = "/password", method = RequestMethod.PUT)
    public ResponseEntity<Response<String>> updatePassword(@RequestBody @Valid UserPasswordDTO userPasswordDTO) {
        UserPasswordDTO savedUserPassword = userServiceImpl.updatePassword(userPasswordDTO);
        return ResponseEntity.ok(
                Response.<String>builder()
                        .timeStamp(DateUtil.getOffsetDateTimeNowInUTC())
                        .message("Jelszó módosítva!")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build()
        );
    }
}
