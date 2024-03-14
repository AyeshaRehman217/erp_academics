package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherSpouseNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherSpouseNationalitiesList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherSpouseNationalitiesListWithStatus(UUID teacherSpouseUUID, String name,String description,Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherSpouseNationalitiesList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherSpouseNationalitiesListWithStatus(UUID teacherSpouseUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
