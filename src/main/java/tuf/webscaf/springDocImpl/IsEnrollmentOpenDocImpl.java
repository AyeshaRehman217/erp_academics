package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IsEnrollmentOpenDocImpl {

    // Get isEnrollmentOpen Field
    @Schema(required = true)
    private Boolean isEnrollmentOpen;
}
