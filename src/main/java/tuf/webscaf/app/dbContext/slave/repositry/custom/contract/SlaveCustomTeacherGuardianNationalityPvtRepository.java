package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomTeacherGuardianNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingTeacherGuardianNationalitiesList(UUID teacherGuardianUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingTeacherGuardianNationalitiesListWithStatus(UUID teacherGuardianUUID, String name,String description,Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherGuardianNationalitiesList(UUID teacherGuardianUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showTeacherGuardianNationalitiesListWithStatus(UUID teacherGuardianUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
