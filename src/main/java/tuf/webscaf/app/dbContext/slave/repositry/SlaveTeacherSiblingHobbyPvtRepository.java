package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingHobbyPvtEntity;

import java.util.UUID;


@Repository
public interface SlaveTeacherSiblingHobbyPvtRepository extends ReactiveCrudRepository<SlaveTeacherSiblingHobbyPvtEntity, Long> {
    //    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndHobbyIdInAndDeletedAtIsNull(Long studentId, List<Long> ids);
//
//    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndDeletedAtIsNull(Long studentId);
    Mono<SlaveTeacherSiblingHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
