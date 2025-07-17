import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImage;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    // Optional: For Spring Security's authority mapping
    public Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return java.util.List.of(() -> "ROLE_" + role.name());
    }
}