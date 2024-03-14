package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAcademicCalendarDetailDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;

import java.util.UUID;

// This interface wil extends in  Academic Calendar Details Repository
public interface SlaveCustomAcademicCalendarDetailRepository {

    /*** Fetch Academic Calendar Details Against Academic Calendar & Academic Session (With & Without Status Filter)***/
    //used to check existing academic calendar detail uuid
    Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarAndSessionWithStatus(UUID academicCalendarUUID, UUID academicSessionUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarAndSessionWithoutStatus(UUID academicCalendarUUID, UUID academicSessionUUID, String key, String comments, String dp, String d, Integer size, Long page);

    /*** Fetch Academic Calendar Details Against Academic Calendar (With & Without Status Filter) ***/
    //used to check existing academic calendar detail uuid
    Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarWithStatus(UUID academicCalendarUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarWithoutStatus(UUID academicCalendarUUID, String key, String comments, String dp, String d, Integer size, Long page);

    /*** Fetch Academic Calendar Details Against Academic Session (With & Without Status Filter) ***/
    Flux<SlaveAcademicCalendarDetailDto> indexAgainstSessionWithStatus(UUID academicSessionUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarDetailDto> indexAgainstSessionWithoutStatus(UUID academicSessionUUID, String key, String comments, String dp, String d, Integer size, Long page);

    /*** Fetch Academic Calendar Details Against Academic Session (With & Without Status Filter) ***/
    Flux<SlaveAcademicCalendarDetailDto> indexWithStatus(Boolean status, String key, String comments, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarDetailDto> indexWithoutStatus(String key, String comments, String dp, String d, Integer size, Long page);
}
