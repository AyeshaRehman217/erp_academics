package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IsRegistrationOpenDocImpl {

    // Get isRegistrationOpen Field
    @Schema(required = true)
    private Boolean isRegistrationOpen;
}
