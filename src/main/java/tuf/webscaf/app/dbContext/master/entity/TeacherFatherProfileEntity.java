package tuf.webscaf.app.dbContext.master.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("public.\"teacher_fth_profiles\"")
public class TeacherFatherProfileEntity {

    @Id
    @Column
    @Schema(hidden = true)
    private Long id;

    @Version
    @Schema(hidden = true)
    private Long version;

    @Column("status")
    private Boolean status;

    @Column("uuid")
    @Schema(hidden = true)
    private UUID uuid;

    @Column("teacher_father_uuid")
    @Schema(required = true)
    private UUID teacherFatherUUID;

    @Column("image")
    @Schema(required = true)
    private UUID image;

    @Column("name")
    @Schema(required = true)
    private String name;

    @Column("nic")
    @Schema(required = true)
    private String nic;

    @Column("age")
    @Schema(required = true)
    private Integer age;

    @Column("city_uuid")
    @Schema(required = true)
    private UUID cityUUID;

    @Column("state_uuid")
    @Schema(required = true)
    private UUID stateUUID;

    @Column("country_uuid")
    @Schema(required = true)
    private UUID countryUUID;

    @Column("no_of_dependents")
    @Schema(required = true)
    private Integer noOfDependents;

    @Column("official_tel")
    private String officialTel;

//    @Column("contact_no_uuid")
//    @Schema(required = true)
//    private UUID contactNoUUID;

    @Column("email")
    private String email;

    @Column("created_by")
    @Schema(hidden = true)
    @CreatedBy
    private UUID createdBy;

    @Column("created_at")
    @Schema(hidden = true)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_by")
    @Schema(hidden = true)
    @CreatedBy
    private UUID updatedBy;

    @Column("updated_at")
    @Schema(hidden = true)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("deleted_by")
    @Schema(hidden = true)
    private UUID deletedBy;

    @Column("deleted_at")
    @Schema(hidden = true)
    private LocalDateTime deletedAt;

    @Column("req_company_uuid")
    @Schema(hidden = true)
    private UUID reqCompanyUUID;

    @Column("req_branch_uuid")
    @Schema(hidden = true)
    private UUID reqBranchUUID;

    @Column("req_created_browser")
    @Schema(hidden = true)
    private String reqCreatedBrowser;

    @Column("req_created_ip")
    @Schema(hidden = true)
    private String reqCreatedIP;

    @Column("req_created_port")
    @Schema(hidden = true)
    private String reqCreatedPort;

    @Column("req_created_os")
    @Schema(hidden = true)
    private String reqCreatedOS;

    @Column("req_created_device")
    @Schema(hidden = true)
    private String reqCreatedDevice;

    @Column("req_created_referer")
    @Schema(hidden = true)
    private String reqCreatedReferer;

    @Column("req_updated_browser")
    @Schema(hidden = true)
    private String reqUpdatedBrowser;

    @Column("req_updated_ip")
    @Schema(hidden = true)
    private String reqUpdatedIP;

    @Column("req_updated_port")
    @Schema(hidden = true)
    private String reqUpdatedPort;

    @Column("req_updated_os")
    @Schema(hidden = true)
    private String reqUpdatedOS;

    @Column("req_updated_device")
    @Schema(hidden = true)
    private String reqUpdatedDevice;

    @Column("req_updated_referer")
    @Schema(hidden = true)
    private String reqUpdatedReferer;

    @Column("req_deleted_browser")
    @Schema(hidden = true)
    private String reqDeletedBrowser;

    @Column("req_deleted_ip")
    @Schema(hidden = true)
    private String reqDeletedIP;

    @Column("req_deleted_port")
    @Schema(hidden = true)
    private String reqDeletedPort;

    @Column("req_deleted_os")
    @Schema(hidden = true)
    private String reqDeletedOS;

    @Column("req_deleted_device")
    @Schema(hidden = true)
    private String reqDeletedDevice;

    @Column("req_deleted_referer")
    @Schema(hidden = true)
    private String reqDeletedReferer;

    @Column("editable")
    @Schema(hidden = true)
    private Boolean editable;

    @Column("deletable")
    @Schema(hidden = true)
    private Boolean deletable;

    @Column("archived")
    @Schema(hidden = true)
    private Boolean archived;
}