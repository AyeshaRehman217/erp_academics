package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveTeacherDto {

   private Long id;

   private Long version;

   private UUID uuid;

   private String key;

   private String employeeCode;

   private UUID campusUUID;

   private UUID deptRankUUID;

   private UUID reportingTo;

   private Boolean status;

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
