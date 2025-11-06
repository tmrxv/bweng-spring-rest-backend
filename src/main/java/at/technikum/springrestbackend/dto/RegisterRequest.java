package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 5, max = 50)
    private String username;

    @NotBlank @Size(min = 8, max = 255)
    private String password;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$")
    private String country;

    private String profileImageUrl;

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

    public String getPassword() { 
        return password;
    }
    public void setPassword(String password) { 
        this.password = password; 
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
}
