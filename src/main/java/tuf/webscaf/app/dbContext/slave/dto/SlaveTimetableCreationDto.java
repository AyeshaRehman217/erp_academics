package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveTimetableCreationDto {

    private Long id;

    private Long version;

    private Boolean status;

    private Boolean rescheduled;

    private UUID uuid;

    private UUID studentUUID;

    private Integer priority;

    private String key;

    private String description;

    private LocalDateTime date;

    private LocalTime startTime;

    private LocalTime endTime;

    private UUID subjectUUID;

    private UUID teacherUUID;

    private UUID classroomUUID;

    private UUID enrollmentUUID;

    private UUID studentGroupUUID;

    private UUID sectionUUID;

    private UUID academicSessionUUID;

    private UUID lectureTypeUUID;

    private UUID lectureDeliveryModeUUID;

    private UUID dayUUID;

    private UUID createdBy;

    private LocalDateTime createdAt;

    private UUID updatedBy;

    private LocalDateTime updatedAt;

    private UUID deletedBy;

    private LocalDateTime deletedAt;

    private Boolean editable;

    private Boolean deletable;

    private Boolean archived;

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
}
