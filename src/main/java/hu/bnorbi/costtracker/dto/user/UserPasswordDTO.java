package hu.bnorbi.costtracker.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPasswordDTO {

    @NotBlank(message = "oldPassword is required!")
    private String oldPassword;

    @NotBlank(message = "newPassword is required!")
    private String newPassword;

    @NotBlank(message = "confirmNewPassword is required!")
    private String confirmNewPassword;

}
