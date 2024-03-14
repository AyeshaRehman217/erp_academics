package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCommencementOfClassesDto;

import java.util.UUID;

// This interface wil extends in Slave Commencement Of Classes Repository
public interface SlaveCustomCommencementOfClassesRepository {

    //fetch All records with status and without status filter
    Flux<SlaveCommencementOfClassesDto> indexWithoutStatus(String key, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCommencementOfClassesDto> indexWithStatus(Boolean status, String key, String description, String dp, String d, Integer size, Long page);

    Mono<SlaveCommencementOfClassesDto> showByUuid(UUID uuid);
}