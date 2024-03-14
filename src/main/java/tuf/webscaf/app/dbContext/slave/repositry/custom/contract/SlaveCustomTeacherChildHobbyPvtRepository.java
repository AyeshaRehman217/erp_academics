package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherChildHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherChildHobbiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherChildHobbiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherChildHobbiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherChildHobbiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
