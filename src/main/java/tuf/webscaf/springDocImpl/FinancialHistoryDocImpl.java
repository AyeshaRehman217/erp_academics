package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FinancialHistoryDocImpl {

    private String description;

    private Boolean status;

    @Schema(required = true)
    private String assetName;

    @Schema(required = true)
    private Double finance;

    @Schema(required = true)
    private UUID currencyUUID;


}
