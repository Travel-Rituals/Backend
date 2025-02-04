package travel.travel.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travel.travel.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
