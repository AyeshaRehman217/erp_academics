package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDocImpl {

    @Schema(required = true)
    private String registrationNo;

    @Schema(required = true)
    private UUID campusUUID;

    @Schema(required = true)
    private UUID campusCourseUUID;

    @Schema(required = true)
    private UUID academicSessionUUID;

    private String reason;
    
    private Boolean status;


}
