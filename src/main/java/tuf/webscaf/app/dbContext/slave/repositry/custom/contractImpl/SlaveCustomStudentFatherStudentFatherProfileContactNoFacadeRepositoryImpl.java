package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentFatherStudentFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeMapper;


public class SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentFatherStudentFatherProfileContactNoFacadeDto slaveStudentFatherStudentFatherProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentFather.* from std_fathers as studentFather\n" +
                " WHERE studentFather.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentFatherData\n" +
                "left join \n" +
                "(select std_fth_profiles.std_father_uuid as studentFatherUUID,\n" +
                "std_fth_profiles.image as image, std_fth_profiles.name as name, \n" +
                "std_fth_profiles.email as email, std_fth_profiles.nic as nic, std_fth_profiles.age as age, \n" +
                "std_fth_profiles.city_uuid as cityUUID, std_fth_profiles.state_uuid as stateUUID, std_fth_profiles.country_uuid as countryUUID, \n" +
                "std_fth_profiles.no_of_dependents as noOfDependents, std_fth_profiles.official_tel as officialTelephone \n" +
                "from std_fth_profiles \n" +
                "where std_fth_profiles.deleted_at is null) as stdFatherProfileData\n" +
                "on studentFatherData.uuid=stdFatherProfileData.studentFatherUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentFatherData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdFatherProfileData.name ILIKE '%" + name + "%' " +
                " or stdFatherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeMapper mapper = new SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentFatherStudentFatherProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentFather.* from std_fathers as studentFather\n" +
                " WHERE studentFather.deleted_at is null\n" +
                " AND studentFather.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentFatherData\n" +
                "left join \n" +
                "(select std_fth_profiles.std_father_uuid as studentFatherUUID,\n" +
                "std_fth_profiles.image as image, std_fth_profiles.name as name, \n" +
                "std_fth_profiles.email as email, std_fth_profiles.nic as nic, std_fth_profiles.age as age, \n" +
                "std_fth_profiles.city_uuid as cityUUID, std_fth_profiles.state_uuid as stateUUID, std_fth_profiles.country_uuid as countryUUID, \n" +
                "std_fth_profiles.no_of_dependents as noOfDependents, std_fth_profiles.official_tel as officialTelephone \n" +
                "from std_fth_profiles \n" +
                "where std_fth_profiles.deleted_at is null) as stdFatherProfileData\n" +
                "on stdFatherProfileData.studentFatherUUID=studentFatherData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentFatherData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdFatherProfileData.name ILIKE '%" + name + "%' " +
                " or stdFatherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeMapper mapper = new SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeMapper();

        Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentFatherStudentFatherProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
