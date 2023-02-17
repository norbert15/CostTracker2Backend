package hu.bnorbi.costtracker.dto.user;

import hu.bnorbi.costtracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationDTO {

    @NotBlank(message = "firstName is required!")
    private String firstName;

    @NotBlank(message = "lastName is required!")
    private String lastName;

    @NotBlank(message = "email is required!")
    private String email;

    @NotBlank(message = "username is required!")
    private String username;

    @NotBlank(message = "password is required!")
    private String password;

    @NotBlank(message = "confirmPassword is required!")
    private String confirmPassword;

    public User toEntity() {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);

        return user;
    }
}
