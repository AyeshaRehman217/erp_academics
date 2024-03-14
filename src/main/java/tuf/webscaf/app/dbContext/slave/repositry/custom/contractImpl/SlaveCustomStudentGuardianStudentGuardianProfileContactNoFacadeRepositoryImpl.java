package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper;


public class SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto slaveStudentGuardianStudentGuardianProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentGuardian.* from std_guardians as studentGuardian\n" +
                " WHERE studentGuardian.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentGuardianData\n" +
                "left join \n" +
                "(select std_grd_profiles.std_guardian_uuid as studentGuardianUUID,\n" +
                "std_grd_profiles.image as image, std_grd_profiles.name as name, \n" +
                "std_grd_profiles.email as email, std_grd_profiles.nic as nic, std_grd_profiles.age as age, \n" +
                "std_grd_profiles.city_uuid as cityUUID, std_grd_profiles.state_uuid as stateUUID, std_grd_profiles.country_uuid as countryUUID, \n" +
                "std_grd_profiles.official_tel as officialTelephone ,std_grd_profiles.no_of_dependents as noOfDependent,\n" +
                "std_grd_profiles.gender_uuid as gender\n" +
                "from std_grd_profiles \n" +
                "where std_grd_profiles.deleted_at is null) as studentGuardianProfileData\n" +
                "on studentGuardianData.uuid=studentGuardianProfileData.studentGuardianUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as studentMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as studentContactNos \n" +
                " on studentGuardianData.uuid=studentContactNos.studentMetaUUID)\n" +
                " where (studentGuardianProfileData.name ILIKE '%" + name + "%' " +
                " or studentGuardianProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper mapper = new SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentGuardianStudentGuardianProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentGuardian.* from std_guardians as studentGuardian\n" +
                " WHERE studentGuardian.deleted_at is null\n" +
                " AND studentGuardian.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentGuardianData\n" +
                "left join \n" +
                "(select std_grd_profiles.std_guardian_uuid as studentGuardianUUID,\n" +
                "std_grd_profiles.image as image, std_grd_profiles.name as name, \n" +
                "std_grd_profiles.email as email, std_grd_profiles.nic as nic, std_grd_profiles.age as age, \n" +
                "std_grd_profiles.city_uuid as cityUUID, std_grd_profiles.state_uuid as stateUUID, std_grd_profiles.country_uuid as countryUUID, \n" +
                "std_grd_profiles.official_tel as officialTelephone ,std_grd_profiles.no_of_dependents as noOfDependent,\n" +
                "std_grd_profiles.gender_uuid as gender\n" +
                "from std_grd_profiles \n" +
                "where std_grd_profiles.deleted_at is null) as studentGuardianProfileData\n" +
                "on studentGuardianProfileData.studentGuardianUUID=studentGuardianData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as studentMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as studentContactNos \n" +
                " on studentGuardianData.uuid = studentContactNos.studentMetaUUID)\n" +
                " where (studentGuardianProfileData.name ILIKE '%" + name + "%' " +
                " or studentGuardianProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper mapper = new SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper();

        Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentGuardianStudentGuardianProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
