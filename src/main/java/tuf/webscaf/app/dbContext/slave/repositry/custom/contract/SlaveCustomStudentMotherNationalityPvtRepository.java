package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomStudentMotherNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingStudentMotherNationalitiesList(UUID studentMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentMotherNationalitiesListWithStatus(UUID studentMotherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentMotherNationalitiesList(UUID studentMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentMotherNationalitiesListWithStatus(UUID studentMotherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
