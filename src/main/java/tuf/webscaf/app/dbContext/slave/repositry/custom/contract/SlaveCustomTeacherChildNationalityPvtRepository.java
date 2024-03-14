package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherChildNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherChildNationalitiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherChildNationalitiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
