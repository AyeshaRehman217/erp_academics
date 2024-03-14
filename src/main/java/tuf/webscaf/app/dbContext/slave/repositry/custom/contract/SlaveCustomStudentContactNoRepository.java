package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;

import java.util.UUID;

// This interface wil extends in Slave Student Contact No Repository
public interface SlaveCustomStudentContactNoRepository {

    //fetch All records without status and Student Meta Filter and without status filter
    Flux<SlaveStudentContactNoDto> indexWithoutStatus(UUID studentMetaUUID, String key, String contactNo, String dp, String d, Integer size, Long page);

    //fetch All records with status and Student Meta Filter
    Flux<SlaveStudentContactNoDto> indexWithStatus(UUID studentMetaUUID, Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page);

    //fetch All records with only status filter
    Flux<SlaveStudentContactNoDto> fetchAllRecordsWithStatusFilter(Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page);

    //fetch All records without status filter
    Flux<SlaveStudentContactNoDto> fetchAllRecordsWithoutStatusFilter(String key, String contactNo, String dp, String d, Integer size, Long page);

    //Show Student Record Against Student Contact No
    Mono<SlaveStudentContactNoDto> showAllStudentContactNo(UUID studentContactNo);

}
