package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomStudentSpouseHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingStudentSpouseHobbiesList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentSpouseHobbiesListWithStatusCheck(UUID teacherSpouseUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentSpouseHobbiesList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentSpouseHobbiesListWithStatus(UUID teacherSpouseUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
