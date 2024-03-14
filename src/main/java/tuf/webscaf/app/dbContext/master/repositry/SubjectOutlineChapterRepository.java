//package tuf.webscaf.app.dbContext.master.repositry;
//
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineChapterEntity;
//
//import java.util.UUID;
//
//@Repository
//public interface SubjectOutlineChapterRepository extends ReactiveCrudRepository<SubjectOutlineChapterEntity, Long> {
//    Mono<SubjectOutlineChapterEntity> findByIdAndDeletedAtIsNull(Long id);
//
//    Mono<SubjectOutlineChapterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
//
//    Mono<SubjectOutlineChapterEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);
//
//    Mono<SubjectOutlineChapterEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
//
//    Mono<SubjectOutlineChapterEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID);
//}
