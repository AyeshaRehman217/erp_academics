package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegisteredCourseDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.*;

import java.util.UUID;

public class SlaveCustomSubjectRepositoryImpl implements SlaveCustomSubjectRepository {
    private DatabaseClient client;
    private SlaveSubjectEntity slaveSubjectEntity;
    private SlaveSubjectDto slaveSubjectDto;
    private SlaveEnrolledSubjectDto slaveEnrolledSubjectDto;
    private SlaveEnrolledCourseSubjectDto slaveEnrolledCourseSubjectDto;

    @Autowired
    public SlaveCustomSubjectRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveSubjectEntity> indexSubjectAgainstAcademicSession(UUID academicSessionUUID, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page) {
        String query = "select distinct subjects.* \n" +
                " from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
                "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
                "where subject_offered.academic_session_uuid= '" + academicSessionUUID +
                "' and subjects.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%'  or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> indexSubjectAgainstAcademicSessionWithStatusFilter(UUID academicSessionUUID, Boolean status, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page) {
        String query = "select distinct subjects.* \n" +
                " from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
                "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
                "where subject_offered.academic_session_uuid= '" + academicSessionUUID +
                "' and subjects.status =" + status +
                " and subjects.deleted_at is null \n" +
                "and course_subject.deleted_at is null \n" +
                "and subject_offered.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }


    @Override
    public Flux<SlaveEnrolledSubjectDto> fetchEnrolledSubjectWithStatusFilter(UUID studentUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours, \n" +
                " semesters.name as semesterName,semesters.semester_no as semesterNo  from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join courses on course_subject.course_uuid=courses.uuid\n" +
                " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join enrollments on subject_offered.uuid=enrollments.subject_offered_uuid\n" +
                " join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid\n" +
                " join students on enrollments.student_uuid=students.uuid\n" +
                " join semesters on enrollments.semester_uuid=semesters.uuid\n" +
                " where subjects.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and enrollments.deleted_at is null\n" +
                " and students.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                " and semesters.deleted_at is null\n" +
                " and subjects.status  = " + status +
                " and students.uuid= '" + studentUUID +
                "' and ( subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%'  " +
                " or semesters.name ILIKE '%" + semesterName + "%'  " +
                " or semesters.semester_no ILIKE '%" + semesterNo + "%'  ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledSubjectMapper mapper = new SlaveCustomEnrolledSubjectMapper();

        Flux<SlaveEnrolledSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrolledSubjectDto> fetchEnrolledSubjectWithoutStatusFilter(UUID studentUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours, \n" +
                " semesters.name as semesterName,semesters.semester_no as semesterNo  from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join courses on course_subject.course_uuid=courses.uuid\n" +
                " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join enrollments on subject_offered.uuid=enrollments.subject_offered_uuid \n" +
                " join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
                " join students on enrollments.student_uuid=students.uuid \n" +
                " join semesters on enrollments.semester_uuid=semesters.uuid \n" +
                " where subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and enrollments.deleted_at is null \n" +
                " and students.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and students.uuid= '" + studentUUID +
                "' and (subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%'  " +
                " or semesters.name ILIKE '%" + semesterName + "%'  " +
                " or semesters.semester_no ILIKE '%" + semesterNo + "%'  ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledSubjectMapper mapper = new SlaveCustomEnrolledSubjectMapper();

        Flux<SlaveEnrolledSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrolledSubjectDto> fetchSubjectAgainstCourseAndStudentWithStatusFilter(UUID studentUUID, UUID courseUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours, \n" +
                " semesters.name as semesterName,semesters.semester_no as semesterNo  from students \n" +
                " JOIN registrations ON registrations.student_uuid = students.uuid\n" +
                " JOIN campus_course ON registrations.campus_course_uuid = campus_course.uuid\n" +
                " JOIN enrollments ON enrollments.course_uuid= campus_course.course_uuid\n" +
                " AND enrollments.student_uuid = students.uuid\n" +
                " AND enrollments.campus_uuid = campus_course.campus_uuid\n" +
                " JOIN subject_offered ON enrollments.subject_offered_uuid = subject_offered.uuid\n" +
                " JOIN course_subject ON subject_offered.course_subject_uuid = course_subject.uuid\n" +
                " JOIN subjects ON course_subject.subject_uuid = subjects.uuid\n" +
                " JOIN courses ON course_subject.course_uuid=courses.uuid\n" +
                " JOIN semesters ON enrollments.semester_uuid=semesters.uuid\n" +
                " where students.deleted_at is null \n" +
                " and subjects.status  = " + status +
                " and registrations.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and enrollments.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and students.uuid= '" + studentUUID +
                "' and enrollments.course_uuid= '" + courseUUID +
                "' and (subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%'  " +
                " or semesters.name ILIKE '%" + semesterName + "%'  " +
                " or semesters.semester_no ILIKE '%" + semesterNo + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledSubjectMapper mapper = new SlaveCustomEnrolledSubjectMapper();

        Flux<SlaveEnrolledSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrolledSubjectDto> fetchSubjectAgainstCourseAndStudentWithoutStatusFilter(UUID studentUUID, UUID courseUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours, \n" +
                " semesters.name as semesterName,semesters.semester_no as semesterNo  from students \n" +
                " JOIN registrations ON registrations.student_uuid = students.uuid\n" +
                " JOIN campus_course ON registrations.campus_course_uuid = campus_course.uuid\n" +
                " JOIN enrollments ON enrollments.course_uuid= campus_course.course_uuid\n" +
                " AND enrollments.student_uuid = students.uuid\n" +
                " AND enrollments.campus_uuid = campus_course.campus_uuid\n" +
                " JOIN subject_offered ON enrollments.subject_offered_uuid = subject_offered.uuid\n" +
                " JOIN course_subject ON subject_offered.course_subject_uuid = course_subject.uuid\n" +
                " JOIN subjects ON course_subject.subject_uuid = subjects.uuid\n" +
                " JOIN courses ON course_subject.course_uuid=courses.uuid\n" +
                " JOIN semesters ON enrollments.semester_uuid=semesters.uuid\n" +
                " where students.deleted_at is null \n" +
                " and registrations.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and enrollments.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and semesters.deleted_at is null \n" +
                " and students.uuid= '" + studentUUID +
                "' and enrollments.course_uuid= '" + courseUUID +
                "' and (subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%'  " +
                " or semesters.name ILIKE '%" + semesterName + "%'  " +
                " or semesters.semester_no ILIKE '%" + semesterNo + "%'  ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledSubjectMapper mapper = new SlaveCustomEnrolledSubjectMapper();

        Flux<SlaveEnrolledSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrolledCourseSubjectDto> fetchSubjectAgainstCourseWithStatusFilter(UUID courseUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " join courses on course_subject.course_uuid=courses.uuid\n" +
                " where subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null  \n" +
                " and subjects.status  = " + status +
                " and courses.uuid= '" + courseUUID +
                "' and (subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledCourseSubjectMapper mapper = new SlaveCustomEnrolledCourseSubjectMapper();

        Flux<SlaveEnrolledCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveEnrolledCourseSubjectDto> fetchSubjectAgainstCourseWithoutStatusFilter(UUID courseUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.code as courseCode,course_subject.total_credit_hours as totalCreditHours from subjects \n" +
                " join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " join courses on course_subject.course_uuid=courses.uuid\n" +
                " where subjects.deleted_at is null \n" +
                " and course_subject.deleted_at is null \n" +
                " and subject_offered.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null  \n" +
                " and courses.uuid= '" + courseUUID +
                "' and (subjects.name ILIKE '%" + subjectName + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' " +
                " or subjects.code ILIKE '%" + subjectCode + "%'  " +
                " or subjects.slug ILIKE '%" + slug + "%'  " +
                " or courses.name ILIKE '%" + courseName + "%'  " +
                " or courses.code ILIKE '%" + courseCode + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomEnrolledCourseSubjectMapper mapper = new SlaveCustomEnrolledCourseSubjectMapper();

        Flux<SlaveEnrolledCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEnrolledCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> fetchSubjectWithStatusFilter(Boolean status, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects \n" +
                " where subjects.deleted_at is null \n" +
                " and subjects.status = " + status +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> fetchSubjectWithoutStatusFilter(String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects \n" +
                " where subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectDto> fetchSubjectWithStatusAndOpenLMSFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, Boolean status, String key, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page) {
        String query = "Select subjects.*,academic_sessions.is_open as openLMS, courses.name as courseName, courses.uuid as courseUUID,\n" +
                " concat_ws('|',academic_sessions.name,subjects.name) as key\n" +
                " from subjects " +
                " join course_subject on subjects.uuid = course_subject.subject_uuid\n" +
                " join subject_offered on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join teacher_subjects on (teacher_subjects.course_subject_uuid = course_subject.uuid\n" +
                " and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid)\n" +
                " join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
                " where teacher_subjects.teacher_uuid= '" + teacherUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and academic_sessions.is_open = " + openLMS +
                " and subjects.status = " + status +
                " and courses.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and subjects.deleted_at is null\n" +
                " and course_subject.deleted_at is null \n" +
                " and teacher_subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.name) ILIKE '%" + key + "%' )" +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectAgainstSessionAndTeacherMapper mapper = new SlaveCustomSubjectAgainstSessionAndTeacherMapper();

        Flux<SlaveSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectDto> fetchSubjectWithOpenLMSFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, String key, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page) {
        String query = "Select subjects.*,courses.name as courseName,courses.uuid as courseUUID,\n" +
                "academic_sessions.is_open as openLMS,concat_ws('|',academic_sessions.name,subjects.name) as key " +
                " from subjects\n" +
                "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                " join teacher_subjects on (teacher_subjects.course_subject_uuid = course_subject.uuid\n" +
                " and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid)\n" +
                " join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
                " where teacher_subjects.teacher_uuid= '" + teacherUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and academic_sessions.is_open = " + openLMS +
                " and academic_sessions.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and subjects.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and teacher_subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.name) ILIKE '%" + key + "%' )" +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectAgainstSessionAndTeacherMapper mapper = new SlaveCustomSubjectAgainstSessionAndTeacherMapper();

        Flux<SlaveSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectDto))
                .all();

        return result;
    }


    @Override
    public Flux<SlaveSubjectDto> showSubjectAgainstTeacherAndAcademicSession(UUID academicSessionUUID, UUID teacherUUID, String key, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page) {
        String query = "select subjects.*,courses.name as courseName,courses.uuid as courseUUID,\n" +
                "academic_sessions.is_open as openLMS,concat_ws('|',academic_sessions.name,subjects.name) as key\n" +
                "from subjects\n" +
                "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                " join courses on courses.uuid=course_subject.course_uuid\n" +
                "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "join teacher_subjects on (teacher_subjects.course_subject_uuid = course_subject.uuid\n" +
                "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid)\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid= '" + teacherUUID +
                "' and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%'  " +
                " or concat_ws('|',academic_sessions.name,subjects.name) ILIKE '%" + key + "%' )" +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectAgainstSessionAndTeacherMapper mapper = new SlaveCustomSubjectAgainstSessionAndTeacherMapper();

        Flux<SlaveSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectDto> showSubjectAgainstTeacherAndAcademicSessionWithStatusFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean status, String key, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page) {

        String query = "select subjects.*, courses.name as courseName,courses.uuid as courseUUID,\n" +
                "academic_sessions.is_open as openLMS,concat_ws('|',academic_sessions.name,subjects.name) as key\n" +
                "from subjects\n" +
                "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
                "join courses on courses.uuid=course_subject.course_uuid\n" +
                "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
                "join teacher_subjects on (teacher_subjects.course_subject_uuid = course_subject.uuid\n" +
                "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid)\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid= '" + teacherUUID +
                "' and subjects.deleted_at is null\n" +
                " and subjects.status =" + status +
                " and course_subject.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' " +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%'  " +
                " or concat_ws('|',academic_sessions.name,subjects.name) ILIKE '%" + key + "%' )" +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectAgainstSessionAndTeacherMapper mapper = new SlaveCustomSubjectAgainstSessionAndTeacherMapper();

        Flux<SlaveSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> fetchSubjectAgainstStudentCourseAndSemesterWithStatusFilter(Boolean status, String name, String shortName, String description, String slug, String code, UUID studentUUID, UUID courseUUID, UUID semesterUUID, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects\n" +
                " join course_subject on subjects.uuid = course_subject.subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join subject_offered on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join enrollments on enrollments.subject_offered_uuid = subject_offered.uuid\n" +
                " join students on enrollments.student_uuid = students.uuid\n" +
//                " join registrations on registrations.student_uuid = students.uuid\n" +
                " where enrollments.semester_uuid = '" + semesterUUID +
                "' and courses.uuid ='" + courseUUID +
                "' and students.uuid ='" + studentUUID +
//                "' and registrations.deleted_at is null\n" +
                "' and students.deleted_at is null\n" +
                " and enrollments.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and subjects.status = " + status +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> fetchSubjectAgainstStudentCourseAndSemesterWithoutStatusFilter(String name, String shortName, String description, String slug, String code, UUID studentUUID, UUID courseUUID, UUID semesterUUID, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects\n" +
                " join course_subject on subjects.uuid = course_subject.subject_uuid\n" +
                " join courses on courses.uuid = course_subject.course_uuid\n" +
                " join subject_offered on course_subject.uuid = subject_offered.course_subject_uuid\n" +
                " join enrollments on enrollments.subject_offered_uuid = subject_offered.uuid\n" +
                " join students on enrollments.student_uuid = students.uuid\n" +
//                " join registrations on registrations.student_uuid = students.uuid\n" +
                " where enrollments.semester_uuid = '" + semesterUUID +
                "' and courses.uuid ='" + courseUUID +
                "' and students.uuid ='" + studentUUID +
//                "' and registrations.deleted_at is null\n" +
                "' and students.deleted_at is null\n" +
                " and enrollments.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                " and subject_offered.deleted_at is null\n" +
                " and course_subject.deleted_at is null\n" +
                " and subjects.deleted_at is null \n" +
                " and (subjects.name ILIKE '%" + name + "%' or subjects.slug ILIKE '%" + slug + "%'" +
                " or subjects.short_name ILIKE '%" + shortName + "%' " +
                " or subjects.description ILIKE '%" + description + "%' or subjects.code ILIKE '%" + code + "%' ) " +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

}
