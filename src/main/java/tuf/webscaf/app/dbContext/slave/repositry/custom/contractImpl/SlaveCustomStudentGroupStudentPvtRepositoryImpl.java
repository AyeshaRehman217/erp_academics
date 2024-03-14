package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentGroupStudentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentMapper;

import java.util.UUID;

public class SlaveCustomStudentGroupStudentPvtRepositoryImpl implements SlaveCustomStudentGroupStudentPvtRepository {
    private DatabaseClient client;
    private SlaveStudentEntity slaveStudentEntity;

    @Autowired
    public SlaveCustomStudentGroupStudentPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentEntity> showUnMappedStudentGroupStudentsList(UUID studentGroupUUID, String studentId, String officialEmail, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* FROM students\n" +
                "WHERE students.uuid NOT IN(\n" +
                "SELECT students.uuid FROM students\n" +
                "LEFT JOIN student_group_students_pvt \n" +
                "ON student_group_students_pvt.student_uuid = students.uuid \n" +
                "WHERE student_group_students_pvt.student_group_uuid = '" + studentGroupUUID +
                "' AND student_group_students_pvt.deleted_at IS NULL \n" +
                "AND students.deleted_at IS NULL)\n" +
                "AND (students.student_id ILIKE '%" + studentId + "%'" +
                " or students.official_email ILIKE '%" + officialEmail + "%')" +
                "AND students.deleted_at IS NULL " +
                "ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> showUnMappedStudentGroupStudentsListWithStatus(UUID studentGroupUUID, String studentId, String officialEmail, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT students.* FROM students\n" +
                "WHERE students.uuid NOT IN(\n" +
                "SELECT students.uuid FROM students\n" +
                "LEFT JOIN student_group_students_pvt \n" +
                "ON student_group_students_pvt.student_uuid = students.uuid \n" +
                "WHERE student_group_students_pvt.student_group_uuid = '" + studentGroupUUID +
                "' AND student_group_students_pvt.deleted_at IS NULL \n" +
                "AND students.deleted_at IS NULL)\n" +
                "AND (students.student_id ILIKE '%" + studentId + "%' " +
                "or students.official_email ILIKE '%" + officialEmail + "%')" +
                "AND students.deleted_at IS NULL " +
                "AND students.status = " + status +
                " ORDER BY students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> showMappedStudentGroupStudentsList(UUID studentGroupUUID, String studentId, String officialEmail, String dp, String d, Integer size, Long page) {
        String query = "select students.* from students\n" +
                "left join student_group_students_pvt \n" +
                "on students.uuid = student_group_students_pvt.student_uuid\n" +
                "where student_group_students_pvt.student_group_uuid = '" + studentGroupUUID +
                "' and students.deleted_at is null\n" +
                "and student_group_students_pvt.deleted_at is null\n" +
                " and (students.student_id ILIKE  '%" + studentId + "%' " +
                "or students.official_email ILIKE  '%" + officialEmail + "%') " +
                "order by students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentEntity> showMappedStudentGroupStudentsListWithStatus(UUID studentGroupUUID, Boolean status, String studentId, String officialEmail, String dp, String d, Integer size, Long page) {
        String query = "select students.* from students\n" +
                "left join student_group_students_pvt \n" +
                "on students.uuid = student_group_students_pvt.student_uuid\n" +
                "where student_group_students_pvt.student_group_uuid = '" + studentGroupUUID +
                "' and students.deleted_at is null\n" +
                "and student_group_students_pvt.deleted_at is null\n" +
                "and students.status = " + status +
                " and (students.student_id ILIKE  '%" + studentId + "%' " +
                "or students.official_email ILIKE  '%" + officialEmail + "%') " +
                "order by students." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentMapper mapper = new SlaveCustomStudentMapper();

        Flux<SlaveStudentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentEntity))
                .all();

        return result;
    }
}
