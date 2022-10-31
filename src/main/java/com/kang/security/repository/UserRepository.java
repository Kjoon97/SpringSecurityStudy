package com.kang.security.repository;

import com.kang.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//자동으로 빈으로 등록됨.(JpaRepository를 상속했기 때문)
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUsername(String username);
}
