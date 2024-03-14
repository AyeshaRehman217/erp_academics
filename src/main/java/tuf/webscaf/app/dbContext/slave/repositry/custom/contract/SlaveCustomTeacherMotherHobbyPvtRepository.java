package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherMotherHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherMotherHobbiesList(UUID teacherMotherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherMotherHobbiesListWithStatusCheck(UUID teacherMotherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherMotherHobbiesList(UUID teacherMotherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherMotherHobbiesListWithStatus(UUID teacherMotherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
