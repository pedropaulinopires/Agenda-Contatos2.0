package com.pedro.spring.repository;

import com.pedro.spring.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginRepository extends JpaRepository<Login, UUID> {

    Login findByUsername(String username);
}
