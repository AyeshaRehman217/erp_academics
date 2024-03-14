//package tuf.webscaf.app.dbContext.master.entity;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.*;
//import org.springframework.data.relational.core.mapping.Column;
//import org.springframework.data.relational.core.mapping.Table;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table("public.\"subject_outline_chapters\"")
//public class SubjectOutlineChapterEntity {
//
//    @Id
//    @Column
//    @Schema(hidden = true)
//    private Long id;
//
//    @Version
//    @Schema(hidden = true)
//    private Long version;
//
//    @Column("status")
//    private Boolean status;
//
//    @Column("uuid")
//    @Schema(hidden = true)
//    private UUID uuid;
//
//    @Column("chapter_no")
//    @Schema(required = true)
//    private Integer chapterNo;
//
//    @Column("name")
//    @Schema(required = true)
//    private String name;
//
//    @Column("description")
//    private String description;
//
//    @Column("subject_outline_uuid")
//    @Schema(required = true)
//    private UUID subjectOutlineUUID;
//
//    @Column("created_by")
//    @CreatedBy
//    @Schema(hidden = true)
//    private UUID createdBy;
//
//    @Column("created_at")
//    @CreatedDate
//    @Schema(hidden = true)
//    private LocalDateTime createdAt;
//
//    @Column("updated_by")
//    @CreatedBy
//    @Schema(hidden = true)
//    private UUID updatedBy;
//
//    @Column("updated_at")
//    @LastModifiedDate
//    @Schema(hidden = true)
//    private LocalDateTime updatedAt;
//
//    @Column("deleted_by")
//    @Schema(hidden = true)
//    private UUID deletedBy;
//
//    @Column("deleted_at")
//    @Schema(hidden = true)
//    private LocalDateTime deletedAt;
//
//    @Column("editable")
//    @Schema(hidden = true)
//    private Boolean editable;
//
//    @Column("deletable")
//    @Schema(hidden = true)
//    private Boolean deletable;
//
//    @Column("archived")
//    @Schema(hidden = true)
//    private Boolean archived;
//}
