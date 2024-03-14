package tuf.webscaf.springDocImpl;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubjectOfferedDocImpl {

    private UUID academicSessionUUID;

    private UUID courseOfferedUUID;

    private boolean isObe;

    private boolean status;

    private List<UUID> subjectUUID;

    private Integer totalCreditHours;




}
