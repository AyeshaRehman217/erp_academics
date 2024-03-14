package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

public interface SlaveCustomTeacherMotherAilmentPvtRepository {
    Flux<SlaveAilmentEntity> existingTeacherMotherAilmentsList(UUID teacherMotherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherMotherAilmentsListWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showTeacherMotherAilmentsList(UUID teacherMotherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherMotherAilmentsListWithStatus(UUID teacherMotherUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);
}
