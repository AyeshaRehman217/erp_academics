package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusCourseDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegisteredCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;

import java.util.UUID;

// This interface wil extends in Slave Student Repository
public interface SlaveCustomStudentRepository {
    /**
     * Fetch All students with and without status filter
     **/
    Flux<SlaveStudentEntity> indexStudentsWithoutStatus(String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexStudentsWithStatus(String studentId, Boolean status, String dp, String d, Integer size, Long page);


    Flux<SlaveStudentRegisteredCourseDto> indexWithCourseOffered(UUID courseOfferedUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentRegisteredCourseDto> indexWithCourseOfferedWithStatus(UUID courseOfferedUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on campus, course and academic session
     **/
    Flux<SlaveStudentEntity> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on campus and academic session
     **/
    Flux<SlaveStudentEntity> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and academic session
     **/
    Flux<SlaveStudentEntity> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and campus
     **/
    Flux<SlaveStudentEntity> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on campus
     **/
    Flux<SlaveStudentEntity> indexWithCampus(UUID campusUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCampusWithStatus(UUID campusUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);


    /**
     * fetch All Students Based on Courses
     **/
    Flux<SlaveStudentEntity> indexWithCourses(UUID courseUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithCoursesWithStatus(UUID courseUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on Academic Sessions
     **/
    Flux<SlaveStudentEntity> indexWithAcademicSession(UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Fetch Students Against Commencement Of Classes Against teacher , Course Subject and Academic Session (This is used by LMS in Assignment Attempt Handler)
     **/
    Flux<SlaveStudentEntity> findAllStudentsAgainstTeacherWithSameCourseSubjectAndSession(UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> findAllStudentsAgainstTeacherWithSameCourseSubjectAndSessionAndStatus(Boolean status, UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page);
}
