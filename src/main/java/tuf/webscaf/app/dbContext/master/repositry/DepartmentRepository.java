package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DepartmentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends ReactiveCrudRepository<DepartmentEntity, Long> {
    Mono<DepartmentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DepartmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
//
//    Mono<DepartmentEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNull(String code);
//
//    Mono<DepartmentEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String code, UUID uuid);

    Mono<DepartmentEntity> findFirstBySlugAndDeletedAtIsNull(String slug);

    Mono<DepartmentEntity> findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(String slug, UUID uuid);

    @Query("Select short_name from departments where short_name <> '' and short_name ilike :shortName and deleted_at is null fetch first row only")
    Mono<DepartmentEntity> checkShortNameIsUnique(String shortName);

    @Query("Select short_name from departments where short_name <> '' and short_name ilike :shortName and deleted_at is null  and  uuid!=:uuid fetch first row only")
    Mono<DepartmentEntity> checkShortNameIsUniqueAndUuidIsNot(String shortName, UUID uuid);

    Mono<DepartmentEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<DepartmentEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<DepartmentEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    Mono<DepartmentEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String code, UUID uuid);

    //Check if Faculty UUID Exists in Departments
    Mono<DepartmentEntity> findFirstByFacultyUUIDAndDeletedAtIsNull(UUID facultyUUID);

    Flux<DepartmentEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);
}
