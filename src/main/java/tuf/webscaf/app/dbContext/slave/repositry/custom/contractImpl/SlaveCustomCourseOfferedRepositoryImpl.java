package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseOfferedRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCourseOfferedMapper;

import java.util.UUID;

public class SlaveCustomCourseOfferedRepositoryImpl implements SlaveCustomCourseOfferedRepository {
    private DatabaseClient client;
    private SlaveCourseOfferedDto slaveCourseOfferedDto;

    @Autowired
    public SlaveCustomCourseOfferedRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndex(String name, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid \n" +
                " join courses on courses.uuid = campus_course.course_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid\n" +
                " join courses on courses.uuid = campus_course.course_uuid\n" +
                " where academic_sessions.deleted_at is null \n" +
                " and course_offered.status = "+ status +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " AND concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithCampusFilter(UUID campusUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid \n" +
                " join courses on courses.uuid = campus_course.course_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and campuses.uuid = '" + campusUUID +
                "' and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusAndCampus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid\n" +
                " join courses on courses.uuid = campus_course.course_uuid\n" +
                " where academic_sessions.deleted_at is null \n" +
                " and course_offered.status = "+ status +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and campuses.uuid = '" + campusUUID +
                "' AND concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithSessionFilter(UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid \n" +
                " join courses on courses.uuid = campus_course.course_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and academic_sessions.uuid = '" + academicSessionUUID +
                "' and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusAndSession(UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid\n" +
                " join courses on courses.uuid = campus_course.course_uuid\n" +
                " where academic_sessions.deleted_at is null \n" +
                " and course_offered.status = "+ status +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and academic_sessions.uuid = '" + academicSessionUUID +
                "' AND concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithCampusAndSessionFilter(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid \n" +
                " join courses on courses.uuid = campus_course.course_uuid \n" +
                " where academic_sessions.deleted_at is null \n" +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and campuses.uuid = '" + campusUUID +
                "' and academic_sessions.uuid = '" + academicSessionUUID +
                "' and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseOfferedDto> courseOfferedIndexWithStatusSessionAndCampus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as key \n" +
                " from course_offered \n" +
                " join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
                " join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
                " join campuses on campuses.uuid = campus_course.campus_uuid\n" +
                " join courses on courses.uuid = campus_course.course_uuid\n" +
                " where academic_sessions.deleted_at is null \n" +
                " and course_offered.status = "+ status +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and course_offered.deleted_at is null" +
                " and campuses.uuid = '" + campusUUID +
                "' and academic_sessions.uuid = '" + academicSessionUUID +
                "' AND concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE '%" + name + "%' " +
                " ORDER BY course_offered." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseOfferedMapper mapper = new SlaveCustomCourseOfferedMapper();

        Flux<SlaveCourseOfferedDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseOfferedDto))
                .all();

        return result;
    }
}
