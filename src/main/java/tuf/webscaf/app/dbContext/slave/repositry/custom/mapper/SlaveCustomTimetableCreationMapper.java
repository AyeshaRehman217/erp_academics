package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTimetableCreationDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomTimetableCreationMapper implements BiFunction<Row, Object, SlaveTimetableCreationDto> {
    @Override
    public SlaveTimetableCreationDto apply(Row source, Object o) {

        return SlaveTimetableCreationDto.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .uuid(source.get("uuid", UUID.class))
                .studentUUID(source.get("studentUUID", UUID.class))
                .subjectUUID(source.get("subject_uuid", UUID.class))
                .enrollmentUUID(source.get("enrollment_uuid", UUID.class))
                .studentGroupUUID(source.get("student_group_uuid", UUID.class))
                .teacherUUID(source.get("teacher_uuid", UUID.class))
                .classroomUUID(source.get("classroom_uuid", UUID.class))
                .sectionUUID(source.get("section_uuid", UUID.class))
                .academicSessionUUID(source.get("academic_session_uuid", UUID.class))
                .lectureTypeUUID(source.get("lecture_type_uuid", UUID.class))
                .lectureDeliveryModeUUID(source.get("lecture_delivery_mode_uuid", UUID.class))
                .dayUUID(source.get("day", UUID.class))
                .priority(source.get("priority", Integer.class))
                .key(source.get("key", String.class))
                .description(source.get("description", String.class))
                .date(source.get("rescheduled_date", LocalDateTime.class))
                .startTime(source.get("start_time", LocalTime.class))
                .endTime(source.get("end_time", LocalTime.class))
                .status(source.get("status", Boolean.class))
                .rescheduled(source.get("is_rescheduled", Boolean.class))
                .createdBy(source.get("created_by", UUID.class))
                .createdAt(source.get("created_at", LocalDateTime.class))
                .updatedBy(source.get("updated_by", UUID.class))
                .updatedAt(source.get("updated_at", LocalDateTime.class))
                .deletable(source.get("deletable", Boolean.class))
                .archived(source.get("archived", Boolean.class))
                .editable(source.get("editable", Boolean.class))
                .reqCompanyUUID(source.get("req_company_uuid", UUID.class))
                .reqBranchUUID(source.get("req_branch_uuid", UUID.class))
                .reqCreatedIP(source.get("req_created_ip", String.class))
                .reqCreatedPort(source.get("req_created_port", String.class))
                .reqCreatedBrowser(source.get("req_created_browser", String.class))
                .reqCreatedOS(source.get("req_created_os", String.class))
                .reqCreatedDevice(source.get("req_created_device", String.class))
                .reqCreatedReferer(source.get("req_created_referer", String.class))
                .reqUpdatedIP(source.get("req_updated_ip", String.class))
                .reqUpdatedPort(source.get("req_updated_port", String.class))
                .reqUpdatedBrowser(source.get("req_updated_browser", String.class))
                .reqUpdatedOS(source.get("req_updated_os", String.class))
                .reqUpdatedDevice(source.get("req_updated_device", String.class))
                .reqUpdatedReferer(source.get("req_updated_referer", String.class))
                .build();

    }
}
