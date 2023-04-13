package com.pedro.spring.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginSingInRequest {

    @NotEmpty(message = "Campo de usuário é obrigatório!")
    private String username;

    @NotEmpty(message = "Campo de senha é obrigatório!")
    private String password;

}
