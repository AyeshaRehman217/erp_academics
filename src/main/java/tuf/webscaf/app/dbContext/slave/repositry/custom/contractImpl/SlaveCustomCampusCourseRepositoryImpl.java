package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCampusCourseRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCampusCourseMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCourseMapper;

import java.util.UUID;

public class SlaveCustomCampusCourseRepositoryImpl implements SlaveCustomCampusCourseRepository {
    private DatabaseClient client;
    private SlaveCampusCourseDto slaveCampusCourseDto;
    private SlaveCourseEntity slaveCourseEntity;

    @Autowired
    public SlaveCustomCampusCourseRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCampusCourseDto> campusCourseIndex(String name, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name) as key \n" +
                "from campus_course \n" +
                "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
                "join courses  on courses.uuid = campus_course.course_uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "where campuses.deleted_at is null " +
                "and courses.deleted_at is null " +
                "and course_levels.deleted_at is null " +
                "and campus_course.deleted_at is null " +
                " AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusCourseDto> campusCourseIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name)  as key\n" +
                "from campus_course \n" +
                "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
                "join courses  on courses.uuid = campus_course.course_uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "where campus_course.status = " + status +
                " and course_levels.deleted_at is null " +
                " and campuses.deleted_at is null " +
                " and courses.deleted_at is null " +
                "and campus_course.deleted_at is null " +
                "AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

    //Fetch Campus Courses Based on
    @Override
    public Flux<SlaveCampusCourseDto> campusCourseIndexWithCampusFilter(UUID campusUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name) as key \n" +
                "from campus_course \n" +
                "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
                "join courses  on courses.uuid = campus_course.course_uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "where campuses.deleted_at is null " +
                "and courses.deleted_at is null " +
                "and course_levels.deleted_at is null " +
                "and campus_course.campus_uuid = '" + campusUUID +
                "' and campus_course.deleted_at is null " +
                " AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusCourseDto> campusCourseIndexWithStatusAndCampus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name)  as key\n" +
                "from campus_course \n" +
                "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
                "join courses  on courses.uuid = campus_course.course_uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "where campus_course.status = " + status +
                " and campus_course.campus_uuid = '" + campusUUID +
                "' and campuses.deleted_at is null " +
                " and courses.deleted_at is null " +
                "and course_levels.deleted_at is null " +
                "and campus_course.deleted_at is null " +
                "AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusCourseDto> campusCourseListAgainstCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name) as key from campus_course\n" +
                "join campuses on campus_course.campus_uuid = campuses.uuid\n" +
                "join courses on campus_course.course_uuid = courses.uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "join course_offered on course_offered.campus_course_uuid = campus_course.uuid\n" +
                "where campus_course.campus_uuid = '" + campusUUID +
                "' and course_offered.academic_session_uuid = '" + academicSessionUUID +
                "' and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and course_levels.deleted_at is null " +
                "and courses.deleted_at is null\n" +
                "AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusCourseDto> campusCourseListAgainstCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name) as key from campus_course\n" +
                "join campuses on campus_course.campus_uuid = campuses.uuid\n" +
                "join courses on campus_course.course_uuid = courses.uuid\n" +
                "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
                "join course_offered on course_offered.campus_course_uuid = campus_course.uuid\n" +
                "where campus_course.campus_uuid = '" + campusUUID +
                "' and course_offered.academic_session_uuid = '" + academicSessionUUID +
                "' and campus_course.status =" + status +
                " and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and campuses.deleted_at is null\n" +
                "and course_levels.deleted_at is null " +
                "and courses.deleted_at is null\n" +
                "AND concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_course." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusCourseMapper mapper = new SlaveCustomCampusCourseMapper();

        Flux<SlaveCampusCourseDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusCourseDto))
                .all();

        return result;
    }

}
