package fitloop.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "birth_date")
    private LocalDate birthday;

    @Column(name = "full_name")
    private String name;

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

            length = 255)
    private String phoneNumber;

    @Column(name = "email",
            nullable = false,
            unique = true,
            length = 255)
    private String email;

    @Column(name = "provider",

            length = 255)
    private String provider;

    @Column(name = "provider_id",

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

    @PrePersist
    public void prePersist() {
        this.role = (this.role == null) ? Role.MEMBER : this.role;
        this.membership = (this.membership == null) ? Membership.SEED : this.membership;
        this.personalInfo = (this.personalInfo == null) ? false : this.personalInfo;
    }
}