package tuf.webscaf.app.dbContext.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDto {

    @Schema(required = true)
    private UUID userTypeUUID;

    @Schema(required = true)
    private String password;
}
