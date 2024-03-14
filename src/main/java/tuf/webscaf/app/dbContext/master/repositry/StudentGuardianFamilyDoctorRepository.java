package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianDocumentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianFamilyDoctorRepository extends ReactiveCrudRepository<StudentGuardianFamilyDoctorEntity, Long> {
    Mono<StudentGuardianFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianFamilyDoctorEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianUUID);

    Mono<StudentGuardianFamilyDoctorEntity> findFirstByStudentGuardianUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentGuardianUUID, String name, String contactNo);

    Mono<StudentGuardianFamilyDoctorEntity> findFirstByStudentGuardianUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentGuardianUUID, String name, String contactNo, UUID uuid);

    Mono<StudentGuardianFamilyDoctorEntity> findFirstByStudentGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentGuardianUUID, String name, String clinicalAddress);

    Mono<StudentGuardianFamilyDoctorEntity> findFirstByStudentGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentGuardianUUID, String name, String clinicalAddress, UUID uuid);
}
