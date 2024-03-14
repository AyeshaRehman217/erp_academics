package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingFamilyDoctorRepository extends ReactiveCrudRepository<SlaveStudentSiblingFamilyDoctorEntity, Long> {
    Flux<SlaveStudentSiblingFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String clinicalAddress);

    Flux<SlaveStudentSiblingFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2, String clinicalAddress, Boolean status3);

    Flux<SlaveStudentSiblingFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID studentSiblingUUID, String description, UUID studentSiblingUUID2, String clinicalAddress, UUID studentSiblingUUID3);

    Flux<SlaveStudentSiblingFamilyDoctorEntity> findAllByNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID studentSiblingUUID, Boolean status, String description, UUID studentSiblingUUID2, Boolean status2, String clinicalAddress, UUID studentSiblingUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(String name, String description,String clinicalAddress);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2,String clinicalAddress, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String name, UUID studentSiblingUUID, String description, UUID studentSiblingUUID2, String clinicalAddress, UUID studentSiblingUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String name, UUID studentSiblingUUID, Boolean status, String description, UUID studentSiblingUUID2, Boolean status2, String clinicalAddress, UUID studentSiblingUUID3, Boolean status3);

    Mono<SlaveStudentSiblingFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSiblingFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
