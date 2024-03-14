package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.RegistrationEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends ReactiveCrudRepository<RegistrationEntity, Long> {
    Mono<RegistrationEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<RegistrationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<RegistrationEntity> findAllByStudentUUIDInAndDeletedAtIsNull(List<UUID> studentUUID);

    Flux<RegistrationEntity> findAllByStudentUUIDInAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNull(List<UUID> studentUUID, UUID campusCourseUUID, UUID academicSessionUUID);

    Mono<RegistrationEntity> findFirstByRegistrationNoAndDeletedAtIsNull(String registrationNo);

    Mono<RegistrationEntity> findFirstByRegistrationNoAndDeletedAtIsNullAndUuidIsNot(String registrationNo, UUID uuid);

    Mono<RegistrationEntity> findFirstByStudentUUIDAndCampusCourseUUIDAndDeletedAtIsNull(UUID studentUUID, UUID campusCourseUUID);

    Mono<RegistrationEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<RegistrationEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<RegistrationEntity> findByStudentUUIDAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNull(UUID studentUUID, UUID campusCourseUUID, UUID academicSessionUUID);

    Mono<RegistrationEntity> findFirstByStudentUUIDAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID campusCourseUUID, UUID academicSessionUUID,UUID uuid);

    Mono<RegistrationEntity> findFirstByStudentUUIDAndCampusCourseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID campusCourseUUID, UUID uuid);

}
