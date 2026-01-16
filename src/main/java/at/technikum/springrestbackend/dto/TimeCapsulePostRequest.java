package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TimeCapsulePostRequest {

    private Long userId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private LocalDateTime sendAt;

    private String fileUrl;

    private String fileType;

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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
