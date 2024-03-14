package tuf.webscaf.app.dbContext.master.dto;

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
public class TeacherChildProfileContactNoFacadeDto {

    @Schema(hidden = true)
    private Long id;

    @Schema(hidden = true)
    private UUID uuid;

    @Schema(hidden = true)
    private Long version;

    private Boolean status;

    @Schema(description = "*optional- student uuid of teacher child")
    private UUID studentUUID;

    @Schema(required = true)
    private UUID image;

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private String nic;

    @Schema(required = true)
    private Integer age;

    private String officialTel;

    @Schema(required = true)
    private UUID cityUUID;

    @Schema(required = true)
    private UUID stateUUID;

    @Schema(required = true)
    private UUID countryUUID;

    @Schema(required = true)
    private UUID genderUUID;

    @Column("email")
    private String email;

    @Schema(required = true)
    private List<TeacherContactNoDto> teacherChildContactNoDto;

    @Schema(hidden = true)
    @CreatedBy
    private UUID createdBy;

    @Schema(hidden = true)
    @CreatedDate
    private LocalDateTime createdAt;

    @Schema(hidden = true)
    @CreatedBy
    private UUID updatedBy;

    @Schema(hidden = true)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Schema(hidden = true)
    private UUID deletedBy;

    @Schema(hidden = true)
    private LocalDateTime deletedAt;

    @Schema(hidden = true)
    private UUID reqCompanyUUID;

    @Schema(hidden = true)
    private UUID reqBranchUUID;

    @Schema(hidden = true)
    private String reqCreatedBrowser;

    @Schema(hidden = true)
    private String reqCreatedIP;

    @Schema(hidden = true)
    private String reqCreatedPort;

    @Schema(hidden = true)
    private String reqCreatedOS;

    @Schema(hidden = true)
    private String reqCreatedDevice;

    @Schema(hidden = true)
    private String reqCreatedReferer;

    @Schema(hidden = true)
    private String reqUpdatedBrowser;

    @Schema(hidden = true)
    private String reqUpdatedIP;

    @Schema(hidden = true)
    private String reqUpdatedPort;

    @Schema(hidden = true)
    private String reqUpdatedOS;

    @Schema(hidden = true)
    private String reqUpdatedDevice;

    @Schema(hidden = true)
    private String reqUpdatedReferer;

    @Schema(hidden = true)
    private String reqDeletedBrowser;

    @Schema(hidden = true)
    private String reqDeletedIP;

    @Schema(hidden = true)
    private String reqDeletedPort;

    @Schema(hidden = true)
    private String reqDeletedOS;

    @Schema(hidden = true)
    private String reqDeletedDevice;

    @Schema(hidden = true)
    private String reqDeletedReferer;

    @Schema(hidden = true)
    private Boolean editable;

    @Schema(hidden = true)
    private Boolean deletable;

    @Schema(hidden = true)
    private Boolean archived;
}
