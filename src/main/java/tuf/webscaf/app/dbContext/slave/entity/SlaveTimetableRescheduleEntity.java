package tuf.webscaf.app.dbContext.slave.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("public.\"timetable_creations\"")
public class SlaveTimetableRescheduleEntity {

    @Id
    @Column
    private Long id;

    @Version
    private Long version;

    @Column("status")
    private Boolean status;

    @Column("is_rescheduled")
    private Boolean rescheduled;

    @Column("uuid")
    private UUID uuid;

    @Column("description")
    private String description;

    @Column("rescheduled_date")
    private LocalDateTime rescheduledDate;

    @Column("start_time")
    private LocalTime startTime;

    @Column("end_time")
    private LocalTime endTime;

    @Column("subject_uuid")
    private UUID subjectUUID;

    @Column("teacher_uuid")
    private UUID teacherUUID;

    @Column("classroom_uuid")
    private UUID classroomUUID;

    @Column("enrollment_uuid")
    private UUID enrollmentUUID;

    @Column("student_group_uuid")
    private UUID studentGroupUUID;

    @Column("section_uuid")
    private UUID sectionUUID;

    @Column("academic_session_uuid")
    private UUID academicSessionUUID;

    @Column("lecture_type_uuid")
    private UUID lectureTypeUUID;

    @Column("lecture_delivery_mode_uuid")
    private UUID lectureDeliveryModeUUID;

    @Column("day")
    private UUID dayUUID;

    @Column("created_by")
    @CreatedBy
    private UUID createdBy;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_by")
    @CreatedBy
    private UUID updatedBy;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("deleted_by")
    private UUID deletedBy;

    @Column("deleted_at")
    private LocalDateTime deletedAt;

    @Column("req_company_uuid")
    private UUID reqCompanyUUID;

    @Column("req_branch_uuid")
    private UUID reqBranchUUID;

    @Column("req_created_browser")
    private String reqCreatedBrowser;

    @Column("req_created_ip")
    private String reqCreatedIP;

    @Column("req_created_port")
    private String reqCreatedPort;

    @Column("req_created_os")
    private String reqCreatedOS;

    @Column("req_created_device")
    private String reqCreatedDevice;

    @Column("req_created_referer")
    private String reqCreatedReferer;

    @Column("req_updated_browser")
    private String reqUpdatedBrowser;

    @Column("req_updated_ip")
    private String reqUpdatedIP;

    @Column("req_updated_port")
    private String reqUpdatedPort;

    @Column("req_updated_os")
    private String reqUpdatedOS;

    @Column("req_updated_device")
    private String reqUpdatedDevice;

    @Column("req_updated_referer")
    private String reqUpdatedReferer;

    @Column("req_deleted_browser")
    private String reqDeletedBrowser;

    @Column("req_deleted_ip")
    private String reqDeletedIP;

    @Column("req_deleted_port")
    private String reqDeletedPort;

    @Column("req_deleted_os")
    private String reqDeletedOS;

    @Column("req_deleted_device")
    private String reqDeletedDevice;

    @Column("req_deleted_referer")
    private String reqDeletedReferer;

    @Column
    private Boolean editable;

    @Column
    private Boolean deletable;

    @Column
    private Boolean archived;
}
