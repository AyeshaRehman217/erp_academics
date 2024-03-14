package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianFamilyDoctorRepository extends ReactiveCrudRepository<TeacherGuardianFamilyDoctorEntity, Long> {
    Mono<TeacherGuardianFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianFamilyDoctorEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianFamilyDoctorEntity> findFirstByTeacherGuardianUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherGuardianUUID, String name, String contactNo);

    Mono<TeacherGuardianFamilyDoctorEntity> findFirstByTeacherGuardianUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherGuardianUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherGuardianFamilyDoctorEntity> findFirstByTeacherGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherGuardianUUID, String name, String clinicalAddress);

    Mono<TeacherGuardianFamilyDoctorEntity> findFirstByTeacherGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherGuardianUUID, String name, String clinicalAddress, UUID uuid);
}
