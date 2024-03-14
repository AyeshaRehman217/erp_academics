package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrollmentDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomEnrollmentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomEnrollmentMapper;

import java.util.UUID;

public class SlaveCustomEnrollmentRepositoryImpl implements SlaveCustomEnrollmentRepository {
    private DatabaseClient client;
    private SlaveEnrollmentDto slaveEnrollmentDto;

    @Autowired
    public SlaveCustomEnrollmentRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Mono<SlaveEnrollmentDto> showRecordByUUID(UUID uuid) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                " from enrollments \n" +
                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
                "  join students on students.uuid = enrollments.student_uuid \n" +
                " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
                "  join courses on courses.uuid = course_subject.course_uuid \n" +
                "  join subjects on subjects.uuid = course_subject.subject_uuid \n" +
                " where enrollments.deleted_at is null \n" +
                " and students.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and enrollments.uuid = '" + uuid + "'";

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Mono<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .first();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexAllRecordsWithOutStatusFilter(String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                " from enrollments \n" +
                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
                "  join students on students.uuid = enrollments.student_uuid\n" +
                " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
                "  join courses on courses.uuid = course_subject.course_uuid\n" +
                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where enrollments.deleted_at is null \n" +
                " and students.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexAllRecordsWithStatusFilter(String key, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                " from enrollments \n" +
                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
                "  join students on students.uuid = enrollments.student_uuid\n" +
                "\t join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
                "  join courses on courses.uuid = course_subject.course_uuid\n" +
                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where enrollments.deleted_at is null \n" +
                " and students.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and semesters.deleted_at is null " +
                " and enrollments.status =" + status +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexAllRecordsWithAcademicSessionFilter(UUID academicSessionUUID, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                " from enrollments \n" +
                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
                "  join students on students.uuid = enrollments.student_uuid\n" +
                "\tjoin subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
                "  join courses on courses.uuid = course_subject.course_uuid\n" +
                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where enrollments.deleted_at is null \n" +
                " and enrollments.academic_session_uuid = '" + academicSessionUUID +
                "' and students.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexAllRecordsWithStatusAndAcademicSessionFilter(String key, Boolean status, UUID academicSessionUUID, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                " from enrollments \n" +
                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
                "  join students on students.uuid = enrollments.student_uuid\n" +
                "\t join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
                "  join courses on courses.uuid = course_subject.course_uuid\n" +
                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
                " where enrollments.deleted_at is null \n" +
                " and students.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and enrollments.academic_session_uuid = '" + academicSessionUUID +
                "' and enrollments.status =" + status +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexWithSubjectAndAcademicSessionFilter(UUID academicSessionUUID, UUID subjectUUID, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                "from enrollments\n" +
                "join students on enrollments.student_uuid=students.uuid\n" +
                "join semesters on enrollments.semester_uuid=semesters.uuid\n" +
                "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid\n" +
                "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "where subjects.uuid= '" + subjectUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and enrollments.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and semesters.deleted_at is null \n" +
                "and students.deleted_at is null \n" +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexWithSubjectAndAcademicSessionWithStatusFilter(UUID academicSessionUUID, UUID subjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                "from enrollments\n" +
                "join students on enrollments.student_uuid=students.uuid\n" +
                "join semesters on enrollments.semester_uuid=semesters.uuid\n" +
                "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid\n" +
                "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "where subjects.uuid= '" + subjectUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and enrollments.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and semesters.deleted_at is null \n" +
                "and students.deleted_at is null \n" +
                " and enrollments.status =" + status +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexWithSubjectFilter(UUID subjectUUID, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                "from enrollments\n" +
                "join students on enrollments.student_uuid=students.uuid\n" +
                "join semesters on enrollments.semester_uuid=semesters.uuid\n" +
                "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid\n" +
                "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "where subjects.uuid= '" + subjectUUID +
                "' and enrollments.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and semesters.deleted_at is null \n" +
                "and students.deleted_at is null \n" +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrollmentDto> indexWithSubjectWithStatusFilter(UUID subjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
                "from enrollments\n" +
                "join students on enrollments.student_uuid=students.uuid\n" +
                "join semesters on enrollments.semester_uuid=semesters.uuid\n" +
                "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid\n" +
                "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid\n" +
                "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid\n" +
                "join subjects on course_subject.subject_uuid=subjects.uuid\n" +
                "join courses on course_subject.course_uuid=courses.uuid\n" +
                "where subjects.uuid= '" + subjectUUID +
                "' and enrollments.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and semesters.deleted_at is null \n" +
                "and students.deleted_at is null \n" +
                " and enrollments.status =" + status +
                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
                " ILIKE '%" + key + "%'" +
                " ORDER BY enrollments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();

        Flux<SlaveEnrollmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrollmentDto))
                .all();

        return result;
    }

//    @Override
//    public Flux<SlaveEnrollmentDto> indexWithAcademicSessionAndCourseSubjectFilter(UUID academicSessionUUID, UUID courseSubjectUUID, String key, String dp, String d, Integer size, Long page) {
//        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
//                " from enrollments \n" +
//                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//                "  join students on students.uuid = enrollments.student_uuid\n" +
//                "\tjoin subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//                "  join courses on courses.uuid = course_subject.course_uuid\n" +
//                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
//                " where enrollments.deleted_at is null \n" +
//                " and enrollments.academic_session_uuid = '" + academicSessionUUID +
//                "' and course_subject.uuid = '" + courseSubjectUUID +
//                "' and students.deleted_at is null \n" +
//                " and subject_offered.deleted_at is null \n" +
//                " and course_subject.deleted_at is null \n" +
//                " and courses.deleted_at is null \n" +
//                " and academic_sessions.deleted_at is null \n" +
//                " and subjects.deleted_at is null \n" +
//                " and semesters.deleted_at is null \n" +
//                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//                " ILIKE '%" + key + "%'" +
//                " ORDER BY enrollments." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();
//
//        Flux<SlaveEnrollmentDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveEnrollmentDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveEnrollmentDto> indexWithStatusAndAcademicSessionAndCourseSubjectFilter(UUID academicSessionUUID, UUID courseSubjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page) {
//        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
//                " from enrollments \n" +
//                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//                "  join students on students.uuid = enrollments.student_uuid\n" +
//                "\t join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//                "  join courses on courses.uuid = course_subject.course_uuid\n" +
//                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
//                " where enrollments.deleted_at is null \n" +
//                " and students.deleted_at is null \n" +
//                " and subject_offered.deleted_at is null \n" +
//                " and course_subject.deleted_at is null \n" +
//                " and courses.deleted_at is null \n" +
//                " and subjects.deleted_at is null \n" +
//                " and academic_sessions.deleted_at is null \n" +
//                " and semesters.deleted_at is null \n" +
//                " and enrollments.academic_session_uuid = '" + academicSessionUUID +
//                "' and course_subject.uuid = '" + courseSubjectUUID +
//                "' and enrollments.status =" + status +
//                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//                " ILIKE '%" + key + "%'" +
//                " ORDER BY enrollments." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();
//
//        Flux<SlaveEnrollmentDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveEnrollmentDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveEnrollmentDto> indexWithCourseSubjectFilter(UUID courseSubjectUUID, String key, String dp, String d, Integer size, Long page) {
//        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
//                " from enrollments \n" +
//                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//                "  join students on students.uuid = enrollments.student_uuid\n" +
//                "\tjoin subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//                "  join courses on courses.uuid = course_subject.course_uuid\n" +
//                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
//                " where enrollments.deleted_at is null \n" +
//                " and course_subject.uuid = '" + courseSubjectUUID +
//                "' and students.deleted_at is null \n" +
//                " and subject_offered.deleted_at is null \n" +
//                " and course_subject.deleted_at is null \n" +
//                " and courses.deleted_at is null \n" +
//                " and academic_sessions.deleted_at is null \n" +
//                " and subjects.deleted_at is null \n" +
//                " and semesters.deleted_at is null \n" +
//                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//                " ILIKE '%" + key + "%'" +
//                " ORDER BY enrollments." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();
//
//        Flux<SlaveEnrollmentDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveEnrollmentDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveEnrollmentDto> indexWithStatusAndCourseSubjectFilter(UUID courseSubjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page) {
//        String query = "select *, concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) as key \n" +
//                " from enrollments \n" +
//                "  join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//                "  join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//                "  join students on students.uuid = enrollments.student_uuid\n" +
//                "\t join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//                "  join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//                "  join courses on courses.uuid = course_subject.course_uuid\n" +
//                "  join subjects on subjects.uuid = course_subject.subject_uuid\n" +
//                " where enrollments.deleted_at is null \n" +
//                " and students.deleted_at is null \n" +
//                " and subject_offered.deleted_at is null \n" +
//                " and course_subject.deleted_at is null \n" +
//                " and courses.deleted_at is null \n" +
//                " and subjects.deleted_at is null \n" +
//                " and academic_sessions.deleted_at is null \n" +
//                " and semesters.deleted_at is null \n" +
//                " and course_subject.uuid = '" + courseSubjectUUID +
//                "' and enrollments.status =" + status +
//                " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//                " ILIKE '%" + key + "%'" +
//                " ORDER BY enrollments." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomEnrollmentMapper mapper = new SlaveCustomEnrollmentMapper();
//
//        Flux<SlaveEnrollmentDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveEnrollmentDto))
//                .all();
//
//        return result;
//    }
}
