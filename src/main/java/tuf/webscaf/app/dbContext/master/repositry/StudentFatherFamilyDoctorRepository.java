package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentFatherFamilyDoctorRepository extends ReactiveCrudRepository<StudentFatherFamilyDoctorEntity, Long> {
    Mono<StudentFatherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherFamilyDoctorEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);

    Mono<StudentFatherFamilyDoctorEntity> findFirstByStudentFatherUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentFatherUUID, String name, String contactNo);

    Mono<StudentFatherFamilyDoctorEntity> findFirstByStudentFatherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentFatherUUID, String name, String contactNo, UUID uuid);

    Mono<StudentFatherFamilyDoctorEntity> findFirstByStudentFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentFatherUUID, String name, String clinicalAddress);

    Mono<StudentFatherFamilyDoctorEntity> findFirstByStudentFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentFatherUUID, String name, String clinicalAddress, UUID uuid);
}
