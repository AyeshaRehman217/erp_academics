package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherEntity;

import java.util.UUID;

@Repository
public interface TeacherRepository extends ReactiveCrudRepository<TeacherEntity, Long> {
    Mono<TeacherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherEntity> findAllByDeptRankUUIDAndDeletedAtIsNull(UUID deptRankUUID);

    Flux<TeacherEntity> findAllByDeptRankUUIDAndDeletedAtIsNullAndUuidIsNot(UUID deptRankUUID, UUID uuid);

    Mono<TeacherEntity> findFirstByEmployeeCodeAndDeletedAtIsNull(String employeeCode);

    Mono<TeacherEntity> findFirstByEmployeeCodeAndDeletedAtIsNullAndUuidIsNot(String employeeCode, UUID uuid);

    Mono<TeacherEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<TeacherEntity> findFirstByReportingToAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherEntity> findFirstByDeptRankUUIDAndDeletedAtIsNull(UUID deptRankUUID);
}
