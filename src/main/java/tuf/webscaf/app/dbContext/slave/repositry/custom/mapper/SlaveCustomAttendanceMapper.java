package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAttendanceDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomAttendanceMapper implements BiFunction<Row, Object, SlaveAttendanceDto> {

    @Override
    public SlaveAttendanceDto apply(Row source, Object o) {
        return SlaveAttendanceDto.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .uuid(source.get("uuid", UUID.class))
                .key(source.get("key", String.class))
                .attendanceTypeUUID(source.get("attendance_type_uuid", UUID.class))
                .commencementOfClassesUUID(source.get("commencement_of_classes_uuid", UUID.class))
                .markedBy(source.get("marked_by", UUID.class))
                .subjectCode(source.get("subjectCode", String.class))
                .subjectName(source.get("subjectName", String.class))
                .studentID(source.get("studentID", String.class))
                .lectureTypeName(source.get("lectureTypeName", String.class))
                .day(source.get("day", String.class))
                .startTime(source.get("startTime", LocalTime.class))
                .endTime(source.get("endTime", LocalTime.class))
                .status(source.get("status", Boolean.class))
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
