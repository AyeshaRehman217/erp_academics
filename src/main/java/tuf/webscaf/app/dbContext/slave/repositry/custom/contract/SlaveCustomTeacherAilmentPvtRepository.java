package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Teacher Ailments Pvt Repository
public interface SlaveCustomTeacherAilmentPvtRepository {

    //used to check existing teacher uuid
    Flux<SlaveAilmentEntity> existingTeacherAilmentsList(UUID teacherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherAilmentsListWithStatus(UUID teacherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showTeacherAilmentsList(UUID teacherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherAilmentsListWithStatus(UUID teacherUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
