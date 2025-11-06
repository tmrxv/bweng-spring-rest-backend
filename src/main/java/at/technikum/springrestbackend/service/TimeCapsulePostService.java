package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.model.TimeCapsulePost;
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

    public TimeCapsulePost save(@NonNull TimeCapsulePost post) {
        return repository.save(post);
    }

    public List<TimeCapsulePost> findAll() {
        return repository.findAll();
    }

    public Optional<TimeCapsulePost> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    public void delete(@NonNull Long id) {
        repository.deleteById(id);
    }
}
