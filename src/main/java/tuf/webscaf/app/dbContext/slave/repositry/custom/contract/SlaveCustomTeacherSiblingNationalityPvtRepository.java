package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherSiblingNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherSiblingNationalitiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherSiblingNationalitiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherSiblingNationalitiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherSiblingNationalitiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}

