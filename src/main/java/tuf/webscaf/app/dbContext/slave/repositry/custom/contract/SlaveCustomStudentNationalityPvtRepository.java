package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomStudentNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingStudentNationalitiesList(UUID studentUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentNationalitiesListWithStatus(UUID studentUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentNationalitiesList(UUID studentUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentNationalitiesListWithStatus(UUID studentUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
