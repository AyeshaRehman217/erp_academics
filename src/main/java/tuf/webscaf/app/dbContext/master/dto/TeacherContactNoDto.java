package tuf.webscaf.app.dbContext.master.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeacherContactNoDto {
    private UUID contactTypeUUID;

    private String contactNo;
}
