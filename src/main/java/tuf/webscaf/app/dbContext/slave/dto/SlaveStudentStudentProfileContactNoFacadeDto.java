package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveStudentStudentProfileContactNoFacadeDto {

    private Long id;

    private UUID uuid;

    private Long version;

    private String studentId;

    private Boolean status;

    private UUID campusUUID;

    private String officialEmail;

    private String description;

    private UUID studentUUID;

    private UUID image;

    private String firstName;

    private String lastName;

    private String email;

    private String telephoneNo;

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

    private List<SlaveStudentContactNoFacadeDto> studentContactNoDto;

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
