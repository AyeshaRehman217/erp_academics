//package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;
//
//import reactor.core.publisher.Flux;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseVisionAndMissionEntity;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveSubLearningTypeEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubLearningTypeRepository;
//
//import java.util.UUID;
//
///**
// * This Interface will extend in Slave Sub Learning Type Repository
// **/
//public interface SlaveCustomBloomsTaxonomySubLearningTypePvtRepository {
//
//    /**
//     * Fetch UnMapped Sub Learning Types against Bloom Taxonomy UUID with and Without Status Filter
//     **/
//    Flux<SlaveSubLearningTypeEntity> unMappedSubLearningTypesAgainstBloomTaxonomy(UUID bloomTaxonomyUUID, String name, String code, String description, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveSubLearningTypeEntity> unMappedSubLearningTypesAgainstBloomTaxonomyWithStatus(UUID bloomTaxonomyUUID, Boolean status, String name, String code, String description, String dp, String d, Integer size, Long page);
//
//    /**
//     * Fetch Mapped Sub Learning Types against Bloom Taxonomy UUID with and Without Status Filter
//     **/
//    Flux<SlaveSubLearningTypeEntity> mappedSubLearningTypesAgainstBloomTaxonomy(UUID bloomTaxonomyUUID, String name, String code, String description, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveSubLearningTypeEntity> mappedSubLearningTypesAgainstBloomTaxonomyWithStatus(UUID bloomTaxonomyUUID, Boolean status, String name, String code, String description, String dp, String d, Integer size, Long page);
//}
