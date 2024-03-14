package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentAcademicRecordDocImpl {

    private Boolean status;

    @Schema(required = true)
    private UUID degreeUUID;

    private Integer totalMarks;

    private Integer obtainedMarks;

    private Float totalCgpa;

    private Float obtainedCgpa;

    @Schema(required = true)
    private Float percentage;

    @Schema(required = true)
    private String grade;

    @Schema(required = true)
    private LocalDateTime startDate;

    @Schema(required = true)
    private LocalDateTime endDate;

    @Schema(required = true)
    private LocalDateTime passOutYear;

    @Schema(required = true)
    private UUID countryUUID;

    @Schema(required = true)
    private UUID stateUUID;

    @Schema(required = true)
    private UUID cityUUID;


}
