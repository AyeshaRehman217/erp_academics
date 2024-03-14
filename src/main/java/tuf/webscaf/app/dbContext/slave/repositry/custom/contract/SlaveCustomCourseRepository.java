package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseDto;

import java.util.UUID;

// This interface wil extends in  Slave Course Repository
public interface SlaveCustomCourseRepository {

    Flux<SlaveCourseDto> indexWithStudent(UUID studentUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithStudentAndStatus(UUID studentUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> index(String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithStatus(String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);

    Mono<SlaveCourseDto> showByUUID(UUID courseUUID);


    /**
     * fetch All Courses based on Academic Session , Campus and Course UUID with status and without Status
     **/
    Flux<SlaveCourseDto> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);


    /**
     * fetch All Students Based on campus and academic session
     **/
    Flux<SlaveCourseDto> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and academic session
     **/
    Flux<SlaveCourseDto> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on courses and campus
     **/
    Flux<SlaveCourseDto> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String name, String shortName, String key, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on campus
     **/
    Flux<SlaveCourseDto> indexWithCampus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCampusWithStatus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);


    /**
     * fetch All Students Based on Courses
     **/
    Flux<SlaveCourseDto> indexWithCourses(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithCoursesWithStatus(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * fetch All Students Based on Academic Sessions
     **/
    Flux<SlaveCourseDto> indexWithAcademicSession(UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseDto> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page);
}
