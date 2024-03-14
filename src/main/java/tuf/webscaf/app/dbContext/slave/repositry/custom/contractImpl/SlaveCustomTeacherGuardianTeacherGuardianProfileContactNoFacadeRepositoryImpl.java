package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeMapper;


public class SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepositoryImpl implements SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherGuardian.* from teacher_guardians as teacherGuardian\n" +
                "left join \n" +
                "(select teacher_grd_profiles.teacher_guardian_uuid as teacherGuardianUUID,\n" +
                "teacher_grd_profiles.image as image, teacher_grd_profiles.name as name, \n" +
                "teacher_grd_profiles.email as email, teacher_grd_profiles.nic as nic, teacher_grd_profiles.age as age, \n" +
                "teacher_grd_profiles.city_uuid as cityUUID, teacher_grd_profiles.state_uuid as stateUUID, teacher_grd_profiles.country_uuid as countryUUID, \n" +
                "teacher_grd_profiles.official_tel as officialTelephone ,teacher_grd_profiles.no_of_dependents as noOfDependent,\n" +
                "teacher_grd_profiles.relation as relation,teacher_grd_profiles.gender_uuid as gender\n" +
                "from teacher_grd_profiles \n" +
                "where teacher_grd_profiles.deleted_at is null) as teacherGuardianProfileData\n" +
                "on teacherGuardian.uuid=teacherGuardianProfileData.teacherGuardianUUID\n" +
                " WHERE teacherGuardian.deleted_at is null\n" +
                " and (teacherGuardianProfileData.name ILIKE '%" + name + "%' " +
                " or teacherGuardianProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherGuardianData\n" +
                " left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherGuardianData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacherGuardian.* from teacher_guardians as teacherGuardian\n" +
                " WHERE teacherGuardian.deleted_at is null\n" +
                " AND teacherGuardian.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherGuardianData\n" +
                "left join \n" +
                "(select teacher_grd_profiles.teacher_guardian_uuid as teacherGuardianUUID,\n" +
                "teacher_grd_profiles.image as image, teacher_grd_profiles.name as name, \n" +
                "teacher_grd_profiles.email as email, teacher_grd_profiles.nic as nic, teacher_grd_profiles.age as age, \n" +
                "teacher_grd_profiles.city_uuid as cityUUID, teacher_grd_profiles.state_uuid as stateUUID, teacher_grd_profiles.country_uuid as countryUUID, \n" +
                "teacher_grd_profiles.official_tel as officialTelephone ,teacher_grd_profiles.no_of_dependents as noOfDependent,\n" +
                "teacher_grd_profiles.relation as relation,teacher_grd_profiles.gender_uuid as gender\n" +
                "from teacher_grd_profiles \n" +
                "where teacher_grd_profiles.deleted_at is null) as teacherGuardianProfileData\n" +
                "on teacherGuardianProfileData.teacherGuardianUUID=teacherGuardianData.uuid\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherGuardianData.uuid = teacherContactNos.teacherMetaUUID)\n" +
                " where (teacherGuardianProfileData.name ILIKE '%" + name + "%' " +
                " or teacherGuardianProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeMapper();

        Flux<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
