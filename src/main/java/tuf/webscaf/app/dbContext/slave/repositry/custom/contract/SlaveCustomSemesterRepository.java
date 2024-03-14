package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSemesterEntity;

import java.util.UUID;

// This interface wil extends in Slave Semester Repository
public interface SlaveCustomSemesterRepository {

    //fetch All records with status and without status filter

    Flux<SlaveSemesterEntity> showMappedSemestersAgainstAcademicCalendarWithOutStatus(String name, UUID academicCalendarUUID, String dp, String d, Integer size, Long page);

    Flux<SlaveSemesterEntity> showMappedSemestersAgainstAcademicCalendarWithStatus(String name, UUID academicCalendarUUID, Boolean status, String dp, String d, Integer size, Long page);

    /**
     * Getting Un Mapped List of Semesters against Academic Calendar UUID
     **/
    Flux<SlaveSemesterEntity> showUnMappedSemestersAgainstAcademicCalendar(String name, UUID academicSessionUUID, UUID courseLevelUUID, String dp, String d, Integer size, Long page);

    Flux<SlaveSemesterEntity> unMappedSemestersAgainstAcademicCalendarWithStatus(String name, UUID academicSessionUUID, UUID courseLevelUUID, Boolean status, String dp, String d, Integer size, Long page);


    Flux<SlaveSemesterEntity> showSemestersAgainstCourseWithOutStatus(String name, UUID courseUUID, UUID studentUUID, String dp, String d, Integer size, Long page);

    Flux<SlaveSemesterEntity> showSemestersAgainstCourseWithStatus(String name, UUID courseUUID, UUID studentUUID, Boolean status, String dp, String d, Integer size, Long page);

}
