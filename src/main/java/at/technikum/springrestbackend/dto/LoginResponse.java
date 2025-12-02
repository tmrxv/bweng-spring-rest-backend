package at.technikum.springrestbackend.dto;

public class LoginResponse {

    private Long id;
    private String email;
    private String username;
    private String role;

    public LoginResponse() {
        // default constructor
    }

    public LoginResponse(Long id, String email, String username, String role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
