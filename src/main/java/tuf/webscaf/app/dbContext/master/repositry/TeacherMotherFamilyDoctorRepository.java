package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherFamilyDoctorEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherFamilyDoctorRepository extends ReactiveCrudRepository<TeacherMotherFamilyDoctorEntity, Long> {

    Mono<TeacherMotherFamilyDoctorEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherFamilyDoctorEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherFamilyDoctorEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherFamilyDoctorEntity> findFirstByTeacherMotherUUIDAndNameAndContactNoAndDeletedAtIsNull(UUID teacherMotherUUID, String name, String contactNo);

    Mono<TeacherMotherFamilyDoctorEntity> findFirstByTeacherMotherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(UUID teacherMotherUUID, String name, String contactNo, UUID uuid);

    Mono<TeacherMotherFamilyDoctorEntity> findFirstByTeacherMotherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(UUID teacherMotherUUID, String name, String clinicalAddress);

    Mono<TeacherMotherFamilyDoctorEntity> findFirstByTeacherMotherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(UUID teacherMotherUUID, String name, String clinicalAddress, UUID uuid);

}
