package com.pedro.spring.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RememberPassword {

    @NotEmpty(message="O campo de usuário é obrigatório!")
    private String username;

    @NotEmpty(message="O campo de senha é obrigatório!")
    private String password;
}
