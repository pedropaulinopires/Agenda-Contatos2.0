package com.pedro.spring.service;

import com.pedro.spring.domain.Login;
import com.pedro.spring.repository.LoginRepository;
import com.pedro.spring.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;

    //save login
    public Login saveLogin(LoginRequest login){
       return loginRepository.save(login.build());
    }

    //findById
    public Login findLoginById(UUID id){
        return loginRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Login not found by id")
        );
    }

    //find by username
    public Login findLoginByUsername(String username){
        return loginRepository.findByUsername(username);
    }

    //replace
    public void replaceLogin(LoginRequest login,UUID id){
        findLoginById(id);
        loginRepository.save(login.build(login,id));
    }

    //replace
    public void replaceLogin(Login login){
        findLoginById(login.getId());
        loginRepository.save(login);
    }



    //delete
    public void deleteLoginById(UUID id){
        loginRepository.delete(findLoginById(id));
    }

}
