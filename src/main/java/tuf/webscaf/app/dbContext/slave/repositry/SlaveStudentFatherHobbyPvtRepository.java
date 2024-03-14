package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentFatherHobbyPvtEntity, Long> {
    //    Flux<StudentFatherHobbyPvtEntity> findAllByStudentIdAndHobbyIdInAndDeletedAtIsNull(Long studentId, List<Long> ids);
//
//    Flux<StudentFatherHobbyPvtEntity> findAllByStudentIdAndDeletedAtIsNull(Long studentId);
    Mono<SlaveStudentFatherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
