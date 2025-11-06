package at.technikum.springrestbackend.dto;

import java.time.OffsetDateTime;

public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String country;
    private String profileImageUrl;
    private String role;
    private OffsetDateTime createdAt;

    public UserResponse() {

    }

    public UserResponse(Long id, String email, String username, String country, 
                        String profileImageUrl, String role, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.country = country;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.createdAt = createdAt;
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

    public String getCountry() { 
        return country; 
    }
    public void setCountry(String country) { 
        this.country = country; 
    }

    public String getProfileImageUrl() { 
        return profileImageUrl; 
    }
    public void setProfileImageUrl(String profileImageUrl) { 
        this.profileImageUrl = profileImageUrl; 
    }

    public String getRole() { 
        return role; 
    }
    public void setRole(String role) { 
        this.role = role; 
    }

    public OffsetDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(OffsetDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}
