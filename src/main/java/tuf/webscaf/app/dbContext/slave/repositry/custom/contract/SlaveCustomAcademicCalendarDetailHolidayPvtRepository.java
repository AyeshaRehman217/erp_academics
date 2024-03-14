package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;

import java.util.UUID;

// This interface wil extends in  Academic Calendar Detail Holidays Pvt Repository
public interface SlaveCustomAcademicCalendarDetailHolidayPvtRepository {

    //used to check existing academic calendar detail uuid
    Flux<SlaveHolidayEntity> unmappedAcademicCalendarDetailHolidaysList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHolidayEntity> unmappedAcademicCalendarDetailHolidaysListWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page);

    //Show All Mapped Records with Status Filter
    Flux<SlaveHolidayEntity> showAcademicCalendarDetailHolidaysList(UUID academicCalendarDetailUUID, String name,String description, String dp, String d, Integer size, Long page);

    Flux<SlaveHolidayEntity> showAcademicCalendarDetailHolidaysListWithStatus(UUID academicCalendarDetailUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page);

}
