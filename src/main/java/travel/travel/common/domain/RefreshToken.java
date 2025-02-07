package travel.travel.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id @GeneratedValue
    private Long id;

    private String refreshToken;

    private LocalDateTime createdAt;

    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }
}
