package com.pedro.spring.request;

import com.pedro.spring.domain.Contact;
import com.pedro.spring.domain.Login;
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
public class ContactRequest {

    @NotEmpty(message = "Campo nome é obrigatório!")
    private String name;

    private String email;

    @NotNull(message = "Campo número é obrigatório!")
    private Long number;

    private Login login;


    public Contact build() {
        return new Contact().builder().name(this.name).email(this.email).number(this.number).login(this.login).build();
    }

    public Contact build(ContactRequest contact, UUID id) {
        return new Contact().builder().id(id).name(this.name).email(this.email).number(this.number).login(this.login).build();
    }

    public void fromContact(Contact contact) {
        this.name = contact.getName();
        this.email = contact.getEmail();
        this.number = contact.getNumber();
    }
}
