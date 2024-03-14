package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

public interface SlaveCustomStudentSpouseNationalityPvtRepository {
    Flux<SlaveNationalityEntity> existingStudentSpouseNationalitiesList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentSpouseNationalitiesListWithStatus(UUID teacherSpouseUUID, String name,String description,Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentSpouseNationalitiesList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentSpouseNationalitiesListWithStatus(UUID teacherSpouseUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
