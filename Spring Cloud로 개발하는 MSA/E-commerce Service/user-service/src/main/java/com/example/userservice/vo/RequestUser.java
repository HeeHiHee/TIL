package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUser {
    @NotNull(message = "Email cannot be null")
    @Size(min=2)
    @Email
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min=2)
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min=8)
    private String pwd;
}
