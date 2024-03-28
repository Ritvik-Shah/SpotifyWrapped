package com.example.spotifywrapped;

public class User {

    private String apiKey;
    private String email;
    private String id;
    private String password;

    public User() {

    }

    public User(String apiKey, String email, String id, String password) {
        this.apiKey = apiKey;
        this.email = email;
        this.id = id;
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
