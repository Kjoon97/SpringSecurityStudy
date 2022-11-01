package com.kang.security.Controller;

import com.kang.security.config.auth.PrincipalDetails;
import com.kang.security.model.User;
import com.kang.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    //PrincipalDetail 만으로 UserDetails타입, Oauth2User타입을 모두 사용할 수 있다.
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println("user = " + user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")    // @Secured로 간단히 특정 매서드에 권한 부여.
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인 정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")    // @PreAuthorize 로 매소드 실행 전 여러 개의 권한 체크 가능.
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터 정보";
    }

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/login=============");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        //둘다 같은 값 나옴.
        System.out.println("principalDetails = " + principalDetails.getUser());
        System.out.println("userDetails = " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauthLogin")
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/OauthLogin=============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        //둘다 같은 값 나옴
        System.out.println("authentication = " + oAuth2User.getAttributes());
        System.out.println("oauth = " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }
}
