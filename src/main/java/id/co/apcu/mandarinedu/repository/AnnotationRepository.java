package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.Annotation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    Optional<Annotation> findByRoomIdAndPageNumber(String roomId, int pageNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Annotation a SET a.fabricData = :data WHERE a.roomId = :roomId AND a.pageNumber = :page")
    int updateFabricData(String roomId, int page, String data);
}
