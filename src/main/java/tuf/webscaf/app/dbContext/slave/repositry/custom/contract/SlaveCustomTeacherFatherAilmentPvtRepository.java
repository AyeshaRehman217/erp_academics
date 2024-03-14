package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

public interface SlaveCustomTeacherFatherAilmentPvtRepository {
    Flux<SlaveAilmentEntity> existingTeacherFatherAilmentsList(UUID teacherFatherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherFatherAilmentsListWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showTeacherFatherAilmentsList(UUID teacherFatherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherFatherAilmentsListWithStatus(UUID teacherFatherUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);
}
