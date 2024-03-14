package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

// This interface wil extends in  Student Guardian Hobbies Pvt Repository
public interface SlaveCustomHobbyStudentGuardianPvtRepository {

    //used to check existing students ids
    Flux<SlaveHobbyEntity> existingHobbyList(UUID stdGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentGuardianHobbiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentGuardianHobbiesList(UUID studentGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentGuardianHobbiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
