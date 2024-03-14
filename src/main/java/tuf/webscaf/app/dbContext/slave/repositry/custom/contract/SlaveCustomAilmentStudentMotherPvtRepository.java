package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student Mother  Ailments Pvt Repository
public interface SlaveCustomAilmentStudentMotherPvtRepository {

    //used to check existing students ids
    Flux<SlaveAilmentEntity> existingAilmentsList(UUID stdMotherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentMotherAilmentsListWithStatus(UUID studentMother, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentMotherAilmentsList(UUID studentMother, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentMotherAilmentsListWithStatus(UUID studentMother, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
