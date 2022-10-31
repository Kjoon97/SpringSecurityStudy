package com.kang.security.config.auth;

import com.kang.security.model.User;
import com.kang.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// "/login" 요청이 오면 자동으로 UserDetailsService 타입으로 Ioc 되어있는 loadUserByUsername 함수 실행됨.
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  //매개변수 username과 form의 name ="username" 동일해야함.
        User userEntity = userRepository.findByUsername(username);
        return new PrincipalDetails(userEntity);  //-> 리턴되면 시큐리티세션(내부 Authentication객체(내부 UserDetails객체))로 됨.
    }
}
