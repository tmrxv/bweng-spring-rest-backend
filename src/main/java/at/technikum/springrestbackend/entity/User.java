package at.technikum.springrestbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, length = 255)
    private String email;

    @NotBlank
    @Size(min = 5, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    // ISO 3166-1 alpha-2 country code, e.g. "AT", "DE", "US"
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a valid ISO 3166-1 alpha-2 code")
    @Column(nullable = false, length = 2)
    private String country;

    @Column(length = 2048)
    private String profileImageUrl;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String role = "USER";

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public User() {

    }

    // Getters and setters
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

    public OffsetDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}
