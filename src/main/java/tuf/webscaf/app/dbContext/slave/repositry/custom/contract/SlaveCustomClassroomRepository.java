package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;

import java.util.UUID;

// This interface wil extends in Slave Classroom Repository
public interface SlaveCustomClassroomRepository {

    //fetch All records with status and without status filter
    Flux<SlaveClassroomDto> indexWithoutStatus(String key, String code, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveClassroomDto> indexWithStatus(Boolean status, String key, String code, String name, String description, String dp, String d, Integer size, Long page);

    //fetch All records with and campus status and without status filter
    Flux<SlaveClassroomDto> indexWithCampusWithoutStatusFilter(UUID campusUUID, String key, String code, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveClassroomDto> indexWithStatusAndCampus(UUID campusUUID, Boolean status, String key, String code, String name, String description, String dp, String d, Integer size, Long page);

    Mono<SlaveClassroomDto> showByUuid(UUID uuid);
}
