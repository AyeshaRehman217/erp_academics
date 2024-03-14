package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeMapper;


public class SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto slaveStudentSiblingStudentSiblingProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentSibling.* from std_siblings as studentSibling\n" +
                " WHERE studentSibling.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentSiblingData\n" +
                "left join \n" +
                "(select std_sibling_profiles.std_sibling_uuid as studentSiblingUUID,\n" +
                "std_sibling_profiles.image as image, std_sibling_profiles.name as name, \n" +
                "std_sibling_profiles.email as email, std_sibling_profiles.nic as nic, std_sibling_profiles.age as age, \n" +
                "std_sibling_profiles.city_uuid as cityUUID, std_sibling_profiles.state_uuid as stateUUID, std_sibling_profiles.country_uuid as countryUUID, \n" +
                "std_sibling_profiles.official_tel as officialTelephone \n" +
                "from std_sibling_profiles \n" +
                "where std_sibling_profiles.deleted_at is null) as stdSiblingProfileData\n" +
                "on studentSiblingData.uuid=stdSiblingProfileData.studentSiblingUUID\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentSiblingData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdSiblingProfileData.name ILIKE '%" + name + "%' " +
                " or stdSiblingProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeMapper mapper = new SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentSiblingStudentSiblingProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select studentSibling.* from std_siblings as studentSibling\n" +
                " WHERE studentSibling.deleted_at is null\n" +
                " AND studentSibling.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentSiblingData\n" +
                "left join \n" +
                "(select std_sibling_profiles.std_sibling_uuid as studentSiblingUUID,\n" +
                "std_sibling_profiles.image as image, std_sibling_profiles.name as name, \n" +
                "std_sibling_profiles.email as email, std_sibling_profiles.nic as nic, std_sibling_profiles.age as age, \n" +
                "std_sibling_profiles.city_uuid as cityUUID, std_sibling_profiles.state_uuid as stateUUID, std_sibling_profiles.country_uuid as countryUUID, \n" +
                "std_sibling_profiles.official_tel as officialTelephone \n" +
                "from std_sibling_profiles \n" +
                "where std_sibling_profiles.deleted_at is null) as stdSiblingProfileData\n" +
                "on stdSiblingProfileData.studentSiblingUUID=studentSiblingData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentSiblingData.uuid = stdContactNos.stdMetaUUID)\n" +
                " where (stdSiblingProfileData.name ILIKE '%" + name + "%' " +
                " or stdSiblingProfileData.nic ILIKE '%" + nic + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeMapper mapper = new SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeMapper();

        Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentSiblingStudentSiblingProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
