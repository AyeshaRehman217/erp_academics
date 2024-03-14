package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAttendanceDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;

import java.util.UUID;

// This interface wil extends in Attendances Repository
public interface SlaveCustomAttendanceRepository {

    /**
     * Fetch Attendance Records With & Without Status Filter
     **/
    Flux<SlaveAttendanceDto> indexAttendanceWithStatusFilter(Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page);

    Flux<SlaveAttendanceDto> indexAttendanceWithoutStatusFilter(String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page);

    Flux<SlaveAttendanceDto> indexAttendanceWithStatusAndSubjectFilter(UUID subjectUUID, Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page);

    Flux<SlaveAttendanceDto> indexAttendanceAgainstSubjectWithoutStatusFilter(UUID subjectUUID, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page);
}
