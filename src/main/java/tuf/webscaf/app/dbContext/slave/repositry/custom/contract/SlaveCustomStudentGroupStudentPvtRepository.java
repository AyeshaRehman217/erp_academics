package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;

import java.util.UUID;

public interface SlaveCustomStudentGroupStudentPvtRepository {

    //Show All UnMapped Records with Status Filter
    Flux<SlaveStudentEntity> showUnMappedStudentGroupStudentsList(UUID studentGroupUUID, String studentId, String officialEmail, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> showUnMappedStudentGroupStudentsListWithStatus(UUID studentGroupUUID, String studentId, String officialEmail, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveStudentEntity> showMappedStudentGroupStudentsList(UUID studentGroupUUID, String studentId,String officialEmail, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> showMappedStudentGroupStudentsListWithStatus(UUID studentGroupUUID, Boolean status, String studentId,String officialEmail, String dp, String d, Integer size, Long page);

}
