package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseFamilyDoctorRepository extends ReactiveCrudRepository<StudentSpouseFamilyDoctorEntity, Long> {
    Mono<StudentSpouseFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseFamilyDoctorEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseFamilyDoctorEntity> findFirstByStudentSpouseUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID studentSpouseUUID, String name, String contactNo);

    Mono<StudentSpouseFamilyDoctorEntity> findFirstByStudentSpouseUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID studentSpouseUUID, String name, String contactNo, UUID uuid);

    Mono<StudentSpouseFamilyDoctorEntity> findFirstByStudentSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID studentSpouseUUID, String name, String clinicalAddress);

    Mono<StudentSpouseFamilyDoctorEntity> findFirstByStudentSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID studentSpouseUUID, String name, String clinicalAddress, UUID uuid);
}
