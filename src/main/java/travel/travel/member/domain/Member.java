package travel.travel.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    private Long id;

    public Member(Long kakaoId) {
        this.id = kakaoId;
    }
}
