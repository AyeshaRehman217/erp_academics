package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDoctorDocImpl {

    @Schema(required = true)
    private String name;

    private String description;

    private Boolean status;

    private String contactNo;

    @Schema(required = true)
    private String clinicalAddress;

}
