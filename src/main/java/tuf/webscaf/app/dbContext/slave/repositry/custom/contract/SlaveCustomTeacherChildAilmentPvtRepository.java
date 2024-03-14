package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

public interface SlaveCustomTeacherChildAilmentPvtRepository {
    Flux<SlaveAilmentEntity> existingTeacherChildAilmentsList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingTeacherChildAilmentsListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherChildAilmentsList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showTeacherChildAilmentsListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
