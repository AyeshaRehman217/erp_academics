package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Teacher Spouse Ailments Pvt Repository
public interface SlaveCustomTeacherSpouseAilmentPvtRepository {

    //used to check existing teacher spouse uuid
    Flux<SlaveAilmentEntity> existingTeacherSpouseAilmentsList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showTeacherSpouseAilmentsList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
