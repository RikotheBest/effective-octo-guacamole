package com.example.springboot_webapp.repo;

import com.example.springboot_webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo extends JpaRepository<User, String> {
    User findByUsername(String username);
}
