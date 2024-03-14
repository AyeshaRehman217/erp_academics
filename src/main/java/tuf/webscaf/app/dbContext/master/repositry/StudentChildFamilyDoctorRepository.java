package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentChildFamilyDoctorRepository extends ReactiveCrudRepository<StudentChildFamilyDoctorEntity, Long> {

    Mono<StudentChildFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildFamilyDoctorEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<StudentChildFamilyDoctorEntity> findFirstByStudentChildUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentChildUUID, String name, String contactNo);

    Mono<StudentChildFamilyDoctorEntity> findFirstByStudentChildUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentChildUUID, String name, String contactNo, UUID uuid);

    Mono<StudentChildFamilyDoctorEntity> findFirstByStudentChildUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentChildUUID, String name, String clinicalAddress);

    Mono<StudentChildFamilyDoctorEntity> findFirstByStudentChildUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentChildUUID, String name, String clinicalAddress, UUID uuid);
}
