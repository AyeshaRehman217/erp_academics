package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDocImpl {

    private Boolean status;

    @Schema(required = true)
    private UUID docId;

    @Schema(required = true)
    private String title;

    private String description;

}
