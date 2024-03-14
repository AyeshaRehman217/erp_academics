package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeMapper;


public class SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepositoryImpl implements SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherSibling.* from teacher_siblings as teacherSibling\n" +
                " WHERE teacherSibling.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherSiblingData\n" +
                "left join \n" +
                "(select teacher_sibling_profiles.teacher_sibling_uuid as teacherSiblingUUID,\n" +
                "teacher_sibling_profiles.image as image, teacher_sibling_profiles.name as name, \n" +
                "teacher_sibling_profiles.email as email, teacher_sibling_profiles.nic as nic, teacher_sibling_profiles.age as age, \n" +
                "teacher_sibling_profiles.city_uuid as cityUUID, teacher_sibling_profiles.state_uuid as stateUUID, teacher_sibling_profiles.country_uuid as countryUUID, \n" +
                "teacher_sibling_profiles.official_tel as officialTelephone \n" +
                "from teacher_sibling_profiles \n" +
                "where teacher_sibling_profiles.deleted_at is null) as teacherSiblingProfileData\n" +
                "on teacherSiblingData.uuid=teacherSiblingProfileData.teacherSiblingUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherSiblingData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherSiblingProfileData.name ILIKE '%" + name + "%' " +
                " or teacherSiblingProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherSibling.* from teacher_siblings as teacherSibling\n" +
                " WHERE teacherSibling.deleted_at is null\n" +
                " AND teacherSibling.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherSiblingData\n" +
                "left join \n" +
                "(select teacher_sibling_profiles.teacher_sibling_uuid as teacherSiblingUUID,\n" +
                "teacher_sibling_profiles.image as image, teacher_sibling_profiles.name as name, \n" +
                "teacher_sibling_profiles.email as email, teacher_sibling_profiles.nic as nic, teacher_sibling_profiles.age as age, \n" +
                "teacher_sibling_profiles.city_uuid as cityUUID, teacher_sibling_profiles.state_uuid as stateUUID, teacher_sibling_profiles.country_uuid as countryUUID, \n" +
                "teacher_sibling_profiles.official_tel as officialTelephone \n" +
                "from teacher_sibling_profiles \n" +
                "where teacher_sibling_profiles.deleted_at is null) as teacherSiblingProfileData\n" +
                "on teacherSiblingProfileData.teacherSiblingUUID=teacherSiblingData.uuid\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherSiblingData.uuid = teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherSiblingProfileData.name ILIKE '%" + name + "%' " +
                " or teacherSiblingProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeMapper();

        Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
