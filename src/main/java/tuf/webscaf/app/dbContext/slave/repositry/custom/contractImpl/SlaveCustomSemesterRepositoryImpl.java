package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSemesterEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSemesterRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSemesterMapper;

import java.util.UUID;

public class SlaveCustomSemesterRepositoryImpl implements SlaveCustomSemesterRepository {
    private DatabaseClient client;
    private SlaveSemesterEntity slaveSemesterEntity;

    @Autowired
    public SlaveCustomSemesterRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveSemesterEntity> showUnMappedSemestersAgainstAcademicCalendar(String name, UUID academicSessionUUID, UUID courseLevelUUID, String dp, String d, Integer size, Long page) {
        String query = "SELECT semesters.* FROM semesters\n" +
                " WHERE semesters.uuid NOT IN(\n" +
                "select semesters.uuid from semesters\n" +
                " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
                " join academic_calendars on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
                " where academic_calendars.academic_session_uuid = '" + academicSessionUUID +
                "' and academic_calendars.course_level_uuid = '" + courseLevelUUID +
                "' and semesters.deleted_at is null\n" +
                " and academic_calendars.deleted_at is null\n" +
                " and academic_calendar_semesters.deleted_at is null)\n" +
                " AND (semesters.name ILIKE '%" + name + "%' )" +
                " AND semesters.deleted_at IS NULL " +
                " ORDER BY semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;


        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSemesterEntity> unMappedSemestersAgainstAcademicCalendarWithStatus(String name, UUID academicSessionUUID, UUID courseLevelUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT semesters.* FROM semesters\n" +
                " WHERE semesters.uuid NOT IN(\n" +
                "select semesters.uuid from semesters\n" +
                " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
                " join academic_calendars on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
                " where academic_calendars.academic_session_uuid = '" + academicSessionUUID +
                "' and academic_calendars.course_level_uuid = '" + courseLevelUUID +
                "' and semesters.deleted_at is null\n" +
                " and academic_calendars.deleted_at is null\n" +
                " and academic_calendar_semesters.deleted_at is null)\n" +
                " AND (semesters.name ILIKE '%" + name + "%' )" +
                " AND semesters.deleted_at IS NULL " +
                " AND semesters.status = " + status +
                " ORDER BY semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSemesterEntity> showSemestersAgainstCourseWithOutStatus(String name, UUID courseUUID, UUID studentUUID, String dp, String d, Integer size, Long page) {
        String query = "select distinct semesters.* from semesters\n" +
                "join enrollments on semesters.uuid = enrollments.semester_uuid\n" +
                "join students on students.uuid = enrollments.student_uuid\n" +
                "join registrations on students.uuid = registrations.student_uuid\n" +
                "where enrollments.course_uuid = '" + courseUUID +
                "' and students.uuid ='" + studentUUID +
                "' and registrations.student_uuid = enrollments.student_uuid\n" +
                "and registrations.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                "and enrollments.deleted_at is null\n" +
                "and semesters.deleted_at is null\n" +
                " and semesters.name ILIKE  '%" + name + "%' " +
                " order by semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSemesterEntity> showSemestersAgainstCourseWithStatus(String name, UUID courseUUID, UUID studentUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select distinct semesters.* from semesters\n" +
                "join enrollments on semesters.uuid = enrollments.semester_uuid\n" +
                "join students on students.uuid = enrollments.student_uuid\n" +
                "join registrations on students.uuid = registrations.student_uuid\n" +
                "where enrollments.course_uuid = '" + courseUUID +
                "' and students.uuid ='" + studentUUID +
                "' and registrations.student_uuid = enrollments.student_uuid\n" +
                "and registrations.deleted_at is null\n" +
                "and students.deleted_at is null\n" +
                "and enrollments.deleted_at is null\n" +
                "and semesters.deleted_at is null\n" +
                " and semesters.status = " + status +
                " and semesters.name ILIKE  '%" + name + "%' " +
                " order by semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSemesterEntity> showMappedSemestersAgainstAcademicCalendarWithOutStatus(String name, UUID academicCalendarUUID, String dp, String d, Integer size, Long page) {
        String query = "select distinct semesters.* from semesters\n" +
                " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
                " where academic_calendar_semesters.academic_calendar_uuid = '" + academicCalendarUUID +
                "' and semesters.deleted_at is null\n" +
                " and academic_calendar_semesters.deleted_at is null\n" +
                " and semesters.name ILIKE  '%" + name + "%' " +
                " order by semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;


        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSemesterEntity> showMappedSemestersAgainstAcademicCalendarWithStatus(String name, UUID academicCalendarUUID, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select semesters.* from semesters\n" +
                " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
                " where academic_calendar_semesters.academic_calendar_uuid = '" + academicCalendarUUID +
                "' and semesters.deleted_at is null\n" +
                " and academic_calendar_semesters.deleted_at is null\n" +
                " and semesters.status = " + status +
                " and semesters.name ILIKE  '%" + name + "%' " +
                " order by semesters." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSemesterMapper mapper = new SlaveCustomSemesterMapper();

        Flux<SlaveSemesterEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSemesterEntity))
                .all();

        return result;
    }
}
