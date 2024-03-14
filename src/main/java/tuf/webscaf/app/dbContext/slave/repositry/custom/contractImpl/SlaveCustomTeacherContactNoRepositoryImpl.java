package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentContactNoMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherContactNoMapper;

import java.util.UUID;


public class SlaveCustomTeacherContactNoRepositoryImpl implements SlaveCustomTeacherContactNoRepository {
    private DatabaseClient client;
    private SlaveTeacherContactNoDto slaveTeacherContactNoDto;

    @Autowired
    public SlaveCustomTeacherContactNoRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherContactNoDto> indexWithoutStatus(UUID teacherMetaUUID, String key, String contactNo, String dp, String d, Integer size, Long page) {

        String query = "select teacher_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) as key \n" +
                " from teacher_contact_nos \n" +
                " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
                " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
                " where teacher_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  teacher_contact_nos.teacher_meta_uuid= '" + teacherMetaUUID +
                "' AND ( concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or teacher_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) " +
                " ORDER BY teacher_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherContactNoMapper mapper = new SlaveCustomTeacherContactNoMapper();

        Flux<SlaveTeacherContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherContactNoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherContactNoDto> indexWithStatus(UUID teacherMetaUUID, Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select teacher_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) as key\n" +
                " from teacher_contact_nos \n" +
                " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where teacher_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  teacher_contact_nos.teacher_meta_uuid= '" + teacherMetaUUID +
                "' AND (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or teacher_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) and teacher_contact_nos.status = " + status +
                " ORDER BY teacher_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherContactNoMapper mapper = new SlaveCustomTeacherContactNoMapper();

        Flux<SlaveTeacherContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherContactNoDto))
                .all();

        return result;
    }


    @Override
    public Flux<SlaveTeacherContactNoDto> fetchAllRecordsWithStatusFilter(Boolean status, String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select teacher_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) as key\n" +
                " from teacher_contact_nos \n" +
                " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where teacher_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " AND (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or teacher_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) and teacher_contact_nos.status = " + status +
                " ORDER BY teacher_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherContactNoMapper mapper = new SlaveCustomTeacherContactNoMapper();

        Flux<SlaveTeacherContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherContactNoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherContactNoDto> fetchAllRecordsWithoutStatusFilter(String key, String contactNo, String dp, String d, Integer size, Long page) {
        String query = "select teacher_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) as key\n" +
                " from teacher_contact_nos \n" +
                " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid\n" +
                " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid\n" +
                " where teacher_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " AND (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE '%" + key + "%' \n" +
                " or teacher_contact_nos.contact_no ILIKE '%" + contactNo + "%' \n" +
                " ) \n " +
                " ORDER BY teacher_contact_nos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherContactNoMapper mapper = new SlaveCustomTeacherContactNoMapper();

        Flux<SlaveTeacherContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherContactNoDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveTeacherContactNoDto> showAllTeacherContactNo(UUID teacherContactNo) {

        String query = "select teacher_contact_nos.*, concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) as key \n" +
                " from teacher_contact_nos \n" +
                " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
                " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
                " where teacher_contact_nos.deleted_at IS NULL \n" +
                " and  contact_categories.deleted_at IS NULL \n" +
                " and  contact_types.deleted_at IS NULL \n" +
                " and  teacher_contact_nos.uuid= '" + teacherContactNo +
                "'" ;

        SlaveCustomTeacherContactNoMapper mapper = new SlaveCustomTeacherContactNoMapper();

        Mono<SlaveTeacherContactNoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherContactNoDto))
                .one();

        return result;
    }

}
