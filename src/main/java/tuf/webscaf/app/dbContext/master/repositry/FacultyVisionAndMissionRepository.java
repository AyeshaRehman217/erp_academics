package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.FacultyVisionAndMissionEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface FacultyVisionAndMissionRepository extends ReactiveCrudRepository<FacultyVisionAndMissionEntity, Long> {
    Mono<FacultyVisionAndMissionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<FacultyVisionAndMissionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<FacultyVisionAndMissionEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);

    Mono<FacultyVisionAndMissionEntity> findFirstByFacultyUUIDAndDeletedAtIsNull(UUID FacultyUUID);

    Mono<FacultyVisionAndMissionEntity> findFirstByVisionAndFacultyUUIDAndDeletedAtIsNull(String name, UUID FacultyUUID);

    Mono<FacultyVisionAndMissionEntity> findFirstByMissionAndFacultyUUIDAndDeletedAtIsNull(String name,UUID FacultyUUID);

    Mono<FacultyVisionAndMissionEntity> findFirstByVisionAndFacultyUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID FacultyUUID,UUID uuid);

    Mono<FacultyVisionAndMissionEntity> findFirstByMissionAndFacultyUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID FacultyUUID,UUID uuid);

    Mono<FacultyVisionAndMissionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<FacultyVisionAndMissionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
