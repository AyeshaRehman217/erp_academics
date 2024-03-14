package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AilmentEntity;
import tuf.webscaf.app.dbContext.master.entity.SemesterEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SemesterRepository extends ReactiveCrudRepository<SemesterEntity, Long> {
    Mono<SemesterEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SemesterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SemesterEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SemesterEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    //Check if Semester No. is Unique
    Mono<SemesterEntity> findFirstBySemesterNoIgnoreCaseAndDeletedAtIsNull(String semesterNo);

    Mono<SemesterEntity> findFirstBySemesterNoIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String semesterNo, UUID uuid);

    Flux<SemesterEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);
}
