package tuf.webscaf.app.dbContext.slave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto {

    private Long id;

    private UUID uuid;

    private Long version;

    private Boolean status;

    private UUID studentUUID;

    @Schema(description = "*optional- student uuid of student spouse")
    private UUID studentSpouseAsStudentUUID;

    @Schema(description = "*optional- teacher uuid of student spouse")
    private UUID studentSpouseAsTeacherUUID;

    private UUID studentSpouseUUID;

    private UUID image;

    private String name;

    private String nic;

    private Integer age;

    private String officialTel;

    private UUID cityUUID;

    private UUID stateUUID;

    private UUID countryUUID;

    private UUID genderUUID;

    private Integer noOfDependents;

    @Column("email")
    private String email;

    private List<SlaveStudentContactNoFacadeDto> studentSpouseContactNoDto;

    @CreatedBy
    private UUID createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private UUID updatedBy;

    @LastModifiedDate
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
