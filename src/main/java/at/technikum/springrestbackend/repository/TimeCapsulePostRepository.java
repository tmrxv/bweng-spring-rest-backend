package at.technikum.springrestbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.technikum.springrestbackend.entity.TimeCapsulePost;

@Repository
public interface TimeCapsulePostRepository extends JpaRepository<TimeCapsulePost, Long> {
    Page<TimeCapsulePost> findByUserId(Long userId, Pageable pageable);
    Page<TimeCapsulePost> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<TimeCapsulePost> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title, Pageable pageable);

    java.util.List<TimeCapsulePost> findTop5ByOrderByCreatedAtDesc();
    
    void deleteByUserId(Long userId);
}
