package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingFamilyDoctorRepository extends ReactiveCrudRepository<StudentSiblingFamilyDoctorEntity, Long> {

    Mono<StudentSiblingFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingFamilyDoctorEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

    Mono<StudentSiblingFamilyDoctorEntity> findFirstByStudentSiblingUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentSiblingUUID, String name, String contactNo);

    Mono<StudentSiblingFamilyDoctorEntity> findFirstByStudentSiblingUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentSiblingUUID, String name, String contactNo, UUID uuid);

    Mono<StudentSiblingFamilyDoctorEntity> findFirstByStudentSiblingUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentSiblingUUID, String name, String clinicalAddress);

    Mono<StudentSiblingFamilyDoctorEntity> findFirstByStudentSiblingUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentSiblingUUID, String name, String clinicalAddress, UUID uuid);

}
