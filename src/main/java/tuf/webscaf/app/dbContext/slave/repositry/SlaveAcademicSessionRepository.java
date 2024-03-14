package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicSessionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicSessionRepository;

import java.util.UUID;

@Repository
public interface SlaveAcademicSessionRepository extends ReactiveCrudRepository<SlaveAcademicSessionEntity, Long>, SlaveCustomAcademicSessionRepository {
    Flux<SlaveAcademicSessionEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveAcademicSessionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //  get academic session bases on status, isOpen, isRegistrationOpen, isEnrollmentOpen and isTimetableAllow
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isOpen, Boolean isRegistrationOpen, Boolean isEnrollmentOpen, Boolean isTimetableAllow, Boolean status, String description, Boolean isOpen1, Boolean isRegistrationOpen1, Boolean isEnrollmentOpen1, Boolean isTimetableAllow1, Boolean status1);

    // count academic session bases on status, isOpen, isRegistrationOpen, isEnrollmentOpen and isTimetableAllow
    Mono<Long> countByNameContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNull
    (String name, Boolean isOpen, Boolean isRegistrationOpen, Boolean isEnrollmentOpen, Boolean isTimetableAllow, Boolean status, String description, Boolean isOpen1, Boolean isRegistrationOpen1, Boolean isEnrollmentOpen1, Boolean isTimetableAllow1, Boolean status1);

    //  get academic session bases on status and isOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isOpen, Boolean status, String description, Boolean isOpen1, Boolean status1);

    // count academic session bases on status and isOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNull
    (String name, Boolean isOpen, Boolean status, String description, Boolean isOpen1, Boolean status1);

    //  get academic session bases on status and isRegistrationOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isRegistrationOpen, Boolean status, String description, Boolean isRegistrationOpen1, Boolean status1);

    // count academic session bases on status and isRegistrationOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNull
    (String name, Boolean isRegistrationOpen, Boolean status, String description, Boolean isRegistrationOpen1, Boolean status1);

    //  get academic session bases on status and isEnrollmentOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isEnrollmentOpen, Boolean status, String description, Boolean isEnrollmentOpen1, Boolean status1);

    // count academic session bases on status and isEnrollmentOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNull
    (String name, Boolean isEnrollmentOpen, Boolean status, String description, Boolean isEnrollmentOpen1, Boolean status1);

    //  get academic session bases on status and isTimetableAllow
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isTimetableAllow, Boolean status, String description, Boolean isTimetableAllow1, Boolean status1);

    // count academic session bases on status and isTimetableAllow
    Mono<Long> countByNameContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNull
    (String name, Boolean isTimetableAllow, Boolean status, String description, Boolean isTimetableAllow1, Boolean status1);

    //  get academic session bases on isRegistrationOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isRegistrationOpen, String description, Boolean isRegistrationOpen1);

    // count academic session bases on isRegistrationOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNull
    (String name, Boolean isRegistrationOpen, String description, Boolean isRegistrationOpen1);

    //  get academic session bases on isOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isOpen, String description, Boolean isOpen1);

    // count academic session bases on isOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndDeletedAtIsNull
    (String name, Boolean isOpen, String description, Boolean isOpen1);

    //  get academic session bases on isEnrollmentOpen
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isEnrollmentOpen, String description, Boolean isEnrollmentOpen1);

    // count academic session bases on isEnrollmentOpen
    Mono<Long> countByNameContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNull
    (String name, Boolean isEnrollmentOpen, String description, Boolean isEnrollmentOpen1);

    //  get academic session bases on isTimetableAllow
    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNull
    (Pageable pageable, String name, Boolean isTimetableAllow, String description, Boolean isTimetableAllow1);

    // count academic session bases on isTimetableAllow
    Mono<Long> countByNameContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNull
    (String name, Boolean isTimetableAllow, String description, Boolean isTimetableAllow1);

    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveAcademicSessionEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Mono<SlaveAcademicSessionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveAcademicSessionEntity> findByNameAndDeletedAtIsNull(String name);


    @Query("select count(*) from academic_sessions \n" +
            "  join academic_calendars on academic_sessions.uuid = academic_calendars.academic_session_uuid \n" +
            "  where academic_sessions.deleted_at IS NULL\n" +
            "  AND academic_calendars.deleted_at IS NULL\n" +
            "  AND academic_sessions.status = :status\n" +
            "  AND  academic_sessions.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countShowAcademicSessionOfCalendarWithStatus(String name, Boolean status);

    @Query("select count(*) from academic_sessions \n" +
            "  join academic_calendars on academic_sessions.uuid = academic_calendars.academic_session_uuid \n" +
            "  where academic_sessions.deleted_at IS NULL\n" +
            "  AND academic_calendars.deleted_at IS NULL\n" +
            "  AND  academic_sessions.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countShowAcademicSessionOfCalendarWithOutStatus(String name);

    @Query("select COUNT(DISTINCT academic_sessions.uuid) from academic_sessions \n" +
            "  join teacher_subjects on academic_sessions.uuid = teacher_subjects.academic_session_uuid \n" +
            "  where academic_sessions.deleted_at IS NULL\n" +
            "  AND teacher_subjects.deleted_at IS NULL\n" +
            "  AND teacher_subjects.teacher_uuid = :teacherUUID\n" +
            "  AND academic_sessions.status = :status\n" +
            "  AND  academic_sessions.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countShowAcademicSessionOfTeacherWithStatus(UUID teacherUUID, String name, Boolean status);

    @Query("select COUNT(DISTINCT academic_sessions.uuid) from academic_sessions \n" +
            "  join teacher_subjects on academic_sessions.uuid = teacher_subjects.academic_session_uuid \n" +
            "  where academic_sessions.deleted_at IS NULL\n" +
            "  AND teacher_subjects.deleted_at IS NULL\n" +
            "  AND teacher_subjects.teacher_uuid = :teacherUUID\n" +
            "  AND  academic_sessions.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countShowAcademicSessionOfTeacherWithOutStatus(UUID teacherUUID, String name);
}
