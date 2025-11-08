package at.technikum.springrestbackend.dto;

import java.time.LocalDateTime;

public class TimeCapsulePostResponse {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private LocalDateTime sendAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Long getUserId() { 
        return userId; 
    }

    public void setUserId(Long userId) { 
        this.userId = userId; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getMessage() { 
        return message; 
    }

    public void setMessage(String message) { 
        this.message = message; 
    }

    public LocalDateTime getSendAt() { 
        return sendAt; 
    }

    public void setSendAt(LocalDateTime sendAt) { 
        this.sendAt = sendAt; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}
