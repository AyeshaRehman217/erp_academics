package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentStudentProfileContactNoFacadeMapper;


public class SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentMotherStudentMotherProfileContactNoFacadeDto slaveStudentMotherStudentMotherProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentMother.* from std_mothers as studentMother\n" +
                " WHERE studentMother.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentMotherData\n" +
                "left join \n" +
                "(select std_mth_profiles.std_mother_uuid as studentMotherUUID,\n" +
                "std_mth_profiles.image as image, std_mth_profiles.name as name, \n" +
                "std_mth_profiles.email as email, std_mth_profiles.nic as nic, std_mth_profiles.age as age, \n" +
                "std_mth_profiles.city_uuid as cityUUID, std_mth_profiles.state_uuid as stateUUID, std_mth_profiles.country_uuid as countryUUID, \n" +
                "std_mth_profiles.no_of_dependents as noOfDependents, std_mth_profiles.official_tel as officialTelephone \n" +
                "from std_mth_profiles \n" +
                "where std_mth_profiles.deleted_at is null) as stdMotherProfileData\n" +
                "on studentMotherData.uuid=stdMotherProfileData.studentMotherUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentMotherData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdMotherProfileData.name ILIKE '%" + name + "%' " +
                " or stdMotherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeMapper mapper = new SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentMotherStudentMotherProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentMother.* from std_mothers as studentMother\n" +
                " WHERE studentMother.deleted_at is null\n" +
                " AND studentMother.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentMotherData\n" +
                "left join \n" +
                "(select std_mth_profiles.std_mother_uuid as studentMotherUUID,\n" +
                "std_mth_profiles.image as image, std_mth_profiles.name as name, \n" +
                "std_mth_profiles.email as email, std_mth_profiles.nic as nic, std_mth_profiles.age as age, \n" +
                "std_mth_profiles.city_uuid as cityUUID, std_mth_profiles.state_uuid as stateUUID, std_mth_profiles.country_uuid as countryUUID, \n" +
                "std_mth_profiles.no_of_dependents as noOfDependents, std_mth_profiles.official_tel as officialTelephone \n" +
                "from std_mth_profiles \n" +
                "where std_mth_profiles.deleted_at is null) as stdMotherProfileData\n" +
                "on stdMotherProfileData.studentMotherUUID=studentMotherData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentMotherData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdMotherProfileData.name ILIKE '%" + name + "%' " +
                " or stdMotherProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeMapper mapper = new SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeMapper();

        Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentMotherStudentMotherProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
