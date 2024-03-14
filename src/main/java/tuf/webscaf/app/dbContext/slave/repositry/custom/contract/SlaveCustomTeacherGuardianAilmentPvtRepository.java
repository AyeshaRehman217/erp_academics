package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Teacher Guardian Ailments Pvt Repository
public interface SlaveCustomTeacherGuardianAilmentPvtRepository {

    //used to check existing teacher spouse uuid
    Flux<SlaveAilmentEntity> existingTeacherGuardianAilmentsList(UUID teacherGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherGuardianAilmentsListWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showTeacherGuardianAilmentsList(UUID teacherGuardianUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherGuardianAilmentsListWithStatus(UUID teacherGuardianUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
