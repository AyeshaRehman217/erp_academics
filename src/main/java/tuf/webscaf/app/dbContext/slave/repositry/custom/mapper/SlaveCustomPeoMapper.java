package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomPeoMapper implements BiFunction<Row, Object, SlavePeoEntity> {
    @Override
    public SlavePeoEntity apply(Row source, Object o) {
        return SlavePeoEntity.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .departmentUUID(source.get("department_uuid", UUID.class))
                .uuid(source.get("uuid", UUID.class))
                .name(source.get("name", String.class))
                .code(source.get("code", String.class))
                .description(source.get("description", String.class))
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
