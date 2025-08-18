package edu.pahana.service.dto;

public class LoginResponse {
    public String token;
    public String role;
    public String username;

    public LoginResponse(String token, String role, String username){
        this.token = token; this.role = role; this.username = username;
    }
}
