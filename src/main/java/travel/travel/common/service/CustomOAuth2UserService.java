package travel.travel.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService  implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 카카오 사용자 정보 가져오기
        Map<String, Object> attributes = oauth2User.getAttributes();
        Long kakaoId = (Long) attributes.get("id");
        log.info("kakao {}" , kakaoId);
        Member member = save(kakaoId);

        // JWT 토큰 발급
        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()));
        return oauth2User;
    }

    private Member save(Long kakaoId) {
        Member member = memberRepository.findById(kakaoId)
                .orElse(memberRepository.save(new Member(kakaoId)));
        log.info("member: {}", member);

        return member;
    }

}