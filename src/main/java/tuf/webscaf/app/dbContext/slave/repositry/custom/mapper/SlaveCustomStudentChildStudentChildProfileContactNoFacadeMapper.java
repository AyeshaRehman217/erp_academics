package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentChildStudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper implements BiFunction<Row, Object, SlaveStudentChildStudentChildProfileContactNoFacadeDto> {

    MultiValueMap<String, SlaveStudentContactNoFacadeDto> studentChildContactNoDtoMultiValueMap = new LinkedMultiValueMap<>();

    @Override
    public SlaveStudentChildStudentChildProfileContactNoFacadeDto apply(Row source, Object o) {

        SlaveStudentContactNoFacadeDto slaveStudentContactNoFacadeDto = null;

        try {
            slaveStudentContactNoFacadeDto = SlaveStudentContactNoFacadeDto.builder()
                    .contactNo(source.get("contactNo", String.class))
                    .contactTypeUUID(source.get("contactTypeUUID", UUID.class))
                    .build();
        } catch (IllegalArgumentException ignored) {
        }

        String studentMetaUuid = source.get("stdMetaUUID", String.class);

        if (studentMetaUuid != null) {
            studentChildContactNoDtoMultiValueMap.add(studentMetaUuid, slaveStudentContactNoFacadeDto);
        }

        return SlaveStudentChildStudentChildProfileContactNoFacadeDto.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .uuid(source.get("uuid", UUID.class))
                .studentUUID(source.get("student_uuid", UUID.class))
                .status(source.get("status", Boolean.class))
                .studentChildAsStudentUUID(source.get("std_child_uuid", UUID.class))
                .studentChildUUID(source.get("studentChildUUID", UUID.class))
                .image(source.get("image", UUID.class))
                .name(source.get("name", String.class))
                .email(source.get("email", String.class))
                .age(source.get("age", Integer.class))
                .nic(source.get("nic", String.class))
                .officialTel(source.get("officialTelephone", String.class))
                .cityUUID(source.get("cityUUID", UUID.class))
                .stateUUID(source.get("stateUUID", UUID.class))
                .countryUUID(source.get("countryUUID", UUID.class))
                .studentChildContactNoDto(studentChildContactNoDtoMultiValueMap.get(studentMetaUuid))
                .createdBy(source.get("created_by", UUID.class))
                .createdAt(source.get("created_at", LocalDateTime.class))
                .updatedBy(source.get("updated_by", UUID.class))
                .updatedAt(source.get("updated_at", LocalDateTime.class))
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
                .deletable(source.get("deletable", Boolean.class))
                .archived(source.get("archived", Boolean.class))
                .editable(source.get("editable", Boolean.class))
                .build();

    }
}
