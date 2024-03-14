package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherChildFamilyDoctorRepository extends ReactiveCrudRepository<TeacherChildFamilyDoctorEntity, Long> {
    Mono<TeacherChildFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildFamilyDoctorEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildFamilyDoctorEntity> findFirstByTeacherChildUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherChildUUID, String name, String contactNo);

    Mono<TeacherChildFamilyDoctorEntity> findFirstByTeacherChildUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherChildUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherChildFamilyDoctorEntity> findFirstByTeacherChildUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherChildUUID, String name, String clinicalAddress);

    Mono<TeacherChildFamilyDoctorEntity> findFirstByTeacherChildUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherChildUUID, String name, String clinicalAddress, UUID uuid);

}
