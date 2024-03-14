package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingNationalityPvtEntity;

import java.util.UUID;


@Repository
public interface SlaveTeacherSiblingNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherSiblingNationalityPvtEntity, Long> {
//    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndHobbyIdInAndDeletedAtIsNull(Long studentId, List<Long> ids);
//
//    Flux<StudentMotherHobbyPvtEntity> findAllByStudentIdAndDeletedAtIsNull(Long studentId);

    Mono<SlaveTeacherSiblingNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
