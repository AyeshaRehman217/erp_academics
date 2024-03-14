package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomStudentChildNationalityPvtRepository {

    Flux<SlaveNationalityEntity> existingStudentChildNationalitiesList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentChildNationalitiesListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentChildNationalitiesList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentChildNationalitiesListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
