package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherGuardianHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherGuardianHobbiesList(UUID teacherGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherGuardianHobbiesListWithStatusCheck(UUID teacherGuardianUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherGuardianHobbiesList(UUID teacherGuardianUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherGuardianHobbiesListWithStatus(UUID teacherGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
