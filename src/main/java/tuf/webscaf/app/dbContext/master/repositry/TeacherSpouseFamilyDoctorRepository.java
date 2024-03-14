package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseFamilyDoctorRepository extends ReactiveCrudRepository<TeacherSpouseFamilyDoctorEntity, Long> {
    Mono<TeacherSpouseFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseFamilyDoctorEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseFamilyDoctorEntity> findFirstByTeacherSpouseUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherSpouseUUID, String name, String contactNo);

    Mono<TeacherSpouseFamilyDoctorEntity> findFirstByTeacherSpouseUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherSpouseFamilyDoctorEntity> findFirstByTeacherSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherSpouseUUID, String name, String clinicalAddress);

    Mono<TeacherSpouseFamilyDoctorEntity> findFirstByTeacherSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, String name, String clinicalAddress, UUID uuid);
}
