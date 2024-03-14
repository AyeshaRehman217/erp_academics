package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

// This interface wil extends in  Student Guardian Nationality Pvt Repository
public interface SlaveCustomNationalityStudentGuardianPvtRepository {

    //used to check existing students ids
    Flux<SlaveNationalityEntity> existingNationalityList(UUID stdGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentGuardianNationalitiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentGuardianNationalitiesList(UUID studentGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentGuardianNationalitiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
