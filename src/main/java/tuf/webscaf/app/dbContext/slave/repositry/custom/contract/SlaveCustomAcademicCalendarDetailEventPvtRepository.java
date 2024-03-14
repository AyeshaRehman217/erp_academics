package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;

import java.util.UUID;

// This interface wil extends in  Academic Calendar Detail Events Pvt Repository
public interface SlaveCustomAcademicCalendarDetailEventPvtRepository {

    //used to check existing academic calendar detail uuid
    Flux<SlaveAcademicCalendarEventEntity> unmappedAcademicCalendarDetailEventsList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarEventEntity> unmappedAcademicCalendarDetailEventsListWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveAcademicCalendarEventEntity> showAcademicCalendarDetailEventsList(UUID academicCalendarDetailUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicCalendarEventEntity> showAcademicCalendarDetailEventsListWithStatus(UUID academicCalendarDetailUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
