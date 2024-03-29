//package tuf.webscaf.app.dbContext.slave.repositry;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineChapterEntity;
//
//import java.util.UUID;
//
//@Repository
//public interface SlaveSubjectOutlineChapterRepository extends ReactiveCrudRepository<SlaveSubjectOutlineChapterEntity, Long> {
//    Flux<SlaveSubjectOutlineChapterEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);
//
//    Flux<SlaveSubjectOutlineChapterEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);
//
//    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);
//
//    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);
//
//    Mono<SlaveSubjectOutlineChapterEntity> findByIdAndDeletedAtIsNull(Long id);
//
//    Mono<SlaveSubjectOutlineChapterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
//
//}
