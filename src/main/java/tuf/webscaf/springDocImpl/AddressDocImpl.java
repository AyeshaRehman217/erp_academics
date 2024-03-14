package tuf.webscaf.springDocImpl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressDocImpl {

    @Schema(required = true)
    private String address;

    private Boolean status;

    @Schema(required = true)
    private UUID addressTypeUUID;


}
