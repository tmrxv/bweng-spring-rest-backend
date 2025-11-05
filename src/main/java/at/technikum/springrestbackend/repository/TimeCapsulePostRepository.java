package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.model.TimeCapsulePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeCapsulePostRepository extends JpaRepository<TimeCapsulePost, Long> {
    
}
