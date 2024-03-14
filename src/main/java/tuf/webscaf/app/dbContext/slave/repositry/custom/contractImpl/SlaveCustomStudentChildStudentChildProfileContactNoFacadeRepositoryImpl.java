package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentChildStudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentChildStudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper;


public class SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentChildStudentChildProfileContactNoFacadeDto slaveStudentChildStudentChildProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentChild.* from std_childs as studentChild\n" +
                " WHERE studentChild.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentChildData\n" +
                "left join \n" +
                "(select std_child_profiles.std_child_uuid as studentChildUUID,\n" +
                "std_child_profiles.image as image, std_child_profiles.name as name, \n" +
                "std_child_profiles.email as email, std_child_profiles.nic as nic, std_child_profiles.age as age, \n" +
                "std_child_profiles.city_uuid as cityUUID, std_child_profiles.state_uuid as stateUUID, std_child_profiles.country_uuid as countryUUID, \n" +
                "std_child_profiles.official_tel as officialTelephone \n" +
                "from std_child_profiles \n" +
                "where std_child_profiles.deleted_at is null) as stdChildProfileData\n" +
                "on studentChildData.uuid=stdChildProfileData.studentChildUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentChildData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdChildProfileData.name ILIKE '%" + name + "%' " +
                " or stdChildProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper mapper = new SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentChildStudentChildProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentChild.* from std_childs as studentChild\n" +
                " WHERE studentChild.deleted_at is null\n" +
                " AND studentChild.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentChildData\n" +
                "left join \n" +
                "(select std_child_profiles.std_child_uuid as studentChildUUID,\n" +
                "std_child_profiles.image as image, std_child_profiles.name as name, \n" +
                "std_child_profiles.email as email, std_child_profiles.nic as nic, std_child_profiles.age as age, \n" +
                "std_child_profiles.city_uuid as cityUUID, std_child_profiles.state_uuid as stateUUID, std_child_profiles.country_uuid as countryUUID, \n" +
                "std_child_profiles.official_tel as officialTelephone \n" +
                "from std_child_profiles \n" +
                "where std_child_profiles.deleted_at is null) as stdChildProfileData\n" +
                "on stdChildProfileData.studentChildUUID=studentChildData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentChildData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdChildProfileData.name ILIKE '%" + name + "%' " +
                " or stdChildProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper mapper = new SlaveCustomStudentChildStudentChildProfileContactNoFacadeMapper();

        Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentChildStudentChildProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
