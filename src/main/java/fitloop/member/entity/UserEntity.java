package fitloop.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Setter
@Getter

@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",
            nullable = false)
    private String username;

    @Column(name = "password",
            nullable = false)
    private String password;

    @Column(name = "birth_date")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",
            nullable = false,
            columnDefinition = "VARCHAR(255) DEFAULT 'SEED'")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership",
            nullable = false,
            columnDefinition = "VARCHAR(255) DEFAULT 'MEMBER'")
    private Membership membership;

    @Column(name = "phone_number",
            nullable = false,
            length = 255)
    private String phoneNumber;

    @Column(name = "email",
            nullable = false,
            unique = true,
            length = 255)
    private String email;

    @Column(name = "login_id",
            nullable = false,
            length = 255)
    private String loginId;

    @Column(name = "provider",
            nullable = false,
            length = 255)
    private String provider;

    @Column(name = "provider_id",
            nullable = false,
            length = 255)
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",
            nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "personal_info",
            nullable = false,
            columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean personalInfo;

    @Builder
    public UserEntity(
            Long id,
            String username,
            String password,
            LocalDate birthday,
            Role role,
            Membership membership,
            String phoneNumber,
            String email,
            String loginId,
            String provider,
            String providerId,
            Boolean personalInfo) {
        this.id = id;
        this.username = Objects.isNull(username) ? "" : username;
        this.password = Objects.isNull(password) ? "" : password;
        this.birthday = birthday;
        this.role = Objects.isNull(role) ? Role.MEMBER : role;
        this.membership = Objects.isNull(membership) ? Membership.SEED : membership;
        this.phoneNumber = Objects.isNull(phoneNumber) ? "" : phoneNumber;
        this.email = Objects.isNull(email) ? "" : email;
        this.loginId = Objects.isNull(loginId) ? "" : loginId;
        this.provider = Objects.isNull(provider) ? "" : provider;
        this.providerId = Objects.isNull(providerId) ? "" : providerId;
        this.personalInfo = Objects.isNull(personalInfo) ? false : personalInfo;
    }
}