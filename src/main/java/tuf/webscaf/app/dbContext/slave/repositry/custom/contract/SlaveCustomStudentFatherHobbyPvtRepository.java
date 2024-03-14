package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomStudentFatherHobbyPvtRepository {
    Flux<SlaveHobbyEntity> existingStudentFatherHobbiesList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentFatherHobbiesListWithStatusCheck(UUID studentFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentFatherHobbiesList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentFatherHobbiesListWithStatus(UUID studentFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
