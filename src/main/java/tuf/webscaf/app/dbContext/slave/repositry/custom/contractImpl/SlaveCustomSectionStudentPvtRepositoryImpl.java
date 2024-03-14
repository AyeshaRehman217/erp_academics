package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSectionStudentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentMapper;

import java.util.UUID;

public class SlaveCustomSectionStudentPvtRepositoryImpl implements SlaveCustomSectionStudentPvtRepository {
    private DatabaseClient client;
    private SlaveStudentEntity slaveStudentEntity;

    @Autowired
    public SlaveCustomSectionStudentPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentEntity> unMappedStudentList( UUID courseOffered, String studentId, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on campus_course.uuid=registrations.campus_course_uuid\n" +
                "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                "where students.uuid NOT IN (\n" +
                " SELECT students.uuid \n" +
                " from students \n" +
                " join section_student_pvt on students.uuid=section_student_pvt.student_uuid\n" +
                " join sections on sections.uuid=section_student_pvt.section_uuid\n" +
                " join course_offered on sections.course_offered_uuid=course_offered.uuid\n" +
                " where sections.course_offered_uuid= '" + courseOffered +
                "' and course_offered.uuid=sections.course_offered_uuid\n" +
                " and section_student_pvt.deleted_at is null\n" +
                " and students.deleted_at is null\n" +
                " and sections.deleted_at is null\n" +
                " and course_offered.deleted_at is null\n" +
                ")\n" +
                " and course_offered.uuid = '" + courseOffered +
                "' and students.deleted_at is null\n" +
                " and registrations.deleted_at is null\n" +
                " and campus_course.deleted_at is null\n" +
                " and course_offered.deleted_at is null\n" +
                " AND students.student_id ILIKE '%" + studentId + "%'\n" +
                " ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;


        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> unMappedStudentListWithStatus( UUID courseOffered, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* \n" +
                "from students\n" +
                "join registrations on registrations.student_uuid=students.uuid\n" +
                "join campus_course on campus_course.uuid=registrations.campus_course_uuid\n" +
                "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
                "where students.uuid NOT IN (\n" +
                " SELECT students.uuid \n" +
                " from students \n" +
                " join section_student_pvt on students.uuid=section_student_pvt.student_uuid\n" +
                " join sections on sections.uuid=section_student_pvt.section_uuid\n" +
                " join course_offered on sections.course_offered_uuid=course_offered.uuid\n" +
                " where  sections.course_offered_uuid= '" + courseOffered +
                "' and course_offered.uuid=sections.course_offered_uuid\n" +
                " and section_student_pvt.deleted_at is null\n" +
                " and students.deleted_at is null\n" +
                " and sections.deleted_at is null\n" +
                " and course_offered.deleted_at is null\n" +
                ") \n" +
                " and course_offered.uuid = '" + courseOffered +
                "' and students.deleted_at is null\n" +
                " and registrations.deleted_at is null\n" +
                " and students.status = " + status +
                " and campus_course.deleted_at is null\n" +
                " and course_offered.deleted_at is null\n" +
                " AND students.student_id ILIKE '%" + studentId + "%'\n" +
                " ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> showMappedStudentListAgainstSection(UUID sectionUUID, UUID courseOffered, String studentId, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* FROM students\n" +
                "JOIN section_student_pvt ON section_student_pvt.student_uuid = students.uuid\n" +
                "JOIN sections ON sections.uuid = section_student_pvt.section_uuid\n" +
                "JOIN course_offered ON course_offered.uuid = sections.course_offered_uuid\n" +
                "WHERE section_student_pvt.section_uuid = '" + sectionUUID +
                "' and course_offered_uuid='" + courseOffered +
                "' AND section_student_pvt.deleted_at IS NULL\n" +
                " AND students.deleted_at IS NULL\n" +
                "AND sections.deleted_at IS NULL\n" +
                "AND course_offered.deleted_at IS NULL\n" +
                "AND students.student_id ILIKE '%" + studentId + "%'\n" +
                "order by students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> showMappedStudentListAgainstSectionWithStatus(UUID sectionUUID, UUID courseOffered, String studentId, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* FROM students\n" +
                "JOIN section_student_pvt ON section_student_pvt.student_uuid = students.uuid\n" +
                "JOIN sections ON sections.uuid = section_student_pvt.section_uuid\n" +
                "JOIN course_offered ON course_offered.uuid = sections.course_offered_uuid\n" +
                "WHERE section_student_pvt.section_uuid = '" + sectionUUID +
                "' and course_offered_uuid='" + courseOffered +
                "' AND section_student_pvt.deleted_at IS NULL\n" +
                " AND students.deleted_at IS NULL\n" +
                "AND sections.deleted_at IS NULL\n" +
                "AND course_offered.deleted_at IS NULL\n" +
                " and students.status = " + status +
                " AND students.student_id ILIKE '%" + studentId + "%'\n" +
                "order by students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }
}
