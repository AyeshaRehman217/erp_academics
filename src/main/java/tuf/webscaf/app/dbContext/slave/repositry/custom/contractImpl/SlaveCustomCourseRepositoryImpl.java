package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCourseDtoMapper;

import java.util.UUID;

public class SlaveCustomCourseRepositoryImpl implements SlaveCustomCourseRepository {
    private DatabaseClient client;
    private SlaveCourseDto slaveCourseDto;


    @Autowired
    public SlaveCustomCourseRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCourseDto> indexWithStudent(UUID studentUUID,String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select *,concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid = courses.uuid\n" +
                "join registrations on registrations.campus_course_uuid = campus_course.uuid\n" +
                "where registrations.student_uuid = '" + studentUUID +
                "' and campus_course.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                " and (courses.name ILIKE '%" + name + "%'" +
                " OR courses.description ILIKE '%" + description + "%'" +
                " OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                " OR courses.short_name ILIKE '%" + shortName + "%'" +
                " OR courses.slug ILIKE '%" + slug + "%'" +
                " OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithStudentAndStatus(UUID studentUUID, Boolean status,String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select *,concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid = courses.uuid\n" +
                "join registrations on registrations.campus_course_uuid = campus_course.uuid\n" +
                "where registrations.student_uuid = '" + studentUUID +
                "' and courses.status =" + status +
                " and campus_course.deleted_at is null\n" +
                "and registrations.deleted_at is null\n" +
                "and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                " and (courses.name ILIKE '%" + name + "%'" +
                " OR courses.description ILIKE '%" + description + "%'" +
                " OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                " OR courses.short_name ILIKE '%" + shortName + "%'" +
                " OR courses.slug ILIKE '%" + slug + "%'" +
                " OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> index(String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                " and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithStatus(String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveCourseDto> showByUUID(UUID courseUUID) {
        String query = "select *, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "where courses.deleted_at is null\n" +
                "and courses.uuid = '" + courseUUID +
                "' and course_levels.deleted_at is null\n";

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Mono<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .one();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampusCourseAndAcademicSession(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*,concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "where courses.uuid= '" + courseUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.deleted_at is null \n" +
                "and campus_course.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampusCourseAndAcademicSessionWithStatus(UUID campusUUID, UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                "where courses.uuid= '" + courseUUID +
                "' and campuses.uuid= '" + campusUUID +
                "' and academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.status =" + status +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "'and campuses.uuid= '" + campusUUID +
                "' and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " join campuses on campus_course.campus_uuid=campuses.uuid\n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "'and campuses.uuid= '" + campusUUID +
                "' and courses.status =" + status +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and campuses.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCoursesAndAcademicSession(UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "'and courses.uuid= '" + courseUUID +
                "' and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCoursesAndAcademicSessionWithStatus(UUID courseUUID, UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "'and courses.uuid= '" + courseUUID +
                "' and courses.status =" + status +
                " and courses.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                "and course_offered.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCoursesAndCampus(UUID courseUUID, UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
                " where campuses.uuid= '" + campusUUID +
                "'and courses.uuid= '" + courseUUID +
                "' and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                " and campus_course.deleted_at is null\n" +
                " and campuses.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCoursesAndCampusWithStatus(UUID courseUUID, UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
                " where campuses.uuid= '" + campusUUID +
                "'and courses.uuid= '" + courseUUID +
                "' and courses.status =" + status +
                " and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                " and campus_course.deleted_at is null\n" +
                " and campuses.deleted_at is null \n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campuses.uuid=campus_course.campus_uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and campuses.deleted_at is null\n" +
                "and campus_course.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCampusWithStatus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join campuses on campuses.uuid=campus_course.campus_uuid\n" +
                "where campuses.uuid= '" + campusUUID +
                "' and courses.status =" + status +
                " and campuses.deleted_at is null\n" +
                " and campus_course.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                " and courses.deleted_at is null\n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCourses(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " where courses.uuid= '" + courseUUID +
                "' and courses.deleted_at is null \n" +
                "and campus_course.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithCoursesWithStatus(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join campus_course on campus_course.course_uuid=courses.uuid\n" +
                " where courses.uuid= '" + courseUUID +
                "' and courses.status =" + status +
                " and courses.deleted_at is null \n" +
                "and campus_course.deleted_at is null \n" +
                " and course_levels.deleted_at is null\n" +
                "and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithAcademicSession(UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join course_offered on campus_course.uuid=course_offered.campus_course_uuid\n" +
                "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "' and course_offered.deleted_at is null\n" +
                " and campus_course.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and courses.deleted_at is null \n" +
                " and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseDto> indexWithAcademicSessionsWithStatus(UUID academicSessionUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description, String dp, String d, Integer size, Long page) {
        String query = "select courses.*, concat(course_levels.short_name,'|',courses.name) as key from courses\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join campus_course on campus_course.course_uuid=courses.uuid\n" +
                "join course_offered on campus_course.uuid=course_offered.campus_course_uuid\n" +
                "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
                " where academic_sessions.uuid= '" + academicSessionUUID +
                "' and courses.status =" + status +
                " and course_offered.deleted_at is null\n" +
                " and campus_course.deleted_at is null\n" +
                " and academic_sessions.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                " and courses.deleted_at is null \n" +
                " and (courses.name ILIKE '%" + name + "%'" +
                "OR courses.description ILIKE '%" + description + "%'" +
                "OR concat(course_levels.short_name,'|',courses.name) ILIKE '%" + key + "%'" +
                "OR courses.short_name ILIKE '%" + shortName + "%'" +
                "OR courses.slug ILIKE '%" + slug + "%'" +
                "OR courses.code ILIKE '%" + code + "%')" +
                " ORDER BY courses." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseDtoMapper mapper = new SlaveCustomCourseDtoMapper();

        Flux<SlaveCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseDto))
                .all();

        return result;
    }
}
