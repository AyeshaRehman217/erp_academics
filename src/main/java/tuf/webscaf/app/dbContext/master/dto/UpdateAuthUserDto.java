package tuf.webscaf.app.dbContext.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAuthUserDto {

    @Schema(required = true)
    private String password;
}
