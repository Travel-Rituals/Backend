package travel.travel.plan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlanService{
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final DestinationRepository destinationRepository;

    public PlanResDto planCreate(@Valid PlanCreateReqDto planCreateReqDto) {
        // member
        Member member = null;

        Destination destination = destinationRepository.findByDestinationName(planCreateReqDto.getDestinationName())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 장소입니다."));

        if (planCreateReqDto.getStartDate().isAfter(planCreateReqDto.getEndDate())) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        Plan savedPlan = planRepository.save(planCreateReqDto.toEntity(member, destination));
        return savedPlan.fromEntity();
    }


    public PlanResDto planRead(Long postId) {
        // member
        Member member = null;

        Plan existingPlan = planRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        return existingPlan.fromEntity();
    }

    public List<PlanResDto> planReadList() {
        // member
        Member member = null;

        List<PlanResDto> plans = planRepository.findAll().stream()
                .map(Plan::fromEntity)
                .collect(Collectors.toList());
        return plans;
    }

    public PlanResDto planUpdate(Long postId, @Valid PlanUpdateReqDto planUpdateReqDto) {
        // member
        Member member = null;

        Plan existingPlan = planRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        existingPlan.updatePlan(planUpdateReqDto.toEntity(member));
        Plan savedPlan = planRepository.save(existingPlan);

        return savedPlan.fromEntity();

    }

    public PlanResDto planDelete(Long postId) {
        // member
        Member member = null;

        Plan existingPlan = planRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        planRepository.delete(existingPlan);
        return existingPlan.fromEntity();
    }
}
