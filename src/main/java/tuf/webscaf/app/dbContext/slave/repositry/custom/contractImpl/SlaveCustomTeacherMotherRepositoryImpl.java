package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherMotherRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherMotherTeacherMotherProfileContactNoFacadeMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherTeacherProfileContactNoFacadeMapper;

import java.util.UUID;


public class SlaveCustomTeacherMotherRepositoryImpl implements SlaveCustomTeacherMotherRepository {
    private DatabaseClient client;
    private SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto slaveTeacherMotherTeacherMotherProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherMotherRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> indexTeacherMotherAndTeacherMotherProfileAndContactNoWithoutStatus(String name, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherMother.* from teacher_mothers as teacherMother\n" +
                " WHERE teacherMother.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherMotherData\n" +
                "left join \n" +
                "(select teacher_mth_profiles.teacher_mother_uuid as teacherMotherUUID,\n" +
                "teacher_mth_profiles.image as image, teacher_mth_profiles.name as name, \n" +
                "teacher_mth_profiles.email as email, teacher_mth_profiles.nic as nic, teacher_mth_profiles.age as age, \n" +
                "teacher_mth_profiles.city_uuid as cityUUID, teacher_mth_profiles.state_uuid as stateUUID, teacher_mth_profiles.country_uuid as countryUUID, \n" +
                "teacher_mth_profiles.no_of_dependents as noOfDependents, teacher_mth_profiles.official_tel as officialTelephone \n" +
                "from teacher_mth_profiles \n" +
                "where teacher_mth_profiles.deleted_at is null) as teacherMotherProfileData\n" +
                "on teacherMotherData.uuid=teacherMotherProfileData.teacherMotherUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherMotherData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherMotherProfileData.name ILIKE '%" + name + "%' " +
                " or teacherMotherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherMotherTeacherMotherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherMotherTeacherMotherProfileContactNoFacadeMapper();

        Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherMotherTeacherMotherProfileContactNoFacadeDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> indexTeacherMotherAndTeacherMotherProfileAndContactNoWithStatus(Boolean status, String name, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherMother.* from teacher_mothers as teacherMother\n" +
                " WHERE teacherMother.deleted_at is null\n" +
                " AND teacherMother.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherMotherData\n" +
                "left join \n" +
                "(select teacher_mth_profiles.teacher_mother_uuid as teacherMotherUUID,\n" +
                "teacher_mth_profiles.image as image, teacher_mth_profiles.name as name, \n" +
                "teacher_mth_profiles.email as email, teacher_mth_profiles.nic as nic, teacher_mth_profiles.age as age, \n" +
                "teacher_mth_profiles.city_uuid as cityUUID, teacher_mth_profiles.state_uuid as stateUUID, teacher_mth_profiles.country_uuid as countryUUID, \n" +
                "teacher_mth_profiles.no_of_dependents as noOfDependents, teacher_mth_profiles.official_tel as officialTelephone \n" +
                "from teacher_mth_profiles \n" +
                "where teacher_mth_profiles.deleted_at is null) as teacherMotherProfileData\n" +
                "on teacherMotherData.uuid=teacherMotherProfileData.teacherMotherUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherMotherData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherMotherProfileData.name ILIKE '%" + name + "%' " +
                " or teacherMotherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherMotherTeacherMotherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherMotherTeacherMotherProfileContactNoFacadeMapper();

        Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherMotherTeacherMotherProfileContactNoFacadeDto))
                .all();

        return result;
    }
}
