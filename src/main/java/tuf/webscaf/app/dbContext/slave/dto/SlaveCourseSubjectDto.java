package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveCourseSubjectDto {

   private Long id;

   private Long version;

   private UUID uuid;

   private String name;

   private UUID semesterUUID;

   private UUID lectureTypeUUID;

   private String theoryCreditHours;

   private String practicalCreditHours;

   private Boolean electiveSubject;

   private UUID courseUUID;

   private UUID subjectUUID;

   private Boolean obe;

   private Boolean status;

   private Integer totalCreditHours;

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
