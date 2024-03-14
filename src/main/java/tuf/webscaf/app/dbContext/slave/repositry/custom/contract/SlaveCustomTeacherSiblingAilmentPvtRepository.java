package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

public interface SlaveCustomTeacherSiblingAilmentPvtRepository {
    Flux<SlaveAilmentEntity> existingTeacherSiblingAilmentsList(UUID teacherSiblingUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherSiblingAilmentsListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherSiblingAilmentsList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherSiblingAilmentsListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
