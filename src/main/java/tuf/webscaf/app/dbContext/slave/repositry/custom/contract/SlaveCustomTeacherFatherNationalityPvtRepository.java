package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherFatherNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherFatherNationalitiesList(UUID teacherFatherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherFatherNationalitiesListWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherFatherNationalitiesList(UUID teacherFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherFatherNationalitiesListWithStatus(UUID teacherFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
