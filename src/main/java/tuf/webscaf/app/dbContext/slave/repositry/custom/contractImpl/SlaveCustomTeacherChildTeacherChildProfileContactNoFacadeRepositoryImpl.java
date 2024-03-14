package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherChildTeacherChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeMapper;


public class SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepositoryImpl implements SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveTeacherChildTeacherChildProfileContactNoFacadeDto slaveTeacherChildTeacherChildProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherChild.* from std_childs as teacherChild\n" +
                " WHERE teacherChild.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherChildData\n" +
                "left join \n" +
                "(select std_child_profiles.std_child_uuid as teacherChildUUID,\n" +
                "std_child_profiles.image as image, std_child_profiles.name as name, \n" +
                "std_child_profiles.email as email, std_child_profiles.nic as nic, std_child_profiles.age as age, \n" +
                "std_child_profiles.city_uuid as cityUUID, std_child_profiles.state_uuid as stateUUID, std_child_profiles.country_uuid as countryUUID, \n" +
                "std_child_profiles.official_tel as officialTelephone \n" +
                "from std_child_profiles \n" +
                "where std_child_profiles.deleted_at is null) as stdChildProfileData\n" +
                "on teacherChildData.uuid=stdChildProfileData.teacherChildUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as stdMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on teacherChildData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdChildProfileData.name ILIKE '%" + name + "%' " +
                " or stdChildProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherChildTeacherChildProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherChild.* from std_childs as teacherChild\n" +
                " WHERE teacherChild.deleted_at is null\n" +
                " AND teacherChild.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherChildData\n" +
                "left join \n" +
                "(select std_child_profiles.std_child_uuid as teacherChildUUID,\n" +
                "std_child_profiles.image as image, std_child_profiles.name as name, \n" +
                "std_child_profiles.email as email, std_child_profiles.nic as nic, std_child_profiles.age as age, \n" +
                "std_child_profiles.city_uuid as cityUUID, std_child_profiles.state_uuid as stateUUID, std_child_profiles.country_uuid as countryUUID, \n" +
                "std_child_profiles.official_tel as officialTelephone \n" +
                "from std_child_profiles \n" +
                "where std_child_profiles.deleted_at is null) as stdChildProfileData\n" +
                "on stdChildProfileData.teacherChildUUID=teacherChildData.uuid\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as stdMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on teacherChildData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdChildProfileData.name ILIKE '%" + name + "%' " +
                " or stdChildProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeMapper();

        Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherChildTeacherChildProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
