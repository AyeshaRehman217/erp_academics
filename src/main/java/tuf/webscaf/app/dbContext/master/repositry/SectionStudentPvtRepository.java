package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SectionStudentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionStudentPvtRepository extends ReactiveCrudRepository<SectionStudentPvtEntity,Long> {
    Mono<SectionStudentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SectionStudentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SectionStudentPvtEntity> findAllBySectionUUIDAndStudentUUIDInAndDeletedAtIsNull(UUID sectionUUID, List<UUID> studentUUID);

    Mono<SectionStudentPvtEntity> findFirstByStudentUUIDInAndDeletedAtIsNull(List<UUID> studentUUID);

    Flux<SectionStudentPvtEntity> findAllBySectionUUIDAndDeletedAtIsNull(UUID sectionUUID);

    Flux<SectionStudentPvtEntity> findBySectionUUIDAndDeletedAtIsNull(UUID sectionUUID);

    Mono<SectionStudentPvtEntity> findFirstBySectionUUIDAndStudentUUIDAndDeletedAtIsNull(UUID sectionUUID, UUID studentUUID);

    Mono<SectionStudentPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<SectionStudentPvtEntity> findFirstBySectionUUIDAndDeletedAtIsNull(UUID sectionUUID);

    Mono<Long> countBySectionUUIDAndDeletedAtIsNull(UUID sectionUUID);
}
