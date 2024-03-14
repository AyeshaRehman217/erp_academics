package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDocImpl {

    @Schema(required = true)
    private String firstName;

    @Schema(required = true)
    private String lastName;

    @Schema(required = true)
    private UUID image;

    @Schema(required = true)
    private String nic;

    private String description;

    private Boolean status;

    @Schema(required = true)
    private UUID emailUUID;

    @Schema(required = true)
    private UUID contactNoUUID;

    private String telephoneNo;

    @Schema(required = true)
    private LocalDateTime birthDate;

    @Schema(required = true)
    private UUID cityUUID;

    @Schema(required = true)
    private UUID stateUUID;

    @Schema(required = true)
    private UUID countryUUID;

    @Schema(required = true)
    private UUID religionUUID;

    @Schema(required = true)
    private UUID sectUUID;

    @Schema(required = true)
    private UUID casteUUID;

    @Schema(required = true)
    private UUID genderUUID;

    @Schema(required = true)
    private UUID maritalStatusUUID;

    @Schema(required = true)
    private String emergencyContactPerson;

    @Schema(required = true)
    private UUID emergencyContactNoUUID;

}
