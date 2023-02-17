package hu.bnorbi.costtracker.entity;

import hu.bnorbi.costtracker.dto.user.UserDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 5, max = 15)
    @Column(unique = true, length = 15)
    private String username;

    @NotBlank
    private String password;

    public UserDTO toUserDTO() {
        UserDTO userDTODTO = new UserDTO();
        userDTODTO.setUsername(username);
        userDTODTO.setEmail(email);
        userDTODTO.setFirstName(firstName);
        userDTODTO.setLastName(lastName);
        userDTODTO.setId(id);

        return userDTODTO;
    }
}
