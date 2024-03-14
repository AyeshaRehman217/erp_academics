package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOutlineOfferedDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineOfferedEntity;

import java.util.UUID;

// This interface wil extends in  SubjectOutline Offered Slave repository
public interface SlaveCustomSubjectOutlineOfferedRepository {

    //index/paginate records with and without Status Filter
    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOffered(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusCheck(String name, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Fetch subject Outline Offered where Obe is true so Subject OBE name will be fetched
     **/
    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedAgainstOBETrue(Boolean obe, String name, String dp, String d, Integer size, Long page);

    /**
     * Fetch subject Outline Offered where Obe is true so Subject Outline name will be fetched
     **/
    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedAgainstOBEFalse(Boolean obe, String name, String dp, String d, Integer size, Long page);

    /**
     * Fetch subject Outline Offered where Obe && Status is true so Subject OBE name will be fetched
     **/
    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusAndOBETrue(Boolean obe, Boolean status, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusAndOBEFalse(Boolean obe, Boolean status, String name, String dp, String d, Integer size, Long page);

    //show subject Outline Offered Record Against UUID
    Mono<SlaveSubjectOutlineOfferedDto> showSubjectOutlineOffered(UUID subjectOutlineOfferedUUID);

    //show subject Outline Offered Record Against UUID
    Mono<SlaveSubjectOutlineOfferedDto> showSubjectOutlineOfferedAgainstOBE(UUID subjectOutlineOfferedUUID, Boolean obe);

}
