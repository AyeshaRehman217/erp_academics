package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherFamilyDoctorRepository extends ReactiveCrudRepository<SlaveStudentFatherFamilyDoctorEntity, Long> {
    Mono<SlaveStudentFatherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String clinicalAddress);

    Flux<SlaveStudentFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Flux<SlaveStudentFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID studentFatherUUID, String description, UUID studentFatherUUID2, String clinicalAddress, UUID studentFatherUUID3);

    Flux<SlaveStudentFatherFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID studentFatherUUID, Boolean status, String description, UUID studentFatherUUID2, Boolean status2, String clinicalAddress, UUID studentFatherUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String clinicalAddress);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String name, UUID studentFatherUUID, String description, UUID studentFatherUUID2, String clinicalAddress, UUID studentFatherUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(String name, UUID studentFatherUUID, Boolean status, String description, UUID studentFatherUUID2, Boolean status2, String clinicalAddress, UUID studentFatherUUID3, Boolean status3);
}
