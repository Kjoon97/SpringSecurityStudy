package com.kang.security.config.auth;

//시큐리티가 (form- action ="/login") 주소 요청을 낚아채서 로그인을 진행시킴.
//로그인 진행이 완료되면 시큐리티 세션을 만들어준다. (Security ContextHolder키 값에 세션 정보 저장.)
//시큐리티 세션에 들어갈 수 있는 객체 타입은 정해져있다. -> Authentication 객체.
//시큐리티 세션 안에 Autentication 객체, Autentication 객체 안에 UserDetails 객체 넣는다.

import com.kang.security.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter  //테스트용 getter
public class PrincipalDetails implements UserDetails, OAuth2User{ //UserDetails, OAuth2User 한꺼번에 구현

    private User user;
    private Map<String,Object> attributes;

    //일반 로그인할 때 사용하는 생성자
    public PrincipalDetails(User user){
        this.user = user;
    }

    //OAuth 로그인할 때 사용하는 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }

    //해당 user 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //만료 안되었니 - true: 만료 안됨
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //잠겼니 - true: 안잠김
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //기한이 지났니 - true: 아니요
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //활성화되었니 - true: 활성(휴면계정 설정할 때 - 1년동안 로그인 안하면 휴면 - 현재시간-로그인시간>1년 초과)
    @Override
    public boolean isEnabled() {
        return true;
    }

    //OAuth2User 매소드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //OAuth2User 매소드
    @Override
    public String getName() {
        return null;
    }
}
