package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFamilyDoctorEntity;


import java.util.UUID;

@Repository
public interface TeacherFamilyDoctorRepository extends ReactiveCrudRepository<TeacherFamilyDoctorEntity, Long> {
    Mono<TeacherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFamilyDoctorEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherFamilyDoctorEntity> findFirstByTeacherUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherUUID, String name, String contactNo);

    Mono<TeacherFamilyDoctorEntity> findFirstByTeacherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherFamilyDoctorEntity> findFirstByTeacherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherUUID, String name, String clinicalAddress);

    Mono<TeacherFamilyDoctorEntity> findFirstByTeacherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, String name, String clinicalAddress, UUID uuid);
}
