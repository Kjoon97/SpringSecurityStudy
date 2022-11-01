package com.kang.security.config.oauth;


import com.kang.security.config.auth.PrincipalDetails;
import com.kang.security.model.User;
import com.kang.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    //Oauth2 로그인 완료 후 후처리 - 구글로 받은 userRequest 데이터 후처리.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        System.out.println("ClientRegistration = " + userRequest.getClientRegistration()); //Registration ID로 어떤 Oauth로 로그인했는지 확인 가능(구글,페이스북)
        System.out.println("AccessToken = " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User= super.loadUser(userRequest);
        System.out.println("Attributes = " + oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();   //google
        String providerID = (String) oAuth2User.getAttributes().get("sub");          //google에서의 회원 primary key
        String email = (String) oAuth2User.getAttributes().get("email");
        String username = provider+"_"+providerID;     //google_123212345245245
        String role ="ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity==null){
            userEntity = User.builder()
                    .username(username)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerID)
                    .build();
            userRepository.save(userEntity);
        }

        //회원가입 강제 진행.
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); //이 객체는 Autentication 객체에 들어감.
    }
    //해당 매소드 종료시 @AuthenticationPrincipal 이 생성된다.
}
//구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 구글 로그인 완료 -> code를 OAuth-client가 받음 -> AccessToken 요청하고 받음
//-> userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원프로필 받아준다.
