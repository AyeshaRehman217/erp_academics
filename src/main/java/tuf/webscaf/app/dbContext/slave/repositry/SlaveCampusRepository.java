package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCampusEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveCampusRepository extends ReactiveCrudRepository<SlaveCampusEntity, Long> {

    Mono<SlaveCampusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    used in seeder
    Mono<SlaveCampusEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SlaveCampusEntity> findFirstByCompanyUUIDAndDeletedAtIsNull(UUID companyUUID);

    //This Query Prints All the Campus UUIDs for given Company UUID
    @Query("SELECT string_agg(uuid::text, ',') " +
            "as campusUUID FROM campuses " +
            "WHERE campuses.company_uuid = :companyUUID")
    Mono<String> getAllCampusUUIDAgainstCompany(UUID companyUUID);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID companyUUID, Boolean status, String description, UUID companyUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNull(String name, UUID companyUUID, Boolean status, String description, UUID companyUUID2, Boolean status2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID companyUUID, String description, UUID companyUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNull(String name, UUID companyUUID, String description, UUID companyUUID2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidIn(Pageable pageable, String name, UUID companyUUID, Boolean status, List<UUID> uuidList, String description, UUID companyUUID2, Boolean status2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidIn(String name, UUID companyUUID, Boolean status, List<UUID> uuidList, String description, UUID companyUUID2, Boolean status2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidIn(Pageable pageable, String name, UUID companyUUID, List<UUID> uuidList, String description, UUID companyUUID2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidIn(String name, UUID companyUUID, List<UUID> uuidList, String description, UUID companyUUID2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(Pageable pageable, String name, Boolean status, List<UUID> uuidList, String description, Boolean status2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(String name, Boolean status, List<UUID> uuidList, String description, Boolean status2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(Pageable pageable, String name, List<UUID> uuidList, String description, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(String name, List<UUID> uuidList, String description, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotIn(Pageable pageable, String name, UUID companyUUID, Boolean status, List<UUID> uuidList, String description, UUID companyUUID2, Boolean status2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotIn(String name, UUID companyUUID, Boolean status, List<UUID> uuidList, String description, UUID companyUUID2, Boolean status2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotIn(Pageable pageable, String name, UUID companyUUID, List<UUID> uuidList, String description, UUID companyUUID2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotIn(String name, UUID companyUUID, List<UUID> uuidList, String description, UUID companyUUID2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotIn(Pageable pageable, String name, Boolean status, List<UUID> uuidList, String description, Boolean status2, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotIn(String name, Boolean status, List<UUID> uuidList, String description, Boolean status2, List<UUID> uuidList2);

    Flux<SlaveCampusEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotIn(Pageable pageable, String name, List<UUID> uuidList, String description, List<UUID> uuidList2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotIn(String name, List<UUID> uuidList, String description, List<UUID> uuidList2);

    Mono<SlaveCampusEntity> findByIdAndDeletedAtIsNull(Long id);
}
