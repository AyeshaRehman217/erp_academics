package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingFamilyDoctorRepository extends ReactiveCrudRepository<TeacherSiblingFamilyDoctorEntity, Long> {

    Mono<TeacherSiblingFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingFamilyDoctorEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingFamilyDoctorEntity> findFirstByTeacherSiblingUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherSiblingUUID, String name, String contactNo);

    Mono<TeacherSiblingFamilyDoctorEntity> findFirstByTeacherSiblingUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherSiblingUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherSiblingFamilyDoctorEntity> findFirstByTeacherSiblingUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherSiblingUUID, String name, String clinicalAddress);

    Mono<TeacherSiblingFamilyDoctorEntity> findFirstByTeacherSiblingUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherSiblingUUID, String name, String clinicalAddress, UUID uuid);
}
