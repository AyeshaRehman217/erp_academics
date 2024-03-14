package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseSubjectRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCourseSubjectMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSubjectMapper;

import java.util.UUID;

public class SlaveCustomCourseSubjectRepositoryImpl implements SlaveCustomCourseSubjectRepository {
    private DatabaseClient client;
    private SlaveCourseSubjectDto slaveCourseSubjectDto;
    private SlaveSubjectEntity slaveSubjectEntity;

    @Autowired
    public SlaveCustomCourseSubjectRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndex(String name, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                "CASE " +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject \n" +
                "join courses  on courses.uuid = course_subject.course_uuid \n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "where courses.deleted_at is null " +
                "and course_levels.deleted_at is null \n" +
                "and subjects.deleted_at is null " +
                "and course_subject.deleted_at is null " +
                "AND \n" +
                "CASE \n" +
                "   WHEN course_subject.obe" +
                "   THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "   ELSE " +
                "   concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                "END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                "CASE " +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject \n" +
                "join courses  on courses.uuid = course_subject.course_uuid \n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid \n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_subject.status = " + status + "\n" +
                "and courses.deleted_at is null " +
                "and course_levels.deleted_at is null \n" +
                "and subjects.deleted_at is null " +
                "and course_subject.deleted_at is null " +
                "AND \n" +
                "CASE \n" +
                "   WHEN course_subject.obe" +
                "   THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "   ELSE " +
                "   concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                "END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithStatusAndObe(String name, Boolean status, Boolean obe, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                "CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject \n" +
                "join courses  on courses.uuid = course_subject.course_uuid \n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_subject.status = " + status + "\n" +
                "and course_subject.obe = " + obe + "\n" +
                "and courses.deleted_at is null " +
                "and course_levels.deleted_at is null \n" +
                "and subjects.deleted_at is null " +
                "and course_subject.deleted_at is null " +
                "AND \n" +
                "CASE \n" +
                "   WHEN course_subject.obe" +
                "   THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "   ELSE " +
                "   concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                "END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithObe(String name, Boolean obe, String dp, String d, Integer size, Long page) {
        String query = "SELECT course_subject.*, " +
                "CASE " +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "FROM course_subject \n" +
                "join courses  on courses.uuid = course_subject.course_uuid \n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "WHERE course_subject.obe = " + obe + "\n" +
                "AND courses.deleted_at is null " +
                "AND course_levels.deleted_at is null \n" +
                "AND subjects.deleted_at is null " +
                "AND course_subject.deleted_at is null " +
                "AND \n" +
                "CASE \n" +
                "   WHEN course_subject.obe" +
                "   THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "   ELSE " +
                "   concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                "END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourse(String name, UUID courseUUID, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " where course_subject.course_uuid = '" + courseUUID +
                "' and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndStatus(String name, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " where course_subject.course_uuid = '" + courseUUID +
                "' and course_subject.status = " + status +
                " and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndObe(String name, UUID courseUUID, Boolean obe, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " where course_subject.course_uuid = '" + courseUUID +
                "' and course_subject.obe = " + obe +
                " and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndObeAndStatus(String name, UUID courseUUID, Boolean obe, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid \n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " where course_subject.course_uuid = '" + courseUUID +
                "' and course_subject.status = " + status +
                " and course_subject.obe = " + obe +
                " and course_levels.deleted_at is null \n" +
                " and courses.deleted_at is null " +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> showMappedCourseSubjectList(String name, String description, String code, UUID courseUUID, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects\n" +
                "left join course_subject \n" +
                "on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_subject.course_uuid = '" + courseUUID +
                "' and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and (subjects.name ILIKE  '%" + name + "%'\n" +
                "or subjects.description ILIKE  '%" + description + "%'\n" +
                "or subjects.code ILIKE  '%" + code + "%')\n" +
                "order by subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> showMappedCourseSubjectListWithStatus(String name, String description, String code, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select subjects.* from subjects\n" +
                "left join course_subject \n" +
                "on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_subject.course_uuid = '" + courseUUID +
                "' and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and subjects.status = " + status +
                " and (subjects.name ILIKE  '%" + name + "%'\n" +
                "or subjects.description ILIKE  '%" + description + "%'\n" +
                "or subjects.code ILIKE  '%" + code + "%')\n" +
                "order by subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> showUnmappedCourseSubjectList(String name, String description, String code, UUID courseUUID, String dp, String d, Integer size, Long page) {
        String query = "SELECT subjects.* FROM subjects\n" +
                "WHERE subjects.uuid NOT IN(\n" +
                "SELECT subjects.uuid FROM subjects\n" +
                "LEFT JOIN course_subject \n" +
                "ON course_subject.subject_uuid = subjects.uuid \n" +
                "WHERE course_subject.course_uuid = '" + courseUUID +
                "' AND course_subject.deleted_at IS NULL \n" +
                "AND subjects.deleted_at IS NULL)\n" +
                "AND (subjects.name ILIKE  '%" + name + "%'\n" +
                "OR subjects.description ILIKE  '%" + description + "%'\n" +
                "OR subjects.code ILIKE  '%" + code + "%')\n" +
                "AND subjects.deleted_at IS NULL " +
                "ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectEntity> showUnmappedCourseSubjectListWithStatus(String name, String description, String code, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT subjects.* FROM subjects\n" +
                "WHERE subjects.uuid NOT IN(\n" +
                "SELECT subjects.uuid FROM subjects\n" +
                "LEFT JOIN course_subject \n" +
                "ON course_subject.subject_uuid = subjects.uuid \n" +
                "WHERE course_subject.course_uuid = '" + courseUUID +
                "' AND course_subject.deleted_at IS NULL \n" +
                "AND subjects.deleted_at IS NULL)\n" +
                "AND (subjects.name ILIKE  '%" + name + "%'\n" +
                "OR subjects.description ILIKE  '%" + description + "%'\n" +
                "OR subjects.code ILIKE  '%" + code + "%')\n" +
                "AND subjects.deleted_at IS NULL " +
                "AND subjects.status = " + status +
                " ORDER BY subjects." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();

        Flux<SlaveSubjectEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartment(String name, UUID departmentUUID, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join departments  on departments.uuid = courses.department_uuid\n" +
                " where departments.uuid = '" + departmentUUID +
                "' and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and departments.deleted_at is null" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndStatus(String name, UUID departmentUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join departments  on departments.uuid = courses.department_uuid\n" +
                " where departments.uuid = '" + departmentUUID +
                "' and course_subject.status=" + status +
                " and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and departments.deleted_at is null" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndObe(String name, UUID departmentUUID, Boolean obe, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join departments  on departments.uuid = courses.department_uuid\n" +
                " where departments.uuid = '" + departmentUUID +
                "' and course_subject.obe=" + obe +
                " and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and departments.deleted_at is null" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;

    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndObeAndStatus(String name, UUID departmentUUID, Boolean obe, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*, " +
                " CASE " +
                "     WHEN course_subject.obe" +
                "     THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "     ELSE " +
                "     concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                " END as key\n" +
                " from course_subject \n" +
                " join courses  on courses.uuid = course_subject.course_uuid \n" +
                " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                " join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                " join departments  on departments.uuid = courses.department_uuid\n" +
                " where departments.uuid = '" + departmentUUID +
                "' and course_subject.status=" + status +
                " and course_subject.obe=" + obe +
                " and courses.deleted_at is null " +
                " and course_levels.deleted_at is null \n" +
                " and departments.deleted_at is null" +
                " and subjects.deleted_at is null " +
                " and course_subject.deleted_at is null " +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithAcademicSession(String name, UUID academicSessionUUID, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
                "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join subject_offered  on subject_offered.course_subject_uuid = course_subject.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid = academic_sessions.uuid\n" +
                "and course_offered.academic_session_uuid = academic_sessions.uuid\n" +
                "where subject_offered.academic_session_uuid = course_offered.academic_session_uuid\n" +
                "and academic_sessions.uuid = '" + academicSessionUUID +
                "' and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
                "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join subject_offered  on subject_offered.course_subject_uuid = course_subject.uuid\n" +
                "join academic_sessions on subject_offered.academic_session_uuid = academic_sessions.uuid\n" +
                "and course_offered.academic_session_uuid = academic_sessions.uuid\n" +
                "where subject_offered.academic_session_uuid = course_offered.academic_session_uuid\n" +
                "and academic_sessions.uuid = '" + academicSessionUUID +
                "' and courses.deleted_at is null\n" +
                "and course_subject.status = " + status +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and subject_offered.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexOfOfferedCoursesWithAcademicSession(String name, UUID academicSessionUUID, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
                "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_offered.academic_session_uuid = '" + academicSessionUUID +
                "' and courses.deleted_at is null\n" +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexOfOfferedCoursesWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
                "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "where course_offered.academic_session_uuid = '" + academicSessionUUID +
                "' and courses.deleted_at is null\n" +
                "and course_subject.status = " + status +
                " and course_levels.deleted_at is null\n" +
                "and campus_course.deleted_at is null\n" +
                "and course_offered.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + name + "%' " +
                "    ELSE " +
                "    concat(course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + name + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacher(String key, UUID academicSessionUUID, UUID teacherUUID, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
                "where teacher_subjects.academic_session_uuid = '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid = '" + teacherUUID +
                "' and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + key + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + key + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithStatus(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
                "where teacher_subjects.academic_session_uuid = '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid = '" + teacherUUID +
                "' and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and course_subject.status =" + status +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + key + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + key + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithStatusAndOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status, Boolean openLMS, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
                "where teacher_subjects.academic_session_uuid = '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid = '" + teacherUUID +
                "' and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and course_subject.status =" + status +
                " and academic_sessions.is_open =" + openLMS +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + key + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + key + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, String dp, String d, Integer size, Long page) {
        String query = "select course_subject.*,\n" +
                "CASE\n" +
                "WHEN course_subject.obe\n" +
                "THEN concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE')\n" +
                "ELSE\n" +
                "concat(academic_sessions.name,'|',course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE')\n" +
                "END as key\n" +
                "from course_subject\n" +
                "join courses  on courses.uuid = course_subject.course_uuid\n" +
                "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
                "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
                "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
                "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
                "where teacher_subjects.academic_session_uuid = '" + academicSessionUUID +
                "' and teacher_subjects.teacher_uuid = '" + teacherUUID +
                "' and courses.deleted_at is null\n" +
                "and course_levels.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and course_subject.deleted_at is null\n" +
                "and teacher_subjects.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                " and academic_sessions.is_open =" + openLMS +
                " AND \n" +
                " CASE \n" +
                "    WHEN course_subject.obe" +
                "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE '%" + key + "%' " +
                "    ELSE " +
                "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE '%" + key + "%' " +
                " END \n" +
                " ORDER BY course_subject." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCourseSubjectMapper mapper = new SlaveCustomCourseSubjectMapper();

        Flux<SlaveCourseSubjectDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCourseSubjectDto))
                .all();

        return result;
    }
}
