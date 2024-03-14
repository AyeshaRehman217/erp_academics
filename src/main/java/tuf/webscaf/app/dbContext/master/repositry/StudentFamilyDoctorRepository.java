package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentFamilyDoctorRepository extends ReactiveCrudRepository<StudentFamilyDoctorEntity, Long> {
    Mono<StudentFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Contact No UUID is used by this table
    Mono<StudentFamilyDoctorEntity> findFirstByContactNoAndDeletedAtIsNull(String contactNo);

    Mono<StudentFamilyDoctorEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

    Mono<StudentFamilyDoctorEntity> findFirstByStudentUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentUUID, String name, String contactNo);

    Mono<StudentFamilyDoctorEntity> findFirstByStudentUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, String name, String contactNo, UUID uuid);

    Mono<StudentFamilyDoctorEntity> findFirstByStudentUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentUUID, String name, String clinicalAddress);

    Mono<StudentFamilyDoctorEntity> findFirstByStudentUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, String name, String clinicalAddress, UUID uuid);

}
