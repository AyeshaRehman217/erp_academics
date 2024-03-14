package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper;


public class SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepositoryImpl implements SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto slaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherFather.* from teacher_fathers as teacherFather\n" +
                " WHERE teacherFather.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherFatherData\n" +
                "left join \n" +
                "(select teacher_fth_profiles.teacher_father_uuid as teacherFatherUUID,\n" +
                "teacher_fth_profiles.image as image, teacher_fth_profiles.name as name, \n" +
                "teacher_fth_profiles.email as email, teacher_fth_profiles.nic as nic, teacher_fth_profiles.age as age, \n" +
                "teacher_fth_profiles.city_uuid as cityUUID, teacher_fth_profiles.state_uuid as stateUUID, teacher_fth_profiles.country_uuid as countryUUID, \n" +
                "teacher_fth_profiles.no_of_dependents as noOfDependents, teacher_fth_profiles.official_tel as officialTelephone \n" +
                "from teacher_fth_profiles \n" +
                "where teacher_fth_profiles.deleted_at is null) as teacherFatherProfileData\n" +
                "on teacherFatherData.uuid=teacherFatherProfileData.teacherFatherUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherFatherData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherFatherProfileData.name ILIKE '%" + name + "%' " +
                " or teacherFatherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherFatherTeacherFatherProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherFather.* from teacher_fathers as teacherFather\n" +
                " WHERE teacherFather.deleted_at is null\n" +
                " AND teacherFather.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherFatherData\n" +
                "left join \n" +
                "(select teacher_fth_profiles.teacher_father_uuid as teacherFatherUUID,\n" +
                "teacher_fth_profiles.image as image, teacher_fth_profiles.name as name, \n" +
                "teacher_fth_profiles.email as email, teacher_fth_profiles.nic as nic, teacher_fth_profiles.age as age, \n" +
                "teacher_fth_profiles.city_uuid as cityUUID, teacher_fth_profiles.state_uuid as stateUUID, teacher_fth_profiles.country_uuid as countryUUID, \n" +
                "teacher_fth_profiles.no_of_dependents as noOfDependents, teacher_fth_profiles.official_tel as officialTelephone \n" +
                "from teacher_fth_profiles \n" +
                "where teacher_fth_profiles.deleted_at is null) as teacherFatherProfileData\n" +
                "on teacherFatherProfileData.teacherFatherUUID=teacherFatherData.uuid\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherFatherData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherFatherProfileData.name ILIKE '%" + name + "%' " +
                " or teacherFatherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper();

        Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherFatherTeacherFatherProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
