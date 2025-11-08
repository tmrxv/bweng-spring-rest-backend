package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.TimeCapsulePostRequest;
import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Service
public class TimeCapsulePostService {

    private final TimeCapsulePostRepository repository;

    public TimeCapsulePostService(TimeCapsulePostRepository repository) {
        this.repository = repository;
    }

    // CRUD operations
    public TimeCapsulePostResponse save(TimeCapsulePostRequest request) {
        TimeCapsulePost entity = toEntity(request);
        TimeCapsulePost saved = repository.save(entity); // may return null
        return toResponse(saved);
    }

    public List<TimeCapsulePostResponse> findAll() {
        return repository.findAll()
                         .stream()
                         .map(this::toResponse)
                         .toList();
    }

    public Optional<TimeCapsulePostResponse> findById(@NonNull Long id) {
        return repository.findById(id).map(this::toResponse);
    }

    public void delete(@NonNull Long id) {
        repository.deleteById(id);
    }

    // Mapping methods
    public TimeCapsulePost toEntity(TimeCapsulePostRequest request) {
        TimeCapsulePost entity = new TimeCapsulePost();
        entity.setUserId(request.getUserId());
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage());
        entity.setSendAt(request.getSendAt());
        return entity;
    }

    public TimeCapsulePostResponse toResponse(TimeCapsulePost entity) {
        TimeCapsulePostResponse dto = new TimeCapsulePostResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setSendAt(entity.getSendAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
