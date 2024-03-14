package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomStudentMotherHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingStudentMotherHobbiesList(UUID studentMotherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentMotherHobbiesListWithStatusCheck(UUID studentMotherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentMotherHobbiesList(UUID studentMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentMotherHobbiesListWithStatus(UUID studentMotherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
