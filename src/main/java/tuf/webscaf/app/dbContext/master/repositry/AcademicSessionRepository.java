package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicSessionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicSessionRepository extends ReactiveCrudRepository<AcademicSessionEntity, Long> {
    Mono<AcademicSessionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicSessionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicSessionEntity> findFirstBySessionTypeUUIDAndDeletedAtIsNull(UUID sessionTypeUUID);

    Mono<AcademicSessionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AcademicSessionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

//    Mono<AcademicSessionEntity> findFirstByStartDateInAndDeletedAtIsNullOrEndDateInAndDeletedAtIsNull(List<UUID> startDate, List<UUID> endDate);
//
//    Mono<AcademicSessionEntity> findFirstByStartDateInAndDeletedAtIsNullAndUuidIsNotOrEndDateInAndDeletedAtIsNullAndUuidIsNot(List<UUID> startDate, UUID uuid, List<UUID> endDate, UUID uuid1);

    //check by start date and end date already exists or not
    Mono<AcademicSessionEntity> findFirstByStartDateAndDeletedAtIsNullOrEndDateAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    Mono<AcademicSessionEntity> findFirstByStartDateAndDeletedAtIsNullAndUuidIsNotOrEndDateAndDeletedAtIsNullAndUuidIsNot(LocalDateTime startDate, UUID uuid, LocalDateTime endDate, UUID uuid1);

    Mono<Long> countByIsOpenAndDeletedAtIsNull(Boolean isOpen);

    Mono<AcademicSessionEntity> findByIsOpenAndDeletedAtIsNull(Boolean isOpen);

    Mono<AcademicSessionEntity> findByUuidAndIsOpenAndDeletedAtIsNull(UUID uuid, Boolean isOpen);

    Mono<Long> countByIsRegistrationOpenAndDeletedAtIsNull(Boolean isRegistrationOpen);

    Mono<AcademicSessionEntity> findByIsRegistrationOpenAndDeletedAtIsNull(Boolean isRegistrationOpen);

    //This query checks whether hr session start date and end date already exist or not
    @Query(" SELECT * FROM academic_sessions " +
            "WHERE ((:startDate BETWEEN start_date AND end_date) OR (:endDate BETWEEN start_date AND end_date)) " +
            "AND academic_sessions.deleted_at IS NULL" +
            " fetch first row only")
    Mono<AcademicSessionEntity> findStartDateAndEndDateIsUnique(LocalDateTime startDate, LocalDateTime endDate);

    //This query checks whether academic session start date and end date already exist or not
    @Query(" SELECT * FROM academic_sessions " +
            "WHERE ((:startDate BETWEEN start_date AND end_date) OR (:endDate BETWEEN start_date AND end_date)) and uuid !=:uuid " +
            "AND academic_sessions.deleted_at IS NULL" +
            " fetch first row only")
    Mono<AcademicSessionEntity> findStartDateAndEndDateIsUniqueAndUuidIsNot(LocalDateTime startDate, LocalDateTime endDate, UUID uuid);
}
