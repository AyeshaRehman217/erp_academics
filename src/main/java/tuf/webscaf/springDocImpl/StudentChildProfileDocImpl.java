package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentChildProfileDocImpl {

    private Boolean status;

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private Integer age;

    @Schema(required = true)
    private UUID image;

    @Schema(required = true)
    private String nic;

    @Schema(required = true)
    private UUID cityUUID;

    @Schema(required = true)
    private UUID stateUUID;

    @Schema(required = true)
    private UUID countryUUID;

    private String officialTel;

    @Schema(required = true)
    private UUID contactNoUUID;

    private UUID emailUUID;

    @Schema(required = true)
    private UUID genderUUID;

}
