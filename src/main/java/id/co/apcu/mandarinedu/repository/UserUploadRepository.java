package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.UserUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUploadRepository extends JpaRepository<UserUpload, String> {
    List<UserUpload> findByUserIdOrderByUploadedAtDesc(String userId);

    List<UserUpload> findAllByOrderByUploadedAtDesc();
}
