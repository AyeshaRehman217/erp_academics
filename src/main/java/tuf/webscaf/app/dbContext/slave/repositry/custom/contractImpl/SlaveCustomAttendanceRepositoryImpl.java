package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAttendanceDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentSiblingPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAttendanceRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAilmentMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAttendanceMapper;

import java.util.UUID;

public class SlaveCustomAttendanceRepositoryImpl implements SlaveCustomAttendanceRepository {
    private DatabaseClient client;
    private SlaveAttendanceDto slaveAttendanceDto;

    @Autowired
    public SlaveCustomAttendanceRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAttendanceDto> indexAttendanceWithStatusFilter(Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page) {
        String query = "select attendances.*,subjects.code as subjectCode,subjects.name as subjectName, \n" +
                "students.id as studentID,lecture_types.name as lectureTypeName,days.name as day, \n" +
                "commencement_of_classes.start_time as startTime, \n" +
                "commencement_of_classes.end_time as endTime, \n" +
                "commencement_of_classes.subject_uuid, concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) as key \n" +
                "from attendances \n" +
                "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid \n" +
                "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid \n" +
                "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                "join students on commencement_of_classes.student_uuid=students.uuid \n" +
                "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid \n" +
                "join days on commencement_of_classes.day=days.uuid \n" +
                "where attendances.deleted_at is null \n" +
                " and attendances.status= " + status +
                " and commencement_of_classes.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and attendance_types.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                "and lecture_types.deleted_at is null\n" +
                "and days.deleted_at is null \n" +
                " AND (subjects.code ILIKE '%" + subjectCode + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name)  ILIKE '%" + key + "%'  " +
                " or subjects.name ILIKE '%" + subjectName + "%'  " +
                "or lecture_types.name ILIKE '%" + lectureTypeName + "%' " +
                "or days.name ILIKE '%" + day + "%' )" +
                " ORDER BY attendances." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAttendanceMapper mapper = new SlaveCustomAttendanceMapper();

        Flux<SlaveAttendanceDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAttendanceDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAttendanceDto> indexAttendanceWithoutStatusFilter(String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page) {
        String query = "select attendances.*,subjects.code as subjectCode,subjects.name as subjectName, \n" +
                " students.id as studentID,lecture_types.name as lectureTypeName,days.name as day, \n" +
                " commencement_of_classes.start_time as startTime, \n" +
                "commencement_of_classes.end_time as endTime, \n" +
                "commencement_of_classes.subject_uuid, concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) as key \n" +
                "from attendances \n" +
                "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid \n" +
                "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid \n" +
                "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                "join students on commencement_of_classes.student_uuid=students.uuid \n" +
                "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid \n" +
                "join days on commencement_of_classes.day=days.uuid \n" +
                "where attendances.deleted_at is null \n" +
                " and commencement_of_classes.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and attendance_types.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                "and lecture_types.deleted_at is null\n" +
                "and days.deleted_at is null \n" +
                " AND (subjects.code ILIKE '%" + subjectCode + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name)  ILIKE '%" + key + "%'  " +
                " or subjects.name ILIKE '%" + subjectName + "%'  " +
                "or lecture_types.name ILIKE '%" + lectureTypeName + "%' " +
                "or days.name ILIKE '%" + day + "%' )" +
                " ORDER BY attendances." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAttendanceMapper mapper = new SlaveCustomAttendanceMapper();

        Flux<SlaveAttendanceDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAttendanceDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAttendanceDto> indexAttendanceWithStatusAndSubjectFilter(UUID subjectUUID, Boolean status, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page) {
        String query = "select attendances.*,subjects.code as subjectCode,subjects.name as subjectName, \n" +
                "students.id as studentID,lecture_types.name as lectureTypeName,days.name as day, \n" +
                "commencement_of_classes.start_time as startTime, \n" +
                "commencement_of_classes.end_time as endTime, \n" +
                "commencement_of_classes.subject_uuid, concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) as key \n" +
                "from attendances \n" +
                "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid \n" +
                "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid \n" +
                "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                "join students on commencement_of_classes.student_uuid=students.uuid \n" +
                "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid \n" +
                "join days on commencement_of_classes.day=days.uuid \n" +
                "where attendances.deleted_at is null \n" +
                " and attendances.status= " + status +
                " and commencement_of_classes.subject_uuid= '" + subjectUUID +
                "' and commencement_of_classes.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and attendance_types.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                "and lecture_types.deleted_at is null\n" +
                "and days.deleted_at is null \n" +
                " AND (subjects.code ILIKE '%" + subjectCode + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name)  ILIKE '%" + key + "%'  " +
                " or subjects.name ILIKE '%" + subjectName + "%'  " +
                "or lecture_types.name ILIKE '%" + lectureTypeName + "%' " +
                "or days.name ILIKE '%" + day + "%' )" +
                " ORDER BY attendances." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAttendanceMapper mapper = new SlaveCustomAttendanceMapper();

        Flux<SlaveAttendanceDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAttendanceDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAttendanceDto> indexAttendanceAgainstSubjectWithoutStatusFilter(UUID subjectUUID, String key, String subjectCode, String subjectName, String lectureTypeName, String day, String dp, String d, Integer size, Long page) {
        String query = "select attendances.*,subjects.code as subjectCode,subjects.name as subjectName, \n" +
                " students.id as studentID,lecture_types.name as lectureTypeName,days.name as day, \n" +
                " commencement_of_classes.start_time as startTime, \n" +
                " commencement_of_classes.end_time as endTime, \n" +
                " commencement_of_classes.subject_uuid, concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name) as key \n" +
                "from attendances \n" +
                "join attendance_types on attendances.attendance_type_uuid=attendance_types.uuid \n" +
                "join commencement_of_classes on attendances.commencement_of_classes_uuid=commencement_of_classes.uuid \n" +
                "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                "join students on commencement_of_classes.student_uuid=students.uuid \n" +
                "join lecture_types on commencement_of_classes.lecture_type_uuid=lecture_types.uuid \n" +
                "join days on commencement_of_classes.day=days.uuid \n" +
                "where attendances.deleted_at is null \n" +
                " and commencement_of_classes.subject_uuid = '" + subjectUUID +
                "' and commencement_of_classes.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and attendance_types.deleted_at is null \n" +
                "and students.deleted_at is null \n" +
                "and lecture_types.deleted_at is null \n" +
                "and days.deleted_at is null \n" +
                " AND (subjects.code ILIKE '%" + subjectCode + "%' " +
                " or concat_ws('|',academic_sessions.name,subjects.code,subjects.name,students.id,attendance_types.name)  ILIKE '%" + key + "%'  " +
                " or subjects.name ILIKE '%" + subjectName + "%'  " +
                "or lecture_types.name ILIKE '%" + lectureTypeName + "%' " +
                "or days.name ILIKE '%" + day + "%' )" +
                " ORDER BY attendances." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAttendanceMapper mapper = new SlaveCustomAttendanceMapper();

        Flux<SlaveAttendanceDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAttendanceDto))
                .all();

        return result;
    }

}
