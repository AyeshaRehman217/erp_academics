package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;

import java.util.UUID;

public interface SlaveCustomStudentChildHobbyPvtRepository {

    Flux<SlaveHobbyEntity> existingStudentChildHobbiesList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> existingStudentChildHobbiesListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentChildHobbiesList(UUID studentChildUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHobbyEntity> showStudentChildHobbiesListWithStatus(UUID studentChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page);
}
