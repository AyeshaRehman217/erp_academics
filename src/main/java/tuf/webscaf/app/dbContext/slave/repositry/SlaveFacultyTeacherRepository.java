package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFacultyTeacherEntity;

import java.util.UUID;

@Repository
public interface SlaveFacultyTeacherRepository extends ReactiveCrudRepository<SlaveFacultyTeacherEntity, Long> {

    Mono<SlaveFacultyTeacherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


    Flux<SlaveFacultyTeacherEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countAllByDeletedAtIsNull();

    /**
     * Fetch All Records With Status Filter
     **/
    Flux<SlaveFacultyTeacherEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countAllByStatusAndDeletedAtIsNull(Boolean status);

    /**
     * Fetch All Records With Faculty Filter
     **/
    Flux<SlaveFacultyTeacherEntity> findAllByFacultyUUIDAndDeletedAtIsNull(Pageable pageable, UUID facultyUUID);

    Mono<Long> countAllByFacultyUUIDAndDeletedAtIsNull(UUID facultyUUID);

    /**
     * Fetch All Records With Faculty Filter and Status
     **/

    Flux<SlaveFacultyTeacherEntity> findAllByFacultyUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID facultyUUID, Boolean status);

    Mono<Long> countAllByFacultyUUIDAndStatusAndDeletedAtIsNull(UUID facultyUUID, Boolean status);

}
