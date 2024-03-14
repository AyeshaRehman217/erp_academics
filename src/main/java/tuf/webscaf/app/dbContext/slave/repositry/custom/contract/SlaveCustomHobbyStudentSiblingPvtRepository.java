package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

// This interface wil extends in  Student Sibling  Hobbies Pvt Repository
public interface SlaveCustomHobbyStudentSiblingPvtRepository {

    //used to check existing students ids
    Flux<SlaveHobbyEntity> existingHobbyList(UUID stdSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentSiblingHobbiesListWithStatus(UUID studentSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentSiblingHobbiesList(UUID studentSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentSiblingHobbiesListWithStatus(UUID studentSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
