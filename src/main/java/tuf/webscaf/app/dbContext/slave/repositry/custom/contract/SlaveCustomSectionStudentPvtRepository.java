package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;

import java.util.UUID;

// This interface wil extends in Student Group Student Repository
public interface SlaveCustomSectionStudentPvtRepository {

    //used to check unMapped students uuids
    Flux<SlaveStudentEntity> unMappedStudentList(UUID courseOffered, String studentId, String dp, String d, Integer size, Long page);

    //used to check unMapped students uuids with Status filter
    Flux<SlaveStudentEntity> unMappedStudentListWithStatus(UUID courseOffered, String studentId, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> showMappedStudentListAgainstSection(UUID sectionUUID, UUID courseOffered, String studentId, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentEntity> showMappedStudentListAgainstSectionWithStatus(UUID sectionUUID, UUID courseOffered, String studentId, Boolean status, String dp, String d, Integer size, Long page);

}
