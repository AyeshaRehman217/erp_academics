package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAttendanceEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAttendanceRepository;

import java.util.UUID;

@Repository
public interface SlaveAttendanceRepository extends ReactiveCrudRepository<SlaveAttendanceEntity, Long>, SlaveCustomAttendanceRepository {
    Flux<SlaveAttendanceEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveAttendanceEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<SlaveAttendanceEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveAttendanceEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Count Attendance Records With & Without Status Filter
     **/
    @Query(" select count(*) from attendances\n" +
            "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid\n" +
            "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid\n" +
            "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            "join students on commencement_of_classes.student_uuid=students.uuid\n" +
            "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid\n" +
            "join days on commencement_of_classes.day=days.uuid\n" +
            "where attendances.deleted_at is null\n" +
            "and commencement_of_classes.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and attendance_types.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and lecture_types.deleted_at is null\n" +
            "and days.deleted_at is null\n" +
            "and attendances.status= :status " +
            " AND (concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) ILIKE concat('%',:key,'%') \n" +
            " or subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " or subjects.code ILIKE concat('%',:subjectCode,'%') \n" +
            " or lecture_types.name ILIKE concat('%',:lectureTypeName,'%') or days.name ILIKE concat('%',:day,'%')  ) ")
    Mono<Long> countAttendanceWithStatusFilter(Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day);

    @Query(" select count(*) from attendances\n" +
            "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid\n" +
            "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid\n" +
            "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            "join students on commencement_of_classes.student_uuid=students.uuid\n" +
            "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid\n" +
            "join days on commencement_of_classes.day=days.uuid\n" +
            "where attendances.deleted_at is null\n" +
            "and commencement_of_classes.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and attendance_types.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and lecture_types.deleted_at is null\n" +
            "and days.deleted_at is null\n" +
            " AND (concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) ILIKE concat('%',:key,'%') \n" +
            " or subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " or subjects.code ILIKE concat('%',:subjectCode,'%') \n" +
            " or lecture_types.name ILIKE concat('%',:lectureTypeName,'%') or days.name ILIKE concat('%',:day,'%')  ) ")
    Mono<Long> countAttendanceWithoutStatusFilter(String key, String subjectCode, String subjectName, String lectureTypeName, String day);

    /**
     * Count Attendance Records With & Without Status Filter (With Subject Filter)
     **/
    @Query(" select count(*) from attendances\n" +
            "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid\n" +
            "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid\n" +
            "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            "join students on commencement_of_classes.student_uuid=students.uuid\n" +
            "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid\n" +
            "join days on commencement_of_classes.day=days.uuid\n" +
            "where attendances.deleted_at is null\n" +
            "and commencement_of_classes.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and attendance_types.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and lecture_types.deleted_at is null\n" +
            "and days.deleted_at is null\n" +
            "and attendances.status= :status " +
            "and commencement_of_classes.subject_uuid= :subjectUUID " +
            " AND (concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) ILIKE concat('%',:key,'%') \n" +
            " or subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " or subjects.code ILIKE concat('%',:subjectCode,'%') \n" +
            " or lecture_types.name ILIKE concat('%',:lectureTypeName,'%') or days.name ILIKE concat('%',:day,'%')  ) ")
    Mono<Long> countAttendanceWithStatusFilterAgainstSubject(UUID subjectUUID, Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day);

    @Query(" select count(*) from attendances\n" +
            "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid\n" +
            "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid\n" +
            "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            "join students on commencement_of_classes.student_uuid=students.uuid\n" +
            "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid\n" +
            "join days on commencement_of_classes.day=days.uuid\n" +
            "where attendances.deleted_at is null\n" +
            "and commencement_of_classes.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and attendance_types.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and lecture_types.deleted_at is null\n" +
            "and days.deleted_at is null\n" +
            "and commencement_of_classes.subject_uuid= :subjectUUID " +
            " AND (concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) ILIKE concat('%',:key,'%') \n" +
            " or subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " or subjects.code ILIKE concat('%',:subjectCode,'%') \n" +
            " or lecture_types.name ILIKE concat('%',:lectureTypeName,'%') or days.name ILIKE concat('%',:day,'%')  ) ")
    Mono<Long> countAttendanceWithoutStatusFilterAgainstSubject(UUID subjectUUID, String key, String subjectCode, String subjectName, String lectureTypeName, String day);
}
