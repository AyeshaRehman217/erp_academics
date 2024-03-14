package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegisteredCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentRegisteredCourseMapper;

import java.util.UUID;

public class SlaveCustomStudentRepositoryImpl implements SlaveCustomStudentRepository {
    private DatabaseClient client;
    private SlaveStudentEntity slaveStudentEntity;
    private SlaveStudentRegisteredCourseDto slaveStudentRegisteredCourseDto;

    @Autowired
    public SlaveCustomStudentRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentEntity> indexStudentsWithoutStatus(String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students \n" +
                "where students.deleted_at is null \n" +
                "AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexStudentsWithStatus(String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students \n" +
                "where students.deleted_at is null \n" +
                " and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentRegisteredCourseDto> indexWithCourseOffered(UUID courseOfferedUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select *, course_offered.uuid as courseOffered from students \n" +
                "join registrations on students.uuid = registrations.student_uuid\n" +
                "join campus_course on campus_course.uuid = registrations.campus_course_uuid\n" +
                "join course_offered on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "where course_offered.uuid = '" + courseOfferedUUID +
                "' and students.deleted_at IS NULL\n" +
                "and registrations.deleted_at IS NULL\n" +
                "and campus_course.deleted_at IS NULL \n" +
                "and course_offered.deleted_at IS NULL\n" +
                "AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentRegisteredCourseMapper mapper = new SlaveCustomStudentRegisteredCourseMapper();

        Flux<SlaveStudentRegisteredCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentRegisteredCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentRegisteredCourseDto> indexWithCourseOfferedWithStatus(UUID courseOfferedUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select *,course_offered.uuid as courseOffered from students \n" +
                "join registrations on students.uuid = registrations.student_uuid\n" +
                "join campus_course on campus_course.uuid = registrations.campus_course_uuid\n" +
                "join course_offered on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "where course_offered.uuid = '" + courseOfferedUUID +
                "' and students.status =" + status +
                " and students.deleted_at IS NULL\n" +
                "and registrations.deleted_at IS NULL\n" +
                "and campus_course.deleted_at IS NULL \n" +
                "and course_offered.deleted_at IS NULL\n" +
                "AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentRegisteredCourseMapper mapper = new SlaveCustomStudentRegisteredCourseMapper();

        Flux<SlaveStudentRegisteredCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentRegisteredCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students \n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null \n" +
                "and registrations.deleted_at is null \n" +
                "and campus_course.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                "and courses.uuid = '" + courseUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid = '" + campusUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students \n" +
                "join registrations on registrations.student_uuid=students.uuid \n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
                "join courses on campus_course.course_uuid=courses.uuid \n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
                "where students.deleted_at is null \n" +
                "and registrations.deleted_at is null \n" +
                "and campus_course.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and courses.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                "and courses.uuid = '" + courseUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid = '" + campusUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid = '" + campusUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' and campuses.uuid = '" + campusUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.uuid = '" + courseUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.uuid = '" + courseUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and campuses.uuid= '" + campusUUID +
                "' and courses.uuid = '" + courseUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and campuses.uuid= '" + campusUUID +
                "' and courses.uuid = '" + courseUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampus(UUID campusUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null \n" +
                "and campuses.uuid= '" + campusUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCampusWithStatus(UUID campusUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and campuses.deleted_at is null \n" +
                "and campuses.uuid= '" + campusUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCourses(UUID courseUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and courses.deleted_at is null \n" +
                "and courses.uuid= '" + courseUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithCoursesWithStatus(UUID courseUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
                "join courses on campus_course.course_uuid=courses.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and courses.deleted_at is null \n" +
                "and courses.uuid= '" + courseUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithAcademicSession(UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid\n" +
                "where students.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and academic_sessions.uuid= '" + academicSessionUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> findAllStudentsAgainstTeacherWithSameCourseSubjectAndSession(UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                " from students \n" +
                " join registrations on registrations.student_uuid=students.uuid \n" +
                " join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid \n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
                " join enrollments on enrollments.student_uuid=students.uuid \n" +
                " join subject_offered on subject_offered.uuid=enrollments.subject_offered_uuid \n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid \n" +
                " join courses on courses.uuid=course_subject.course_uuid \n" +
                " join commencement_of_classes on subjects.uuid=commencement_of_classes.subject_uuid \n" +
                " join teachers on teachers.uuid=commencement_of_classes.teacher_uuid \n" +
                " where students.deleted_at is null \n" +
                " and  course_offered.academic_session_uuid=subject_offered.academic_session_uuid \n" +
                " and  enrollments.uuid=commencement_of_classes.enrollment_uuid \n" +
                " and  academic_sessions.uuid=commencement_of_classes.academic_session_uuid\n" +
                " and  registrations.deleted_at is null\n" +
                " and  enrollments.deleted_at is null \n" +
                " and  campus_course.deleted_at is null \n" +
                " and  course_offered.deleted_at is null \n" +
                " and  academic_sessions.deleted_at is null \n" +
                " and  subject_offered.deleted_at is null \n" +
                " and  course_subject.deleted_at is null \n" +
                " and  subjects.deleted_at is null \n" +
                " and  courses.deleted_at is null \n" +
                " and  commencement_of_classes.deleted_at is null \n" +
                " and  enrollments.deleted_at is null \n" +
                " and  teachers.deleted_at is null\n" +
                " and teachers.uuid= '" + teacherUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and course_subject.uuid= '" + courseSubjectUUID +
                "' AND students.student_id ILIKE '%" + studentId + "%' " +
                " ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> findAllStudentsAgainstTeacherWithSameCourseSubjectAndSessionAndStatus(Boolean status, UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId, String dp, String d, Integer size, Long page) {
        String query = "select students.* \n" +
                " from students \n" +
                " join registrations on registrations.student_uuid=students.uuid \n" +
                " join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid \n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
                " join enrollments on enrollments.student_uuid=students.uuid \n" +
                " join subject_offered on subject_offered.uuid=enrollments.subject_offered_uuid \n" +
                " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
                " join subjects on subjects.uuid=course_subject.subject_uuid \n" +
                " join courses on courses.uuid=course_subject.course_uuid \n" +
                " join commencement_of_classes on subjects.uuid=commencement_of_classes.subject_uuid \n" +
                " join teachers on teachers.uuid=commencement_of_classes.teacher_uuid \n" +
                " where students.deleted_at is null \n" +
                " and  course_offered.academic_session_uuid=subject_offered.academic_session_uuid \n" +
                " and  enrollments.uuid=commencement_of_classes.enrollment_uuid \n" +
                " and  academic_sessions.uuid=commencement_of_classes.academic_session_uuid\n" +
                " and  registrations.deleted_at is null\n" +
                " and  enrollments.deleted_at is null \n" +
                " and  campus_course.deleted_at is null \n" +
                " and  course_offered.deleted_at is null \n" +
                " and  academic_sessions.deleted_at is null \n" +
                " and  subject_offered.deleted_at is null \n" +
                " and  course_subject.deleted_at is null \n" +
                " and  subjects.deleted_at is null \n" +
                " and  courses.deleted_at is null \n" +
                " and  commencement_of_classes.deleted_at is null \n" +
                " and  enrollments.deleted_at is null \n" +
                " and  teachers.deleted_at is null\n" +
                " and teachers.uuid= '" + teacherUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and course_subject.uuid= '" + courseSubjectUUID +
                "' and students.status =" + status +
                " AND students.student_id ILIKE '%" + studentId + "%' " +
                " ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

}
