package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentContactNoMapper;

import java.util.UUID;


public class SlaveCustomStudentContactNoRepositoryImpl implements SlaveCustomStudentContactNoRepository {
    private DatabaseClient client;
    private SlaveStudentContactNoDto slaveStudentContactNoDto;

    @Autowired
    public SlaveCustomStudentContactNoRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentContactNoDto> indexWithoutStatus(UUID studentMetaUUID, String key, String contactNo, String dp, String d, Integer size, Long page) {

        String query = "select student_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) as key \n" +
                " from student_contact_nos \n" +
                " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
                " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
                " where student_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  student_contact_nos.student_meta_uuid= '" + studentMetaUUID +
                "' AND (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or student_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) " +
                " ORDER BY student_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentContactNoMapper mapper = new SlaveCustomStudentContactNoMapper();

        Flux<SlaveStudentContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentContactNoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentContactNoDto> indexWithStatus(UUID studentMetaUUID, Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select student_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) as key\n" +
                " from student_contact_nos \n" +
                " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where student_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  student_contact_nos.student_meta_uuid= '" + studentMetaUUID +
                "' AND (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or student_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) and student_contact_nos.status = " + status +
                " ORDER BY student_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentContactNoMapper mapper = new SlaveCustomStudentContactNoMapper();

        Flux<SlaveStudentContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentContactNoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentContactNoDto> fetchAllRecordsWithStatusFilter(Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select student_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) as key\n" +
                " from student_contact_nos \n" +
                " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where student_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " AND (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or student_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) and student_contact_nos.status = " + status +
                " ORDER BY student_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentContactNoMapper mapper = new SlaveCustomStudentContactNoMapper();

        Flux<SlaveStudentContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentContactNoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveStudentContactNoDto> fetchAllRecordsWithoutStatusFilter(String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select student_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) as key\n" +
                " from student_contact_nos \n" +
                " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where student_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " AND (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or student_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) \n " +
                " ORDER BY student_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomStudentContactNoMapper mapper = new SlaveCustomStudentContactNoMapper();

        Flux<SlaveStudentContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentContactNoDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveStudentContactNoDto> showAllStudentContactNo(UUID studentContactNo) {

        String query = "select student_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) as key \n" +
                " from student_contact_nos \n" +
                " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
                " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
                " where student_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  student_contact_nos.uuid= '" + studentContactNo +
                "'" ;

        SlaveCustomStudentContactNoMapper mapper = new SlaveCustomStudentContactNoMapper();

        Mono<SlaveStudentContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentContactNoDto))
                .one();

        return result;
    }

}
