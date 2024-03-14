package tuf.webscaf.app.dbContext.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentContactNoDto {
    private UUID contactTypeUUID;

    private String contactNo;
}
