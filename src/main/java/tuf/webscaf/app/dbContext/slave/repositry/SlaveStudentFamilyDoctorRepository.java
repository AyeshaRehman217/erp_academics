package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFamilyDoctorRepository extends ReactiveCrudRepository<SlaveStudentFamilyDoctorEntity, Long> {
    Flux<SlaveStudentFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String clinicalAddress);

    Flux<SlaveStudentFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Flux<SlaveStudentFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID studentUUID, String description, UUID studentUUID2, String clinicalAddress, UUID studentUUID3);

    Flux<SlaveStudentFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name,UUID studentUUID, Boolean status, String description,UUID studentUUID2, Boolean status2, String clinicalAddress,UUID studentUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String clinicalAddress);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status1, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(String name, UUID studentUUID, String description, UUID studentUUID2, String clinicalAddress, UUID studentUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(String name,UUID studentUUID, Boolean status, String description,UUID studentUUID2, Boolean status2, String clinicalAddress,UUID studentUUID3, Boolean status3);

    Mono<SlaveStudentFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
