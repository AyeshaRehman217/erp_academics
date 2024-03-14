package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;

import java.util.UUID;

// This interface wil extends in Campus Course Repository
public interface SlaveCustomCampusCourseRepository {

    Flux<SlaveCampusCourseDto> campusCourseIndex(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveCampusCourseDto> campusCourseIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    //Fetch Records Based on CampusUUID filter
    Flux<SlaveCampusCourseDto> campusCourseIndexWithCampusFilter(UUID campusUUID, String name, String dp, String d, Integer size, Long page);

    //Fetch Records Based on CampusUUID filter and Status filter
    Flux<SlaveCampusCourseDto> campusCourseIndexWithStatusAndCampus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    //Fetch Records Based on CampusUUID  and Academic Session
    Flux<SlaveCampusCourseDto> campusCourseListAgainstCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page);

    //Fetch Records Based on CampusUUID  and Academic Session and Status filter
    Flux<SlaveCampusCourseDto> campusCourseListAgainstCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

}
