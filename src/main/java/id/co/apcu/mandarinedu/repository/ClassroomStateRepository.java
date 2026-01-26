package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.ClassroomState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomStateRepository extends JpaRepository<ClassroomState, String> {

}
