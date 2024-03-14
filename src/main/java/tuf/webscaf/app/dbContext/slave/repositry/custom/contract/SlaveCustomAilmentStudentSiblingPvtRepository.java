package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student Sibling  Ailments Pvt Repository
public interface SlaveCustomAilmentStudentSiblingPvtRepository {

    //used to check existing students ids
    Flux<SlaveAilmentEntity> existingAilmentsList(UUID stdSiblingUUID, String name,String description, String dp, String d, Integer size, Long page);


    Flux<SlaveAilmentEntity> existingStudentSiblingAilmentsListWithStatus(UUID studentSiblingUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentSiblingAilmentsList(UUID studentSiblingUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentSiblingAilmentsListWithStatus(UUID studentSiblingUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
