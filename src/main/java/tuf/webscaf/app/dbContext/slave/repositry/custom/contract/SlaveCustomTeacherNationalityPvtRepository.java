package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherNationalitiesList(UUID teacherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherNationalitiesListWithStatus(UUID teacherUUID, String name,String description,Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherNationalitiesList(UUID teacherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherNationalitiesListWithStatus(UUID teacherUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
