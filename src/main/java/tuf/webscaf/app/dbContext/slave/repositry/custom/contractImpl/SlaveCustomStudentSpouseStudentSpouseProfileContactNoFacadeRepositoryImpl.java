package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeMapper;


public class SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto slaveStudentSpouseStudentSpouseProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentSpouse.* from std_spouses as studentSpouse\n" +
                " WHERE studentSpouse.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentSpouseData\n" +
                "left join \n" +
                "(select std_spouse_profiles.std_spouse_uuid as studentSpouseUUID,\n" +
                "std_spouse_profiles.image as image, std_spouse_profiles.name as name, \n" +
                "std_spouse_profiles.email as email, std_spouse_profiles.nic as nic, std_spouse_profiles.age as age, \n" +
                "std_spouse_profiles.city_uuid as cityUUID, std_spouse_profiles.state_uuid as stateUUID, std_spouse_profiles.country_uuid as countryUUID, \n" +
                "std_spouse_profiles.official_tel as officialTelephone ,std_spouse_profiles.no_of_dependents as noOfDependent\n" +
                "from std_spouse_profiles \n" +
                "where std_spouse_profiles.deleted_at is null) as stdSpouseProfileData\n" +
                "on studentSpouseData.uuid=stdSpouseProfileData.studentSpouseUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentSpouseData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdSpouseProfileData.name ILIKE '%" + name + "%' " +
                " or stdSpouseProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeMapper mapper = new SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentSpouseStudentSpouseProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentSpouse.* from std_spouses as studentSpouse\n" +
                " WHERE studentSpouse.deleted_at is null\n" +
                " AND studentSpouse.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentSpouseData\n" +
                "left join \n" +
                "(select std_spouse_profiles.std_spouse_uuid as studentSpouseUUID,\n" +
                "std_spouse_profiles.image as image, std_spouse_profiles.name as name, \n" +
                "std_spouse_profiles.email as email, std_spouse_profiles.nic as nic, std_spouse_profiles.age as age, \n" +
                "std_spouse_profiles.city_uuid as cityUUID, std_spouse_profiles.state_uuid as stateUUID, std_spouse_profiles.country_uuid as countryUUID, \n" +
                "std_spouse_profiles.official_tel as officialTelephone ,std_spouse_profiles.no_of_dependents as noOfDependent\n" +
                "from std_spouse_profiles \n" +
                "where std_spouse_profiles.deleted_at is null) as stdSpouseProfileData\n" +
                "on stdSpouseProfileData.studentSpouseUUID=studentSpouseData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentSpouseData.uuid = stdContactNos.stdMetaUUID)\n" +
                " where (stdSpouseProfileData.name ILIKE '%" + name + "%' " +
                " or stdSpouseProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeMapper mapper = new SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeMapper();

        Flux<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentSpouseStudentSpouseProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
