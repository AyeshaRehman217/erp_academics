package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

// This interface wil extends in Student  Hobbies Pvt Repository
public interface SlaveCustomHobbyStudentPvtRepository {

    //used to check existing students ids
    Flux<SlaveHobbyEntity> existingHobbyList(UUID studentUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentHobbiesListWithStatusCheck(UUID studentUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentHobbiesList(UUID studentUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentHobbiesListWithStatus(UUID studentUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

}
