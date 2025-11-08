package at.technikum.springrestbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.technikum.springrestbackend.entity.TimeCapsulePost;

@Repository
public interface TimeCapsulePostRepository extends JpaRepository<TimeCapsulePost, Long> {
    
}
