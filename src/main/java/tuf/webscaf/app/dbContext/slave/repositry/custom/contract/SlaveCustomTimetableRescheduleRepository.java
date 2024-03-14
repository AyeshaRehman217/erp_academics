package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTimetableCreationDto;

import java.util.UUID;

// This interface wil extends in Slave Timetables
public interface SlaveCustomTimetableRescheduleRepository {

    //fetch All records with status and without status filter
    Flux<SlaveTimetableCreationDto> indexWithoutStatus(String key, String description, String dp, String d, Integer size, Long page);

    //fetch All records with status and without status filter
    Flux<SlaveTimetableCreationDto> indexWithStatus(Boolean status, String key, String description, String dp, String d, Integer size, Long page);

    //fetch All records with status and without status filter
    Flux<SlaveTimetableCreationDto> indexWithoutStatusAgainstSubject(UUID subjectUUID, String key, String description, String dp, String d, Integer size, Long page);

    //fetch All records with status and without status filter
    Flux<SlaveTimetableCreationDto> indexWithStatusAgainstSubject(UUID subjectUUID, Boolean status, String key, String description, String dp, String d, Integer size, Long page);

}
