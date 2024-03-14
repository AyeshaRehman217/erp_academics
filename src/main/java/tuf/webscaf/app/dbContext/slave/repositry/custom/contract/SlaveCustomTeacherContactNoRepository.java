package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoDto;

import java.util.UUID;

// This interface wil extends in Slave Teacher Contact No Repository
public interface SlaveCustomTeacherContactNoRepository {

    //fetch All records with status and without status filter
    Flux<SlaveTeacherContactNoDto> indexWithoutStatus(UUID teacherMetaUUID, String key, String contactNo, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherContactNoDto> indexWithStatus(UUID teacherMetaUUID, Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page);

    //fetch All records with only status filter
    Flux<SlaveTeacherContactNoDto> fetchAllRecordsWithStatusFilter(Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page);

    //fetch All records without status filter
    Flux<SlaveTeacherContactNoDto> fetchAllRecordsWithoutStatusFilter(String key, String contactNo, String dp, String d, Integer size, Long page);

    //Show Teacher Record Against Teacher Contact No
    Mono<SlaveTeacherContactNoDto> showAllTeacherContactNo(UUID teacherContactNo);
}
