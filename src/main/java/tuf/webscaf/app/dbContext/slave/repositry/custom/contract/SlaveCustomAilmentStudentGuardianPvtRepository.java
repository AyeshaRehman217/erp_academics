package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student Guardian Ailments Pvt Repository
public interface SlaveCustomAilmentStudentGuardianPvtRepository {

    //used to check existing students ids
    Flux<SlaveAilmentEntity> existingAilmentsList(UUID stdGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentGuardianAilmentsListWithStatus(UUID studentGuardianUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentGuardianAilmentsList(UUID studentGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentGuardianAilmentsListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
