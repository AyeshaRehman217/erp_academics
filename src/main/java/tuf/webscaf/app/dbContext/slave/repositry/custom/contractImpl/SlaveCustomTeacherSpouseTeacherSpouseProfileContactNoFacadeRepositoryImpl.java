package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeMapper;


public class SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepositoryImpl implements SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherSpouse.* from teacher_spouses as teacherSpouse\n" +
                " WHERE teacherSpouse.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherSpouseData\n" +
                "left join \n" +
                "(select teacher_spouse_profiles.teacher_spouse_uuid as teacherSpouseUUID,\n" +
                "teacher_spouse_profiles.image as image, teacher_spouse_profiles.name as name, \n" +
                "teacher_spouse_profiles.email as email, teacher_spouse_profiles.nic as nic, teacher_spouse_profiles.age as age, \n" +
                "teacher_spouse_profiles.city_uuid as cityUUID, teacher_spouse_profiles.state_uuid as stateUUID, teacher_spouse_profiles.country_uuid as countryUUID, \n" +
                "teacher_spouse_profiles.official_tel as officialTelephone ,teacher_spouse_profiles.no_of_dependents as noOfDependent\n" +
                "from teacher_spouse_profiles \n" +
                "where teacher_spouse_profiles.deleted_at is null) as teacherSpouseProfileData\n" +
                "on teacherSpouseData.uuid=teacherSpouseProfileData.teacherSpouseUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherSpouseData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherSpouseProfileData.name ILIKE '%" + name + "%' " +
                " or teacherSpouseProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherSpouse.* from teacher_spouses as teacherSpouse\n" +
                " WHERE teacherSpouse.deleted_at is null\n" +
                " AND teacherSpouse.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherSpouseData\n" +
                "left join \n" +
                "(select teacher_spouse_profiles.teacher_spouse_uuid as teacherSpouseUUID,\n" +
                "teacher_spouse_profiles.image as image, teacher_spouse_profiles.name as name, \n" +
                "teacher_spouse_profiles.email as email, teacher_spouse_profiles.nic as nic, teacher_spouse_profiles.age as age, \n" +
                "teacher_spouse_profiles.city_uuid as cityUUID, teacher_spouse_profiles.state_uuid as stateUUID, teacher_spouse_profiles.country_uuid as countryUUID, \n" +
                "teacher_spouse_profiles.official_tel as officialTelephone ,teacher_spouse_profiles.no_of_dependents as noOfDependent\n" +
                "from teacher_spouse_profiles \n" +
                "where teacher_spouse_profiles.deleted_at is null) as teacherSpouseProfileData\n" +
                "on teacherSpouseProfileData.teacherSpouseUUID=teacherSpouseData.uuid\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherSpouseData.uuid = teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherSpouseProfileData.name ILIKE '%" + name + "%' " +
                " or teacherSpouseProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeMapper();

        Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
