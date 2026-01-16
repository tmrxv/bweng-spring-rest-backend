package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.TimeCapsulePostRequest;
import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import java.util.Optional;

@Service
public class TimeCapsulePostService {

    private final TimeCapsulePostRepository repository;

    public TimeCapsulePostService(TimeCapsulePostRepository repository) {
        this.repository = repository;
    }

    // Create post — current authenticated User is used as owner
    public TimeCapsulePostResponse save(TimeCapsulePostRequest request, User currentUser) {
        TimeCapsulePost entity = toEntity(request);
        entity.setUser(currentUser);
        TimeCapsulePost saved = repository.save(entity);
        return toResponse(saved);
    }

    // Read with optional filtering and pagination
    public Page<TimeCapsulePostResponse> findAll(Optional<Long> userId, Optional<String> title, Pageable pageable) {
        Page<TimeCapsulePost> page;
        if (userId.isPresent() && title.isPresent()) {
            page = repository.findByUserIdAndTitleContainingIgnoreCase(userId.get(), title.get(), pageable);
        } else if (userId.isPresent()) {
            page = repository.findByUserId(userId.get(), pageable);
        } else if (title.isPresent()) {
            page = repository.findByTitleContainingIgnoreCase(title.get(), pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    public Optional<TimeCapsulePostResponse> findById(@NonNull Long id) {
        return repository.findById(id).map(this::toResponse);
    }

    public java.util.List<TimeCapsulePostResponse> findLatest(int limit) {
        java.util.List<TimeCapsulePost> list = repository.findTop5ByOrderByCreatedAtDesc();
        return list.stream().limit(limit).map(this::toResponse).toList();
    }
    // Update — only owner or admin allowed; check is done in controller/service caller
    public TimeCapsulePostResponse update(@NonNull Long id, TimeCapsulePostRequest req) {
        TimeCapsulePost post = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));

        post.setTitle(req.getTitle());
        post.setMessage(req.getMessage());
        post.setSendAt(req.getSendAt());

        // allow file link update (set via upload endpoint)
        if (req.getFileUrl() != null) {
            post.setFileUrl(req.getFileUrl());
            post.setFileType(req.getFileType());
        }

        TimeCapsulePost saved = repository.save(post);
        return toResponse(saved);
    }

    // Delete — only owner or admin allowed; check enforced by caller
    public void delete(@NonNull Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Post with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    public TimeCapsulePostResponse attachFile(@NonNull Long id, String fileUrl, String fileType) {
        TimeCapsulePost post = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
        post.setFileUrl(fileUrl);
        post.setFileType(fileType);
        TimeCapsulePost saved = repository.save(post);
        return toResponse(saved);
    }

    // Mapping methods
    public TimeCapsulePost toEntity(TimeCapsulePostRequest request) {
        TimeCapsulePost entity = new TimeCapsulePost();
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage());
        entity.setSendAt(request.getSendAt());
        if (request.getFileUrl() != null) {
            entity.setFileUrl(request.getFileUrl());
            entity.setFileType(request.getFileType());
        }
        return entity;
    }

    public TimeCapsulePostResponse toResponse(TimeCapsulePost entity) {
        TimeCapsulePostResponse dto = new TimeCapsulePostResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setSendAt(entity.getSendAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setFileUrl(entity.getFileUrl());
        dto.setFileType(entity.getFileType());
        return dto;
    }
}
