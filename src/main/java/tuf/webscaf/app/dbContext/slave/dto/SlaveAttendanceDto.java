package tuf.webscaf.app.dbContext.slave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveAttendanceDto {
    private Long id;

    private Long version;

    private Boolean status;

    private UUID uuid;

    private String key;

    private String subjectCode;

    private String subjectName;

    private String studentID;

    private String lectureTypeName;

    private String day;

    private LocalTime startTime;

    private LocalTime endTime;

    private UUID attendanceTypeUUID;

    private UUID commencementOfClassesUUID;

    private UUID markedBy;

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
