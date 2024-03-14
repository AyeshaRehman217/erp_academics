package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student  Ailments Pvt Repository
public interface SlaveCustomAilmentStudentPvtRepository {

    //used to check existing students ids
    Flux<SlaveAilmentEntity> existingStudentAilmentsList(UUID stdUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentAilmentsListWithStatus(UUID stdUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentAilmentsList(UUID stdUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentAilmentsListWithStatus(UUID stdUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
