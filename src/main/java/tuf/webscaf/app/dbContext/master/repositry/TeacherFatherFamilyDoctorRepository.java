package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherFamilyDoctorRepository extends ReactiveCrudRepository<TeacherFatherFamilyDoctorEntity, Long> {
    Mono<TeacherFatherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherFamilyDoctorEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<TeacherFatherFamilyDoctorEntity> findFirstByTeacherFatherUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherFatherUUID, String name, String contactNo);

    Mono<TeacherFatherFamilyDoctorEntity> findFirstByTeacherFatherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherFatherUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherFatherFamilyDoctorEntity> findFirstByTeacherFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherFatherUUID, String name, String clinicalAddress);

    Mono<TeacherFatherFamilyDoctorEntity> findFirstByTeacherFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherFatherUUID, String name, String clinicalAddress, UUID uuid);
}
