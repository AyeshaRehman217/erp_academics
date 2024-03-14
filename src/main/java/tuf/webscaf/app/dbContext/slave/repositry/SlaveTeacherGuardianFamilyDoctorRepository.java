package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianFamilyDoctorRepository extends ReactiveCrudRepository<SlaveTeacherGuardianFamilyDoctorEntity, Long> {
    Flux<SlaveTeacherGuardianFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String clinicalAddress);

    Flux<SlaveTeacherGuardianFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Flux<SlaveTeacherGuardianFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID teacherGuardianUUID, String description, UUID teacherGuardianUUID2, String clinicalAddress, UUID teacherGuardianUUID3);

    Flux<SlaveTeacherGuardianFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID teacherGuardianUUID, Boolean status, String description, UUID teacherGuardianUUID2, Boolean status2, String clinicalAddress, UUID teacherGuardianUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String clinicalAddress);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String name, UUID teacherGuardianUUID, String description, UUID teacherGuardianUUID2, String clinicalAddress, UUID teacherGuardianUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(String name, UUID teacherGuardianUUID, Boolean status, String description, UUID teacherGuardianUUID2, Boolean status2, String clinicalAddress, UUID teacherGuardianUUID3, Boolean status3);

    Mono<SlaveTeacherGuardianFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
