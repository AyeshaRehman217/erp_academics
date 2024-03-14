package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineOfferedEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectOutlineOfferedRepository;

import java.util.UUID;

@Repository
public interface SlaveSubjectOutlineOfferedRepository extends ReactiveCrudRepository<SlaveSubjectOutlineOfferedEntity, Long>, SlaveCustomSubjectOutlineOfferedRepository {

    Flux<SlaveSubjectOutlineOfferedEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countAllByDeletedAtIsNull();

    Mono<SlaveSubjectOutlineOfferedEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Count Subject Outline Offered with and without Status
     **/
    //query used subject Outline Offered without obe check
    @Query("WITH subjectOutlineOffered AS (" +
            "SELECT \n" +
            "CASE WHEN subject_outline_offered.subject_outline_uuid IS NOT NULL AND subject_outline_offered.subject_obe_uuid IS NULL\n" +
            "  THEN (\n" +
            "        SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name)\n" +
            "        FROM subject_outline_offered\n" +
            "                 JOIN subject_outlines ON subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
            "                 JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
            "                 JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "                 JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
            "                 JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
            "                 JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
            "        WHERE subject_outline_offered.deleted_at IS NULL\n" +
            "          AND academic_sessions.deleted_at IS NULL\n" +
            "          AND subject_offered.deleted_at IS NULL\n" +
            "          AND course_subject.deleted_at IS NULL\n" +
            "          AND courses.deleted_at IS NULL\n" +
            "          AND subject_outlines.deleted_at IS NULL\n" +
            "          AND subjects.deleted_at IS NULL\n" +
            "    )\n" +
            "     WHEN subject_outline_offered.subject_obe_uuid IS NOT NULL AND subject_outline_offered.subject_outline_uuid IS NULL\n" +
            "         THEN (\n" +
            "         SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name)\n" +
            "         FROM subject_outline_offered\n" +
            "                  JOIN subject_obes ON subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
            "                  JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
            "                  JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "                  JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
            "                  JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
            "                  JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
            "         WHERE subject_outline_offered.deleted_at IS NULL\n" +
            "           AND academic_sessions.deleted_at IS NULL\n" +
            "           AND subject_offered.deleted_at IS NULL\n" +
            "           AND course_subject.deleted_at IS NULL\n" +
            "           AND courses.deleted_at IS NULL\n" +
            "           AND subject_obes.deleted_at IS NULL\n" +
            "           AND subjects.deleted_at IS NULL\n" +
            "     )\n" +
            "     ELSE ''\n" +
            "END AS name\n" +
            "FROM subject_outline_offered \n" +
            "WHERE subject_outline_offered.deleted_at IS NULL)\n" +
            "SELECT count (*) FROM subjectOutlineOffered \n" +
            "WHERE subjectOutlineOffered.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countSubjectOutlineOffered(String name);

    //query used subject Outline Offered without obe check
    @Query("select count(*)\n" +
            "from subject_outline_offered\n" +
            "join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
            "join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
            "join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid\n" +
            "where subject_outline_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            "and subject_outlines.deleted_at is null\n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) ILIKE concat('%',:name,'%') \n" +
            "and subject_outline_offered.status = :status ")
    Mono<Long> countSubjectOutlineOfferedWithStatus(String name, Boolean status);


    /**
     * Count Subject Outline Offered with and without OBE Filter
     **/
    //query used subject Outline Offered without obe check
    @Query("select count(*)\n" +
            "from subject_outline_offered\n" +
            "join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid \n" +
            "join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid \n" +
            "join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "join courses on courses.uuid=course_subject.course_uuid \n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid \n" +
            "where subject_outline_offered.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and subject_obes.deleted_at is null \n" +
            "and subject_outline_offered.obe=:obe \n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) ILIKE concat('%',:name,'%')")
    Mono<Long> countSubjectOutlineOfferedWithOBETrue(Boolean obe, String name);

    @Query("select count(*)\n" +
            "from subject_outline_offered\n" +
            "join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid \n" +
            "join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid \n" +
            "join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "join courses on courses.uuid=course_subject.course_uuid \n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid \n" +
            "where subject_outline_offered.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and subject_outlines.deleted_at is null \n" +
            "and subject_outline_offered.obe=:obe \n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) ILIKE concat('%',:name,'%')")
    Mono<Long> countSubjectOutlineOfferedWithOBEFalse(Boolean obe, String name);

    //query used subject Outline Offered without obe check
    @Query("select count(*)\n" +
            "from subject_outline_offered\n" +
            "join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
            "join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
            "join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid\n" +
            "where subject_outline_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            "and subject_obes.deleted_at is null\n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) ILIKE concat('%',:name,'%') \n" +
            "and subject_outline_offered.status = :status " +
            "and subject_outline_offered.obe = :obe")
    Mono<Long> countSubjectOutlineOfferedWithOBETrueAndStatus(Boolean status, Boolean obe, String name);

    @Query("select count(*)\n" +
            "from subject_outline_offered\n" +
            "join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_obe_uuid \n" +
            "join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid \n" +
            "join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "join courses on courses.uuid=course_subject.course_uuid \n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid \n" +
            "where subject_outline_offered.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and subject_outlines.deleted_at is null \n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) ILIKE concat('%',:name,'%') \n" +
            "and subject_outline_offered.status = :status " +
            "and subject_outline_offered.obe = :obe")
    Mono<Long> countSubjectOutlineOfferedWithOBEFalseAndStatus(Boolean status, Boolean obe, String name);
}
