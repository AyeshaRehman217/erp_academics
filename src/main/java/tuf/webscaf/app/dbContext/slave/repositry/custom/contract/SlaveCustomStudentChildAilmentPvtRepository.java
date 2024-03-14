package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

public interface SlaveCustomStudentChildAilmentPvtRepository {

    Flux<SlaveAilmentEntity> existingStudentChildAilmentsList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentChildAilmentsListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentChildAilmentsList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentChildAilmentsListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
