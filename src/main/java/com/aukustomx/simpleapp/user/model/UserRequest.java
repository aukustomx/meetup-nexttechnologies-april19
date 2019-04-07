package com.aukustomx.simpleapp.user.model;

import javax.validation.constraints.NotBlank;

public class UserRequest {

    @NotBlank(message = "El nombre no debe ser null o vacío")
    private String name;

    @NotBlank(message = "El email no debe ser null o vacío")
    private String email;

    public UserRequest() {
    }

    public UserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
