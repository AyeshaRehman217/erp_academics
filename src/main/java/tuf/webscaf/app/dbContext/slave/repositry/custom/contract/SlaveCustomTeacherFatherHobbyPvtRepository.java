package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomTeacherFatherHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingTeacherFatherHobbiesList(UUID teacherFatherUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingTeacherFatherHobbiesListWithStatusCheck(UUID teacherFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherFatherHobbiesList(UUID teacherFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showTeacherFatherHobbiesListWithStatus(UUID teacherFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
