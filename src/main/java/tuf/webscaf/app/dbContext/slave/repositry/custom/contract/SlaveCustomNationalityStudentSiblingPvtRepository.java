package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;

import java.util.UUID;

// This interface wil extends in  Student Sibling  Nationality Pvt Repository
public interface SlaveCustomNationalityStudentSiblingPvtRepository {

    //used to check existing students ids
    Flux<SlaveNationalityEntity> existingStdSiblingNationalityList(UUID stdSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> existingStudentSiblingNationalitiesListWithStatus(UUID studentSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentSiblingNationalitiesList(UUID studentSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveNationalityEntity> showStudentSiblingNationalitiesListWithStatus(UUID studentSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
