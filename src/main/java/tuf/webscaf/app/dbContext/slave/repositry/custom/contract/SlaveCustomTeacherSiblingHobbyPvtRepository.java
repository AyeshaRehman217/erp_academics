package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherSiblingHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherSiblingHobbiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherSiblingHobbiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherSiblingHobbiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherSiblingHobbiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
