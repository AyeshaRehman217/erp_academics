package tuf.webscaf.app.dbContext.slave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import tuf.webscaf.app.dbContext.master.dto.StudentContactNoDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherContactNoDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveTeacherTeacherProfileContactNoFacadeDto {

    private Long id;

    private UUID uuid;

    private Long version;

    private String employeeCode;

    private Boolean status;

    private UUID campusUUID;

    private UUID teacherUUID;

    private UUID deptRankUUID;

    private UUID image;

    private String firstName;

    private String lastName;

    private String email;

    private String telephoneNo;

    @Schema(description = "This is the teacher uuid to whom this teacher is reporting to")
    private UUID reportingTo;

    private String nic;

    private LocalDateTime birthDate;

    private UUID cityUUID;

    private UUID stateUUID;

    private UUID countryUUID;

    private UUID religionUUID;

    private UUID sectUUID;

    private UUID casteUUID;

    private UUID genderUUID;

    private UUID maritalStatusUUID;

    private List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto;

    private UUID createdBy;

    private LocalDateTime createdAt;

    private UUID updatedBy;

    private LocalDateTime updatedAt;

    private UUID deletedBy;

    private LocalDateTime deletedAt;

    private UUID reqCompanyUUID;

    private UUID reqBranchUUID;

    private String reqCreatedBrowser;

    private String reqCreatedIP;

    private String reqCreatedPort;

    private String reqCreatedOS;

    private String reqCreatedDevice;

    private String reqCreatedReferer;

    private String reqUpdatedBrowser;

    private String reqUpdatedIP;

    private String reqUpdatedPort;

    private String reqUpdatedOS;

    private String reqUpdatedDevice;

    private String reqUpdatedReferer;

    private String reqDeletedBrowser;

    private String reqDeletedIP;

    private String reqDeletedPort;

    private String reqDeletedOS;

    private String reqDeletedDevice;

    private String reqDeletedReferer;

    private Boolean editable;

    private Boolean deletable;

    private Boolean archived;
}
