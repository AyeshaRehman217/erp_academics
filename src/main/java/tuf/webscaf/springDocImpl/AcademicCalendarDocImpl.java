package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCalendarDocImpl {

    @Schema(required = true)
    private String name;

    private String description;

    private Boolean status;

    @Schema(required = true)
    private UUID academicSessionUUID;

    @Schema(required = true)
    private UUID courseLevelUUID;

    @Schema(required = true)
    private List<UUID> semesterUUID;
}
