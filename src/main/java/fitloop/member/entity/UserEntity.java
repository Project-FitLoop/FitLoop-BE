package fitloop.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter

@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            columnDefinition = "VARCHAR(255) DEFAULT 'SEED'")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "membership",
            nullable = false,
            columnDefinition = "VARCHAR(255) DEFAULT 'MEMBER'")
    private Membership membership;

    @Column(name = "phone_number", nullable = false, length = 255)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "login_id", length = 255)
    private String loginId;

    @Column(name = "provider", length = 255)
    private String provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}