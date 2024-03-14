package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherMotherNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherMotherNationalitiesList(UUID teacherMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherMotherNationalitiesListWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherMotherNationalitiesList(UUID teacherMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherMotherNationalitiesListWithStatus(UUID teacherMotherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
