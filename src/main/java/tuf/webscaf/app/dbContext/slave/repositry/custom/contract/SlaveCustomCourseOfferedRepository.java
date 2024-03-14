package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseOfferedDto;

import java.util.UUID;

// This interface wil extends in Course Offered Repository
public interface SlaveCustomCourseOfferedRepository {

    Flux<SlaveCourseOfferedDto> courseOfferedIndex(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Campus UUID
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithCampusFilter(UUID campusUUID, String name, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Campus UUID and Status
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusAndCampus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Academic Session UUID
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithSessionFilter(UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Academic Session and Status
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusAndSession(UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Campus UUID and Academic Session
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithCampusAndSessionFilter(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    /**
     * Fetch course offered Based on Campus UUID and Status and Academic Session
     **/
    Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusSessionAndCampus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);
}
