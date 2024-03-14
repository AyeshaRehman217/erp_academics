package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.cglib.core.Local;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CommencementOfClassesEntity;
import tuf.webscaf.app.dbContext.master.entity.RegistrationEntity;
import tuf.webscaf.app.dbContext.master.entity.TimetableCreationEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCommencementOfClassesRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface CommencementOfClassesRepository extends ReactiveCrudRepository<CommencementOfClassesEntity, Long> {

    Mono<CommencementOfClassesEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CommencementOfClassesEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CommencementOfClassesEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<CommencementOfClassesEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);


    //check by start time and end time and day and created at (While Storing Record)
    @Query("select commencement_of_classes.* from commencement_of_classes" +
            " WHERE commencement_of_classes.start_time = :startTime " +
            " AND commencement_of_classes.end_time= :endTime " +
            " AND commencement_of_classes.student_uuid= :studentUUID " +
            " AND commencement_of_classes.academic_session_uuid= :academicSessionUUID " +
            " AND commencement_of_classes.subject_uuid= :subjectUUID " +
            " AND commencement_of_classes.day= :dayUUID ")
    Mono<CommencementOfClassesEntity> findByCommencementOfClassesStartTimeAndEndTimeDayStudentAcademicSessionSubject(LocalTime startTime, LocalTime endTime, UUID studentUUID, UUID academicSessionUUID, UUID subjectUUID, UUID dayUUID);


    //check by start time and end time and day and created at (While Storing Record)
    @Query("select commencement_of_classes.* from commencement_of_classes" +
            " WHERE commencement_of_classes.start_time = :startTime " +
            " AND commencement_of_classes.end_time= :endTime " +
            " AND commencement_of_classes.day= :dayUUID" +
            " AND DATE(commencement_of_classes.created_at) = :createdAt")
    Mono<CommencementOfClassesEntity> findByCommencementOfClassesStartTimeAndEndTimeExists(LocalTime startTime, LocalTime endTime, UUID dayUUID, LocalDate createdAt);


    //check by start time and end time and day and created at (While Updating Record)
    @Query("select commencement_of_classes.* from commencement_of_classes" +
            " WHERE commencement_of_classes.start_time = :startTime " +
            " AND commencement_of_classes.end_time= :endTime " +
            " AND commencement_of_classes.day= :dayUUID" +
            " AND DATE(commencement_of_classes.created_at) = :createdAt" +
            " AND commencement_of_classes.uuid != :commencementUUID")
    Mono<CommencementOfClassesEntity> findByCommencementOfClassesStartTimeAndEndTimeExistsAndUuidIsNot(LocalTime startTime, LocalTime endTime, UUID dayUUID, LocalDate createdAt, UUID commencementUUID);

    @Query("SELECT view.priority, 0,view.is_rescheduled, view.description,\n" +
            " view.rescheduled_date ,\n" +
            " view.start_time,view.end_time,\n" +
            " view.studentuuid as student_uuid,\n" +
            " view.subject_uuid,\n" +
            " view.enrollment_uuid,\n" +
            " view.section_uuid,\n" +
            " view.student_group_uuid,\n" +
            " view.teacher_uuid,\n" +
            " view.classroom_uuid,\n" +
            " view.academic_session_uuid,\n" +
            " view.lecture_type_uuid,\n" +
            " view.lecture_delivery_mode_uuid,view.day, view.created_by, \n" +
            " :createdAt \n" +
            " FROM timetableView as view\n" +
            " WHERE view.priority=(SELECT MAX(view2.priority)\n" +
            "             FROM timetableView as view2\n" +
            "             WHERE view.start_time = view2.start_time\n" +
            "                 AND view.end_time = view2.end_time\n" +
            "                 AND view.studentuuid = view2.studentuuid\n" +
            "                 AND view.day = view2.day)\n" +
            " AND view.day = :dayUUID \n" +
            " AND view.start_time = :startTime \n" +
            " AND view.end_time = :endTime\n" +
            " AND DATE(view.created_at) = :createdAt ")
    Mono<CommencementOfClassesEntity> fetchRecordFromTimeTableViewWithStartTimeEndTimeAndDay(LocalTime startTime, LocalTime endTime, UUID dayUUID, LocalDate createdAt);
}


