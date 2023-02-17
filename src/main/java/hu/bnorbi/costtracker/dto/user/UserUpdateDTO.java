package hu.bnorbi.costtracker.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateDTO {

    @NotBlank(message = "firstName is required!")
    private String firstName;

    @NotBlank(message = "lastName is required!")
    private String lastName;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "username is required")
    private String username;

}
