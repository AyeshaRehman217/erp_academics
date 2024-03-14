package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOutlineOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectOutlineOfferedRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSubjectOutlineOfferedMapper;

import java.util.UUID;

public class SlaveCustomSubjectOutlineOfferedRepositoryImpl implements SlaveCustomSubjectOutlineOfferedRepository {
    private DatabaseClient client;
    private SlaveSubjectOutlineOfferedDto slaveSubjectOutlineOfferedDto;

    @Autowired
    public SlaveCustomSubjectOutlineOfferedRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOffered(String name, String dp, String d, Integer size, Long page) {
        String query = "WITH subjectOutlineOffered AS (\n" +
                " SELECT subjectOutlineOffered.*,\n" +
                "       CASE WHEN subjectOutlineOffered.subject_outline_uuid IS NOT NULL AND subjectOutlineOffered.subject_obe_uuid IS NULL\n" +
                "           THEN (\n" +
                "               SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name)\n" +
                "               FROM subject_outline_offered \n" +
                "                        JOIN subject_outlines ON subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
                "                        JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                "                        JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "                        JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
                "                        JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
                "                        JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                "               WHERE subject_outline_offered.deleted_at IS NULL\n" +
                "               AND subject_outline_offered.uuid = subjectOutlineOffered.uuid\n" +
                "               AND subject_offered.deleted_at IS NULL\n" +
                "               AND academic_sessions.deleted_at IS NULL\n" +
                "               AND course_subject.deleted_at IS NULL\n" +
                "               AND courses.deleted_at IS NULL\n" +
                "               AND subject_outlines.deleted_at IS NULL\n" +
                "               AND subjects.deleted_at IS NULL\n" +
                "           )\n" +
                "            WHEN subjectOutlineOffered.subject_obe_uuid IS NOT NULL AND subjectOutlineOffered.subject_outline_uuid IS NULL\n" +
                "                THEN (\n" +
                "                SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name)\n" +
                "                FROM subject_outline_offered\n" +
                "                         JOIN subject_obes ON subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
                "                         JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                "                         JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "                         JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
                "                         JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
                "                         JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                "                WHERE subject_outline_offered.deleted_at IS NULL\n" +
                "                AND subject_outline_offered.uuid = subjectOutlineOffered.uuid\n" +
                "                AND subject_offered.deleted_at IS NULL\n" +
                "                AND academic_sessions.deleted_at IS NULL\n" +
                "                AND course_subject.deleted_at IS NULL\n" +
                "                AND courses.deleted_at IS NULL\n" +
                "                AND subject_obes.deleted_at IS NULL\n" +
                "                AND subjects.deleted_at IS NULL\n" +
                "            )\n" +
                "            ELSE ''\n" +
                "       END AS name\n" +
                " FROM subject_outline_offered AS subjectOutlineOffered\n" +
                " WHERE subjectOutlineOffered.deleted_at IS NULL\n" +
                ") \n" +
                "SELECT * FROM subjectOutlineOffered \n" +
                "WHERE subjectOutlineOffered.name ILIKE '%" + name + "%' " +
                "ORDER BY subjectOutlineOffered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;

    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusCheck(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "WITH subjectOutlineOffered AS (\n" +
                " SELECT subjectOutlineOffered.*,\n" +
                "       CASE WHEN subjectOutlineOffered.subject_outline_uuid IS NOT NULL AND subjectOutlineOffered.subject_obe_uuid IS NULL\n" +
                "           THEN (\n" +
                "               SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name)\n" +
                "               FROM subject_outline_offered \n" +
                "                        JOIN subject_outlines ON subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
                "                        JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                "                        JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "                        JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
                "                        JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
                "                        JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                "               WHERE subject_outline_offered.deleted_at IS NULL\n" +
                "               AND subject_outline_offered.uuid = subjectOutlineOffered.uuid\n" +
                "               AND subject_offered.deleted_at IS NULL\n" +
                "               AND academic_sessions.deleted_at IS NULL\n" +
                "               AND course_subject.deleted_at IS NULL\n" +
                "               AND courses.deleted_at IS NULL\n" +
                "               AND subject_outlines.deleted_at IS NULL\n" +
                "               AND subjects.deleted_at IS NULL\n" +
                "           )\n" +
                "            WHEN subjectOutlineOffered.subject_obe_uuid IS NOT NULL AND subjectOutlineOffered.subject_outline_uuid IS NULL\n" +
                "                THEN (\n" +
                "                SELECT  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name)\n" +
                "                FROM subject_outline_offered\n" +
                "                         JOIN subject_obes ON subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
                "                         JOIN subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                "                         JOIN course_subject ON course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "                         JOIN courses ON courses.uuid=course_subject.course_uuid\n" +
                "                         JOIN subjects ON subjects.uuid=course_subject.subject_uuid\n" +
                "                         JOIN academic_sessions ON academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                "                WHERE subject_outline_offered.deleted_at IS NULL\n" +
                "                AND subject_outline_offered.uuid = subjectOutlineOffered.uuid\n" +
                "                AND subject_offered.deleted_at IS NULL\n" +
                "                AND academic_sessions.deleted_at IS NULL\n" +
                "                AND course_subject.deleted_at IS NULL\n" +
                "                AND courses.deleted_at IS NULL\n" +
                "                AND subject_obes.deleted_at IS NULL\n" +
                "                AND subjects.deleted_at IS NULL\n" +
                "            )\n" +
                "            ELSE ''\n" +
                "       END AS name\n" +
                " FROM subject_outline_offered AS subjectOutlineOffered\n" +
                " WHERE subjectOutlineOffered.deleted_at IS NULL\n" +
                ") \n" +
                "SELECT * FROM subjectOutlineOffered \n" +
                "WHERE subjectOutlineOffered.name ILIKE '%" + name + "%' " +
                " and subjectOutlineOffered.status =" + status +
                " ORDER BY subjectOutlineOffered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedAgainstOBETrue(Boolean obe, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid\n" +
                " where subject_outline_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null\n" +
                " and subject_obes.deleted_at is null\n" +
                " and subject_outline_offered.obe = " + obe +
                " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) ILIKE '%" + name + "%' " +
                "ORDER BY subject_outline_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedAgainstOBEFalse(Boolean obe, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid\n" +
                " where subject_outline_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null\n" +
                " and subject_outlines.deleted_at is null\n" +
                " and subject_outline_offered.obe = " + obe +
                " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) ILIKE '%" + name + "%' " +
                "ORDER BY subject_outline_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusAndOBETrue(Boolean obe, Boolean status, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid\n" +
                " where subject_outline_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null\n" +
                " and subject_obes.deleted_at is null\n" +
                " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) ILIKE '%" + name + "%' " +
                " and subject_outline_offered.obe = " + obe +
                " and subject_outline_offered.status = " + status +
                " ORDER BY subject_outline_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOutlineOfferedDto> indexSubjectOutlineOfferedWithStatusAndOBEFalse(Boolean obe, Boolean status, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid \n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid \n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
                " join courses on courses.uuid=course_subject.course_uuid \n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid \n" +
                " where subject_outline_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null\n" +
                " and subject_outlines.deleted_at is null\n" +
                " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) ILIKE '%" + name + "%' " +
                " and subject_outline_offered.obe = " + obe +
                " and subject_outline_offered.status = " + status +
                " ORDER BY subject_outline_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Flux<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveSubjectOutlineOfferedDto> showSubjectOutlineOffered(UUID subjectOutlineOfferedUUID) {

        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid\n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid\n" +
                " where subject_outline_offered.uuid =  '" + subjectOutlineOfferedUUID +
                "' and subject_outline_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and subject_outlines.deleted_at is null";

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Mono<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .one();

        return result;
    }

    @Override
    public Mono<SlaveSubjectOutlineOfferedDto> showSubjectOutlineOfferedAgainstOBE(UUID subjectOutlineOfferedUUID, Boolean obe) {

        String query = "select subject_outline_offered.*,concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name) as name\n" +
                " from subject_outline_offered\n" +
                " join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid\n" +
                " join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid\n" +
                " join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid\n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid\n" +
                " where subject_outline_offered.uuid =  '" + subjectOutlineOfferedUUID +
                "' and subject_outline_offered.deleted_at is null\n" +
                " and subject_outline_offered.obe = " + obe +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and subject_obes.deleted_at is null";

        SlaveCustomSubjectOutlineOfferedMapper mapper = new SlaveCustomSubjectOutlineOfferedMapper();

        Mono<SlaveSubjectOutlineOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOutlineOfferedDto))
                .one();

        return result;
    }

}
