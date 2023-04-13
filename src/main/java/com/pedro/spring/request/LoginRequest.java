package com.pedro.spring.request;

import com.pedro.spring.domain.Login;
import com.pedro.spring.enums.Sex;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequest {

    @NotEmpty(message = "O campo nome é obrigatório!")
    private String name;

    @NotEmpty(message = "O campo de usuário é obrigatório!")
    private String username;

    @NotEmpty(message = "O campo de senha é obrigatório!")
    private String password;

    @NotNull(message = "Sexo é obrigatório!")
    private Sex sex;

    public Login build(){
        return new Login().builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .sex(this.sex)
                .build();
    }

    public Login build(LoginRequest login, UUID id){
        return new Login().builder()
                .id(id)
                .name(login.getName())
                .username(login.getUsername())
                .password(login.getPassword())
                .sex(login.getSex())
                .build();
    }
    public LoginRequest build(Login login){
        return new LoginRequest().builder()
                .name(login.getName())
                .username(login.getUsername())
                .password(login.getPassword())
                .sex(login.getSex())
                .build();
    }

    public void fromLogin(Login login){
        this.name = login.getName();
        this.username = login.getUsername();
        this.sex = login.getSex();
    }
}
