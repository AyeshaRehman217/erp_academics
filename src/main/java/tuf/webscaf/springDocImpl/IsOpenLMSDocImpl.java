package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IsOpenLMSDocImpl {

    // Get isOpenLMS Field
    @Schema(required = true)
    private Boolean isOpenLMS;
}
