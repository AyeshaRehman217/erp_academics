package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentSiblingNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentSiblingNationalityPvtEntity, Long> {
    Mono<SlaveStudentSiblingNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSiblingNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentSiblingNationalityPvtEntity> findAllByStudentSiblingUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID studentSiblingUUID, List<UUID> nationalityUUID);

    Flux<SlaveStudentSiblingNationalityPvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

}
