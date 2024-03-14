package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherFamilyDoctorRepository extends ReactiveCrudRepository<SlaveTeacherFatherFamilyDoctorEntity, Long> {
    Flux<SlaveTeacherFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String clinicalAddress);

    Flux<SlaveTeacherFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Flux<SlaveTeacherFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2, String clinicalAddress, UUID teacherFatherUUID3);

    Flux<SlaveTeacherFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID teacherFatherUUID, Boolean status, String description, UUID teacherFatherUUID2, Boolean status2, String clinicalAddress, UUID teacherFatherUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String clinicalAddress);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String name, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2, String clinicalAddress, UUID teacherFatherUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(String name, UUID teacherFatherUUID, Boolean status, String description, UUID teacherFatherUUID2, Boolean status2, String clinicalAddress, UUID teacherFatherUUID3, Boolean status3);

    Mono<SlaveTeacherFatherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}