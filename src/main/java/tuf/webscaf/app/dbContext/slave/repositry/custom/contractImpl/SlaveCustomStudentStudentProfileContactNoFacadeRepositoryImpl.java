package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentStudentProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentStudentProfileContactNoFacadeMapper;


public class SlaveCustomStudentStudentProfileContactNoFacadeRepositoryImpl implements SlaveCustomStudentStudentProfileContactNoFacadeRepository {
    private DatabaseClient client;
    private SlaveStudentStudentProfileContactNoFacadeDto slaveStudentStudentProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomStudentStudentProfileContactNoFacadeRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveStudentStudentProfileContactNoFacadeDto> indexWithoutStatus(String firstname, String lastname, String studentId, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select student.* from students as student\n" +
                " WHERE student.deleted_at is null\n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentData\n" +
                "left join \n" +
                "(select std_profiles.student_uuid as studentUUID,  std_profiles.description as description, \n" +
                "std_profiles.image as image, std_profiles.first_name as firstName, std_profiles.last_name as lastName, \n" +
                "std_profiles.email as email, std_profiles.tel as telephoneNo, std_profiles.nic as nic, std_profiles.birth_date as birthDate, \n" +
                "std_profiles.city_uuid as cityUUID, std_profiles.state_uuid as stateUUID, std_profiles.country_uuid as countryUUID, \n" +
                "std_profiles.religion_uuid as religionUUID, std_profiles.sect_uuid as sectUUID, std_profiles.caste_uuid as casteUUID, \n" +
                "std_profiles.gender_uuid as genderUUID, std_profiles.marital_status_uuid as maritalStatusUUID \n" +
                "from std_profiles \n" +
                "where std_profiles.deleted_at is null) as stdProfileData\n" +
                "on stdProfileData.studentUUID=studentData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdProfileData.firstName ILIKE '%" + firstname + "%' " +
                " or stdProfileData.lastName ILIKE '%" + lastname + "%'" +
                " or stdProfileData.nic ILIKE '%" + nic + "%'" +
                " or studentData.student_id ILIKE '%" + studentId + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentStudentProfileContactNoFacadeMapper mapper = new SlaveCustomStudentStudentProfileContactNoFacadeMapper();

        return client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentStudentProfileContactNoFacadeDto))
                .all();
    }

    @Override
    public Flux<SlaveStudentStudentProfileContactNoFacadeDto> indexWithStatus(String firstname, String lastname, String studentId, String nic, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select student.* from students as student\n" +
                " WHERE student.deleted_at is null\n" +
                " AND student.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as studentData\n" +
                "left join \n" +
                "(select std_profiles.student_uuid as studentUUID,  std_profiles.description as description, \n" +
                "std_profiles.image as image,   std_profiles.first_name as firstName, std_profiles.last_name as lastName, \n" +
                "std_profiles.email as email,  std_profiles.tel as telephoneNo, std_profiles.nic as nic, std_profiles.birth_date as birthDate, \n" +
                "std_profiles.city_uuid as cityUUID, std_profiles.state_uuid as stateUUID, std_profiles.country_uuid as countryUUID, \n" +
                "std_profiles.religion_uuid as religionUUID, std_profiles.sect_uuid as sectUUID, std_profiles.caste_uuid as casteUUID, \n" +
                "std_profiles.gender_uuid as genderUUID, std_profiles.marital_status_uuid as maritalStatusUUID \n" +
                "from std_profiles \n" +
                "where std_profiles.deleted_at is null) as stdProfileData\n" +
                "on stdProfileData.studentUUID=studentData.uuid\n" +
                "left join \n" +
                "(select student_contact_nos.contact_type_uuid as contactTypeUUID, student_contact_nos.contact_no as contactNo,  \n" +
                " student_contact_nos.student_meta_uuid as stdMetaUUID\n" +
                " from student_contact_nos \n" +
                " where student_contact_nos.deleted_at is null) as stdContactNos \n" +
                " on studentData.uuid=stdContactNos.stdMetaUUID)\n" +
                " where (stdProfileData.firstName ILIKE '%" + firstname + "%' " +
                " or stdProfileData.lastName ILIKE '%" + lastname + "%'" +
                " or stdProfileData.nic ILIKE '%" + nic + "%'" +
                " or studentData.student_id ILIKE '%" + studentId + "%')" +
                " order by " + dp + " " + d;

        SlaveCustomStudentStudentProfileContactNoFacadeMapper mapper = new SlaveCustomStudentStudentProfileContactNoFacadeMapper();

        Flux<SlaveStudentStudentProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveStudentStudentProfileContactNoFacadeDto))
                .all();

        return result;
    }

}
