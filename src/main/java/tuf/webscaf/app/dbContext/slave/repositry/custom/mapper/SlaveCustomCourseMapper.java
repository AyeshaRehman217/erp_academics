package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomCourseMapper implements BiFunction<Row, Object, SlaveCourseEntity> {
    @Override
    public SlaveCourseEntity apply(Row source, Object o) {
        return SlaveCourseEntity.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .uuid(source.get("uuid", UUID.class))
                .status(source.get("status", Boolean.class))
                .name(source.get("key", String.class))
                .slug(source.get("slug", String.class))
                .description(source.get("description", String.class))
                .code(source.get("code", String.class))
                .shortName(source.get("short_name", String.class))
                .duration(source.get("duration", String.class))
                .isSemester(source.get("is_semester", Boolean.class))
                .noOfSemester(source.get("no_of_semester", Integer.class))
                .noOfAnnuals(source.get("no_of_annuals", Integer.class))
                .departmentUUID(source.get("department_uuid", UUID.class))
                .eligibilityCriteria(source.get("eligibility_criteria", String.class))
                .maximumAgeLimit(source.get("maximum_age_limit", Integer.class))
                .minimumAgeLimit(source.get("minimum_age_limit", Integer.class))
                .courseLevelUUID(source.get("course_level_uuid", UUID.class))
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
