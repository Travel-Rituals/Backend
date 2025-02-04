package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;

    private static final String LIKE_COUNT_KEY = "likes:count:";
    private static final String LIKE_USERS_KEY = "likes:post:";

    public boolean toggleLike(Long postId) {
        Plan existingPlan = planRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        // member
        Member member = null;

        String likeUserKey = LIKE_USERS_KEY + postId;
        String likeCountKey = LIKE_COUNT_KEY + postId;

        Boolean isLiked = redisTemplate.opsForSet().isMember(likeUserKey, member.getId());

        if (Boolean.TRUE.equals(isLiked)) {
            // 좋아요 취소
            redisTemplate.opsForSet().remove(likeUserKey, member.getId());
            redisTemplate.opsForValue().decrement(likeCountKey);
            return false;
        }

        // 좋아요 추가
        redisTemplate.opsForSet().add(likeUserKey, member.getId());
        redisTemplate.opsForValue().increment(likeCountKey);
        return true;
    }

    // 좋아요 개수 조회
    public Long getLikeCount(Long postId) {
        Plan existingPlan = planRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        String likeCountKey = LIKE_COUNT_KEY + postId;
        Object count = redisTemplate.opsForValue().get(likeCountKey);
        if (count == null) {
            return 0L;
        }
        return Long.parseLong(count.toString());
    }

}
