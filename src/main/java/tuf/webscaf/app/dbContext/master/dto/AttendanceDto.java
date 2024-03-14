package tuf.webscaf.app.dbContext.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private LocalTime startTime;

    private LocalTime endTime;

    private UUID dayUUID;

    private UUID studentUUID;

    private UUID academicSessionUUID;

    private UUID subjectUUID;

//    private UUID teacherUUID;

    @Schema(hidden = true)
    private UUID commencementOfClassesUUID;

    private UUID attendanceTypeUUID;

    private UUID markedBy;

    private boolean status;

}
