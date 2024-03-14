package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentMotherFamilyDoctorRepository extends ReactiveCrudRepository<StudentMotherFamilyDoctorEntity, Long> {
    Mono<StudentMotherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherFamilyDoctorEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);

    Mono<StudentMotherFamilyDoctorEntity> findFirstByStudentMotherUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentMotherUUID, String name, String contactNo);

    Mono<StudentMotherFamilyDoctorEntity> findFirstByStudentMotherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentMotherUUID, String name, String contactNo, UUID uuid);

    Mono<StudentMotherFamilyDoctorEntity> findFirstByStudentMotherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentMotherUUID, String name, String clinicalAddress);

    Mono<StudentMotherFamilyDoctorEntity> findFirstByStudentMotherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentMotherUUID, String name, String clinicalAddress, UUID uuid);

}
