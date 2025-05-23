package fitloop.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true, length = 512)
    private String refresh;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    private LocalDateTime firstIssuedAt;

    @Column(nullable = false)
    private LocalDateTime lastReissuedAt;

    public static RefreshEntity createRenewed(String username, String refresh, LocalDateTime expiration, LocalDateTime originalFirstIssuedAt) {
        return RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(expiration)
                .firstIssuedAt(originalFirstIssuedAt)
                .lastReissuedAt(LocalDateTime.now())
                .build();
    }
}
