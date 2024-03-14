package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherSpouseHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherSpouseHobbiesList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherSpouseHobbiesListWithStatusCheck(UUID teacherSpouseUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherSpouseHobbiesList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherSpouseHobbiesListWithStatus(UUID teacherSpouseUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
