package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlavePloPeoPvtDto {

   private Long id;

   private Long version;

   private UUID uuid;

   private Boolean status;

   private String name;

   private UUID peoUUID;

   private UUID ploUUID;

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
