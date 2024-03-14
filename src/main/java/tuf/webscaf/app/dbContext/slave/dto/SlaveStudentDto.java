package tuf.webscaf.app.dbContext.slave.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SlaveStudentDto {
   private UUID studentUUID;
   private UUID courseOfferedUUID;
}
