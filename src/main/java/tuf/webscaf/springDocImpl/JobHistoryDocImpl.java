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
public class JobHistoryDocImpl {

    @Schema(required = true)
    private String occupation;

    @Schema(required = true)
    private String designation;

    @Schema(required = true)
    private String organization;

    @Schema(required = true)
    private Long income;

    @Schema(required = true)
    private Boolean status;

    @Schema(required = true)
    private UUID currencyUUID;

    @Schema(required = true)
    private LocalDateTime startDate;

    @Schema(required = true)
    private LocalDateTime endDate;

}
