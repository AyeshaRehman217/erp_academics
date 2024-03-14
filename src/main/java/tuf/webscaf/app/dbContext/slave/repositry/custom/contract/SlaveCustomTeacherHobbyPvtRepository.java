package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherHobbiesList(UUID teacherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherHobbiesListWithStatusCheck(UUID teacherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherHobbiesList(UUID teacherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherHobbiesListWithStatus(UUID teacherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
