package hu.bnorbi.costtracker.dto.user;

import hu.bnorbi.costtracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoggedInDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String token;

}
