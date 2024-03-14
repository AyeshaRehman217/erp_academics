package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingAilmentPvtEntity;

import java.util.UUID;


@Repository
public interface SlaveTeacherSiblingAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherSiblingAilmentPvtEntity, Long> {
//    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndHobbyIdInAndDeletedAtIsNull(Long studentId, List<Long> ids);
//
//    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndDeletedAtIsNull(Long studentId);

    Mono<SlaveTeacherSiblingAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
