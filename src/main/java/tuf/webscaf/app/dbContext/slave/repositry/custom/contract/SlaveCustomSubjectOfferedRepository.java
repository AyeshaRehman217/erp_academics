package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOfferedDto;

import java.util.UUID;

// This interface wil extends in  Subject Offered Repository
public interface SlaveCustomSubjectOfferedRepository {

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndex(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Index Subject Offered Against Student and Course with and without Status Filter
     **/
    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstStudentAndCourseWithStatus(Boolean status, UUID studentUUID, UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstStudentAndCourse(UUID studentUUID, UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    /**
     * Index Subject Offered Against Course with and without Status Filter
     **/
    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseWithStatus(Boolean status, UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourse(UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    /**
     * Index Subject Offered Against Obe with and without Status Filter
     **/
    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithStatusAndOBE(Boolean status, Boolean obe,String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithOBE(Boolean obe, String name, String dp, String d, Integer size, Long page);


    /**
     * Index Subject Offered Against Course And Obe with and without Status Filter
     **/
    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseWithStatusAndOBE(Boolean status, Boolean obe, UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseAndOBE(Boolean obe, UUID courseUUID, String name, String dp, String d, Integer size, Long page);


    /**
     * fetch All Courses based on Academic Session , Campus and Course UUID with status and without Status
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);


    /**
     * fetch All Students Based on campus and academic session
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and academic session
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and campus
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on campus
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCampus(UUID campusUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCampusWithStatus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page);


    /**
     * fetch All Students Based on Courses
     **/
    Flux<SlaveSubjectOfferedDto> indexWithCourses(UUID courseUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithCoursesWithStatus(UUID courseUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on Academic Sessions
     **/
    Flux<SlaveSubjectOfferedDto> indexWithAcademicSession(UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOfferedDto> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, Boolean status, String name, String dp, String d, Integer size, Long page);

}
