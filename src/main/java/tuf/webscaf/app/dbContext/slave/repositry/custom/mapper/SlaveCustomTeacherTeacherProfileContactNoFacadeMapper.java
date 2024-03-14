package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;

import io.r2dbc.spi.Row;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

public class SlaveCustomTeacherTeacherProfileContactNoFacadeMapper implements BiFunction<Row, Object, SlaveTeacherTeacherProfileContactNoFacadeDto> {

    MultiValueMap<String, SlaveTeacherContactNoFacadeDto> teacherContactNoDtoMultiValueMap = new LinkedMultiValueMap<>();

    @Override
    public SlaveTeacherTeacherProfileContactNoFacadeDto apply(Row source, Object o) {

        SlaveTeacherContactNoFacadeDto teacherContactNoDto = null;

        try {
            teacherContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                    .contactNo(source.get("contactNo", String.class))
                    .contactTypeUUID(source.get("contactTypeUUID", UUID.class))
                    .build();
        } catch (IllegalArgumentException ignored) {
        }

        String teacherMetaUuid = source.get("teacherMetaUUID", String.class);

        if (teacherMetaUuid != null) {
            teacherContactNoDtoMultiValueMap.add(teacherMetaUuid, teacherContactNoDto);
        }

        return SlaveTeacherTeacherProfileContactNoFacadeDto.builder()
                .id(source.get("id", Long.class))
                .version(source.get("version", Long.class))
                .uuid(source.get("uuid", UUID.class))
                .teacherUUID(source.get("teacher_uuid", UUID.class))
                .status(source.get("status", Boolean.class))
                .campusUUID(source.get("campus_uuid", UUID.class))
                .email(source.get("email", String.class))
                .campusUUID(source.get("campus_uuid", UUID.class))
                .image(source.get("image", UUID.class))
                .firstName(source.get("firstName", String.class))
                .lastName(source.get("lastName", String.class))
                .email(source.get("email", String.class))
                .telephoneNo(source.get("telephoneNo", String.class))
                .nic(source.get("nic", String.class))
                .birthDate(source.get("birthDate", LocalDateTime.class))
                .cityUUID(source.get("cityUUID", UUID.class))
                .stateUUID(source.get("stateUUID", UUID.class))
                .countryUUID(source.get("countryUUID", UUID.class))
                .religionUUID(source.get("religionUUID", UUID.class))
                .sectUUID(source.get("sectUUID", UUID.class))
                .casteUUID(source.get("casteUUID", UUID.class))
                .genderUUID(source.get("genderUUID", UUID.class))
                .maritalStatusUUID(source.get("maritalStatusUUID", UUID.class))
                .teacherContactNoDto(teacherContactNoDtoMultiValueMap.get(teacherMetaUuid))
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
