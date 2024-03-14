package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloDocImpl {

    //Get clo UUIDs
    @Schema(required = true)
    private List<UUID> cloUUID;
}