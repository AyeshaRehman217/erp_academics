package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseSubjectDocImpl {

    @Column("course_uuid")
    @Schema(required = true)
    private UUID courseUUID;

    @Column("subject_uuid")
    @Schema(required = true)
    private UUID subjectUUID;

    @Column("semester_uuid")
    @Schema(required = true)
    private UUID semesterUUID;

    @Column("lecture_type_uuid")
    @Schema(required = true)
    private UUID lectureTypeUUID;

    @Column("theory_credit_hours")
    private String theoryCreditHours ;

    @Column("practical_credit_hours")
    private String practicalCreditHours ;

    @Column("elective_subject")
    private Boolean electiveSubject;

    @Column("status")
    private Boolean status;

    @Column("total_credit_hours")
    @Schema(required = true)
    private Integer totalCreditHours;

}
