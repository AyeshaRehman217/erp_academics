package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectOfferedRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSubjectOfferedMapper;

import java.util.UUID;

public class SlaveCustomSubjectOfferedRepositoryImpl implements SlaveCustomSubjectOfferedRepository {
    private DatabaseClient client;
    private SlaveSubjectOfferedDto slaveSubjectOfferedDto;

    @Autowired
    public SlaveCustomSubjectOfferedRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndex(String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where academic_sessions.deleted_at is null\n" +
                " and subject_offered.status = " + status +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    /**
     * Fetch Subject offered Against Course and Student with and without status filter
     **/
    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstStudentAndCourseWithStatus(Boolean status, UUID studentUUID, UUID courseUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                "join courses on courses.uuid=course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join students on registrations.student_uuid=students.uuid \n" +
                "where students.uuid = '" + studentUUID +
                "' and courses.uuid = '" + courseUUID +
                "' and registrations.academic_session_uuid=subject_offered.academic_session_uuid\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and registrations.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and students.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstStudentAndCourse(UUID studentUUID, UUID courseUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                "join courses on courses.uuid=course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join students on registrations.student_uuid=students.uuid \n" +
                "where students.uuid = '" + studentUUID +
                "' and courses.uuid = '" + courseUUID +
                "' and registrations.academic_session_uuid=subject_offered.academic_session_uuid\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and courses.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                " and subjects.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    /**
     * Fetch Subject offered Against Course with and without status filter
     **/
    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseWithStatus(Boolean status, UUID courseUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                "CASE WHEN course_subject.obe     \n" +
                "THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE  concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where academic_sessions.deleted_at is null\n" +
                " and courses.uuid= '" + courseUUID +
                "' and subject_offered.status = " + status +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                " WHEN course_subject.obe  \n" +
                " THEN \n" +
                " concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' \n" +
                " ELSE     \n" +
                " concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%'   \n" +
                " END\n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourse(UUID courseUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                "join courses on courses.uuid=course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                "where courses.uuid= '" + courseUUID +
                "' and course_offered.academic_session_uuid=subject_offered.academic_session_uuid\n" +
                " and campus_course.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null \n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_offered.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null" +
                " and subjects.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithStatusAndOBE(Boolean status, Boolean obe, String name, String dp, String d, Integer size, Long page) {

        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where academic_sessions.deleted_at is null\n" +
                " and subject_offered.status = " + status +
                " and course_subject.obe = " + obe +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexWithOBE(Boolean obe, String name, String dp, String d, Integer size, Long page) {

        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name," +
                "" +
                " '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and course_subject.obe = " + obe +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseWithStatusAndOBE(Boolean status, Boolean obe, UUID courseUUID, String name, String dp, String d, Integer size, Long page) {

        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where academic_sessions.deleted_at is null\n" +
                " and courses.uuid= '" + courseUUID +
                "' and subject_offered.status = " + status +
                " and course_subject.obe = " + obe +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> subjectOfferedIndexAgainstCourseAndOBE(Boolean obe, UUID courseUUID, String name, String dp, String d, Integer size, Long page) {

        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from subject_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
                " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where academic_sessions.deleted_at is null\n" +
                " and courses.uuid= '" + courseUUID +
                "' and course_subject.obe = " + obe +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }


    /**
     * All the Functions Below is used by Enrollments (The Functions Fetch Subject Offered based on combinations of Academic Session , campus and Courses)
     **/
    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where academic_sessions.uuid = '" + academicSessionUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where academic_sessions.uuid = '" + academicSessionUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampus(UUID campusUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCampusWithStatus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on courses.uuid=campus_course.course_uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null \n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCourses(UUID courseUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithCoursesWithStatus(UUID courseUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where courses.uuid= '" + courseUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithAcademicSession(UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectOfferedDto> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, Boolean status, String name, String dp, String d, Integer size, Long page) {
        String query = "select subject_offered.*, course_subject.obe,\n" +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                "from subject_offered\n" +
                "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where academic_sessions.uuid= '" + academicSessionUUID +
                "' and subject_offered.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null" +
                "and course_levels.deleted_at is null \n" +
                " and subject_offered.status = " + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY subject_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectOfferedMapper mapper = new SlaveCustomSubjectOfferedMapper();

        Flux<SlaveSubjectOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectOfferedDto))
                .all();

        return result;
    }
}
