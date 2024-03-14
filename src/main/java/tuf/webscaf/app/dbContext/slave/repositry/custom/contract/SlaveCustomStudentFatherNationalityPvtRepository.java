package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

// This interface wil extends in  Student Father  Nationality Pvt Repository
public interface SlaveCustomStudentFatherNationalityPvtRepository {

    //used to check existing nationalities ids
    Flux<SlaveNationalityEntity> existingNationalityList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentFatherNationalitiesListWithStatus(UUID studentFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentFatherNationalitiesList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentFatherNationalitiesListWithStatus(UUID studentFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
