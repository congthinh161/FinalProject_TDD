package Model;

import Model.Enum.UserRole;
import lombok.*;

@Data
@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String address;
    private String phone;
    private UserRole role;

    public User(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}


