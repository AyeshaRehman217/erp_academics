package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherTeacherProfileContactNoFacadeMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherMapper;

import java.util.UUID;


public class SlaveCustomTeacherRepositoryImpl implements SlaveCustomTeacherRepository {
    private DatabaseClient client;
    private SlaveTeacherDto slaveTeacherDto;
    private SlaveTeacherTeacherProfileContactNoFacadeDto slaveTeacherTeacherProfileContactNoFacadeDto;

    @Autowired
    public SlaveCustomTeacherRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTeacherDto> indexWithoutStatus(String key, String employeeCode, String dp, String d, Integer size, Long page) {
        String query = "select teachers.*, concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) as key\n" +
                " from teachers \n" +
                "  left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
                "  where teachers.deleted_at IS NULL\n" +
                "  AND teacher_profiles.deleted_at IS NULL\n" +
                "  AND (teachers.employee_code ILIKE '%" + employeeCode + "%' \n" +
                "  or concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) ILIKE '%" + key + "%') " +
                " ORDER BY teachers." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherMapper mapper = new SlaveCustomTeacherMapper();

        Flux<SlaveTeacherDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherDto> indexWithStatus(Boolean status, String key, String employeeCode, String dp, String d, Integer size, Long page) {
        String query = "select teachers.*, concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) as key\n" +
                "  from teachers \n" +
                "  left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
                "  where teachers.deleted_at IS NULL\n" +
                "  and  teachers.status = " + status +
                "  AND teacher_profiles.deleted_at IS NULL\n" +
                "  AND (teachers.employee_code ILIKE '%" + employeeCode + "%' \n" +
                "  or concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) ILIKE '%" + key + "%') " +
                " ORDER BY teachers." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTeacherMapper mapper = new SlaveCustomTeacherMapper();

        Flux<SlaveTeacherDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> indexTeacherAndTeacherProfileAndContactNoWithoutStatus(String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacher.* from teachers as teacher \n" +
                " WHERE teacher.deleted_at is null \n" +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherData\n" +
                "left join \n" +
                "(select teacher_profiles.teacher_uuid as teacherUUID, \n" +
                "teacher_profiles.image as image,   teacher_profiles.first_name as firstName, teacher_profiles.last_name as lastName, \n" +
                "teacher_profiles.email as email,  teacher_profiles.tel as telephoneNo, teacher_profiles.nic as nic, teacher_profiles.birth_date as birthDate, \n" +
                "teacher_profiles.city_uuid as cityUUID, teacher_profiles.state_uuid as stateUUID, teacher_profiles.country_uuid as countryUUID, \n" +
                "teacher_profiles.religion_uuid as religionUUID, teacher_profiles.sect_uuid as sectUUID, teacher_profiles.caste_uuid as casteUUID, \n" +
                "teacher_profiles.gender_uuid as genderUUID, teacher_profiles.marital_status_uuid as maritalStatusUUID \n" +
                "from teacher_profiles \n" +
                "where teacher_profiles.deleted_at is null) as teacherProfileData\n" +
                "on teacherData.uuid=teacherProfileData.teacherUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherData.uuid=teacherContactNos.teacherMetaUUID)" +
                "  AND (teacherData.employee_code ILIKE '%" + employeeCode + "%' OR teacherProfileData.firstName ILIKE '%" + firstName + "%' " +
                " OR teacherProfileData.lastName ILIKE '%" + lastName + "%' OR teacherProfileData.email ILIKE '%" + email + "%'" +
                " OR teacherProfileData.telephoneNo ILIKE '%" + telephoneNo + "%' OR teacherProfileData.nic ILIKE '%" + nic + "%' )" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherTeacherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherTeacherProfileContactNoFacadeMapper();

        Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherTeacherProfileContactNoFacadeDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> indexTeacherAndTeacherProfileAndContactNoWithStatus(Boolean status, String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page) {
        String query = "Select * from (\n" +
                "(select teacher.* from teachers as teacher\n" +
                " WHERE teacher.deleted_at is null\n" +
                " AND teacher.status = " + status +
                " order by " + dp + " " + d + " " +
                " limit " + size + " offset " + page +
                ") as teacherData\n" +
                "left join \n" +
                "(select teacher_profiles.teacher_uuid as teacherUUID, \n" +
                "teacher_profiles.image as image,   teacher_profiles.first_name as firstName, teacher_profiles.last_name as lastName, \n" +
                "teacher_profiles.email as email,  teacher_profiles.tel as telephoneNo, teacher_profiles.nic as nic, teacher_profiles.birth_date as birthDate, \n" +
                "teacher_profiles.city_uuid as cityUUID, teacher_profiles.state_uuid as stateUUID, teacher_profiles.country_uuid as countryUUID, \n" +
                "teacher_profiles.religion_uuid as religionUUID, teacher_profiles.sect_uuid as sectUUID, teacher_profiles.caste_uuid as casteUUID, \n" +
                "teacher_profiles.gender_uuid as genderUUID, teacher_profiles.marital_status_uuid as maritalStatusUUID \n" +
                "from teacher_profiles \n" +
                "where teacher_profiles.deleted_at is null) as teacherProfileData\n" +
                "on teacherData.uuid=teacherProfileData.teacherUUID\n" +
                "left join \n" +
                "(select teacher_contact_nos.contact_type_uuid as contactTypeUUID, teacher_contact_nos.contact_no as contactNo,  \n" +
                " teacher_contact_nos.teacher_meta_uuid as teacherMetaUUID\n" +
                " from teacher_contact_nos \n" +
                " where teacher_contact_nos.deleted_at is null) as teacherContactNos \n" +
                " on teacherData.uuid=teacherContactNos.teacherMetaUUID)\n" +
                "  AND (teacherData.employee_code ILIKE '%" + employeeCode + "%' OR teacherProfileData.firstName ILIKE '%" + firstName + "%' " +
                " OR teacherProfileData.lastName ILIKE '%" + lastName + "%' OR teacherProfileData.email ILIKE '%" + email + "%'" +
                " OR teacherProfileData.telephoneNo ILIKE '%" + telephoneNo + "%' OR teacherProfileData.nic ILIKE '%" + nic + "%' )" +
                " order by " + dp + " " + d;

        SlaveCustomTeacherTeacherProfileContactNoFacadeMapper mapper = new SlaveCustomTeacherTeacherProfileContactNoFacadeMapper();

        Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherTeacherProfileContactNoFacadeDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveTeacherDto> showByUuid(UUID uuid) {
        String query = "select teachers.*, concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) as key\n" +
                "  from teachers \n" +
                "  left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
                "  where teachers.uuid='" + uuid +
                "' and teachers.deleted_at IS NULL\n" +
                "  AND teacher_profiles.deleted_at IS NULL\n";

        SlaveCustomTeacherMapper mapper = new SlaveCustomTeacherMapper();

        Mono<SlaveTeacherDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTeacherDto))
                .one();

        return result;
    }
}
