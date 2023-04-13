package com.pedro.spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Long number;
    @ManyToOne
    private Login login;

    public Contact build(){
        return new Contact().builder()
                .name(this.name)
                .email(this.email)
                .number(this.number)
                .login(this.login)
                .build();
    }
}
