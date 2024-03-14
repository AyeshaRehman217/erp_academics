package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.util.UUID;

// This interface wil extends in  Student Spouse Ailments Pvt Repository
public interface SlaveCustomStudentSpouseAilmentPvtRepository {

    //used to check existing teacher spouse uuid
    Flux<SlaveAilmentEntity> existingStudentSpouseAilmentsList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> existingStudentSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAilmentEntity> showStudentSpouseAilmentsList(UUID teacherSpouseUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAilmentEntity> showStudentSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
