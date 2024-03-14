package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student Father  Ailments Pvt Repository
public interface SlaveCustomAilmentStudentFatherPvtRepository {

    //used to check existing students ids
    Flux<SlaveAilmentEntity> existingAilmentsList(UUID stdFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentFatherAilmentsListWithStatus(UUID stdFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentFatherAilmentsList(UUID stdFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentFatherAilmentsListWithStatus(UUID stdFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
