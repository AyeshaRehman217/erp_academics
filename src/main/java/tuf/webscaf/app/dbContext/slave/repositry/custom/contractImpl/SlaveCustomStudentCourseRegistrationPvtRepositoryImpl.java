//package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;
//
//import io.r2dbc.spi.ConnectionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.r2dbc.core.DatabaseClient;
//import reactor.core.publisher.Flux;
//import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegistrationCourseDto;
//import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentCourseRegistrationPvtRepository;
//import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentCourseRegisteredMapper;
//
//import java.util.UUID;
//
//public class SlaveCustomStudentCourseRegistrationPvtRepositoryImpl implements SlaveCustomStudentCourseRegistrationPvtRepository {
//    private DatabaseClient client;
//    private SlaveStudentRegistrationCourseDto stdRegistrationDto;
//
//    @Autowired
//    public SlaveCustomStudentCourseRegistrationPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
//        this.client = DatabaseClient.create(cf);
//    }
//
//    @Override
//    public Flux<SlaveStudentRegistrationCourseDto> existingCourseList(UUID studentUUID, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page) {
//
//
//        String query = "select courses.*,\n" +
//                "course_types.name as courseTypeName,\n" +
//                "course_levels.name as courseLevelName,\n" +
//                "academic_sessions.year as academicSessionYear\n" +
//                "FROM courses \n" +
//                "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//                "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//                "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//                "WHERE courses.uuid NOT IN (\n" +
//                "SELECT courses.uuid FROM courses\n" +
//                "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//                "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN student_course_registrations_pvt ON student_course_registrations_pvt.course_offered_uuid=course_offered.uuid\n" +
//                "WHERE student_course_registrations_pvt.student_uuid= '" + studentUUID +
//                "' AND student_course_registrations_pvt.deleted_at IS NULL \n" +
//                "AND courses.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND course_offered.deleted_at IS NULL\n" +
//                "AND courses.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND course_offered.deleted_at IS NULL)\n" +
//                "AND course_types.deleted_at IS NULL\n" +
//                "AND course_levels.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND (courses.name ILIKE '%" + courseName + "%' \n" +
//                "OR courses.slug ILIKE '%" + courseSlug + "%'  \n" +
//                "OR courses.short_name ILIKE '%" + courseShortName + "%' \n" +
//                "OR courses.description ILIKE  '%" + courseDescription + "%' \n" +
//                "OR courses.code ILIKE  '%" + courseCode + "%' \n" +
//                "OR course_levels.name ILIKE  '%" + searchCourseLevelName + "%' \n" +
//                "OR course_types.name ILIKE  '%" + searchCourseTypeName + "%'\n" +
//                ")" +
//                "ORDER BY courses." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomStudentCourseRegisteredMapper mapper = new SlaveCustomStudentCourseRegisteredMapper();
//
//        Flux<SlaveStudentRegistrationCourseDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, stdRegistrationDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveStudentRegistrationCourseDto> existingCourseListWithStatus(UUID studentUUID, Boolean status, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page) {
//
//
//        String query = "select courses.*,\n" +
//                "course_types.name as courseTypeName,\n" +
//                "course_levels.name as courseLevelName,\n" +
//                "academic_sessions.year as academicSessionYear\n" +
//                "FROM courses \n" +
//                "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//                "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//                "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//                "WHERE courses.uuid NOT IN (\n" +
//                "SELECT courses.uuid FROM courses\n" +
//                "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//                "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN student_course_registrations_pvt ON student_course_registrations_pvt.course_offered_uuid=course_offered.uuid\n" +
//                "WHERE student_course_registrations_pvt.student_uuid = '" + studentUUID +
//                "'AND student_course_registrations_pvt.deleted_at IS NULL \n" +
//                "AND courses.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND course_offered.deleted_at IS NULL\n" +
//                "AND courses.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND course_offered.deleted_at IS NULL)\n" +
//                "AND courses.status = " + status +
//                " AND course_types.deleted_at IS NULL\n" +
//                "AND course_levels.deleted_at IS NULL\n" +
//                "AND academic_sessions.deleted_at IS NULL\n" +
//                "AND (courses.name ILIKE '%" + courseName + "%' \n" +
//                "OR courses.slug ILIKE '%" + courseSlug + "%'  \n" +
//                "OR courses.short_name ILIKE '%" + courseShortName + "%' \n" +
//                "OR courses.description ILIKE  '%" + courseDescription + "%' \n" +
//                "OR courses.code ILIKE  '%" + courseCode + "%' \n" +
//                "OR course_levels.name ILIKE  '%" + searchCourseLevelName + "%' \n" +
//                "OR course_types.name ILIKE  '%" + searchCourseTypeName + "%'\n" +
//                ")" +
//                " ORDER BY courses." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomStudentCourseRegisteredMapper mapper = new SlaveCustomStudentCourseRegisteredMapper();
//
//        Flux<SlaveStudentRegistrationCourseDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, stdRegistrationDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveStudentRegistrationCourseDto> showMappedCourseListAgainstStudent(UUID studentUUID, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page) {
//
//        String query = "select courses.*,\n" +
//                "course_types.name as courseTypeName,\n" +
//                "course_levels.name as courseLevelName,\n" +
//                "academic_sessions.year as academicSessionYear\n" +
//                "from courses \n" +
//                "left join course_offered on courses.uuid = course_offered.course_uuid\n" +
//                "left join student_course_registrations_pvt on course_offered.uuid=student_course_registrations_pvt.course_offered_uuid\n" +
//                "left JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//                "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//                "where student_course_registrations_pvt.student_uuid = '" + studentUUID +
//                "' and course_offered.deleted_at IS NULL\n" +
//                "and student_course_registrations_pvt.deleted_at IS NULL\n" +
//                "and courses.deleted_at IS NULL\n" +
//                "and course_types.deleted_at IS NULL\n" +
//                "and academic_sessions.deleted_at IS NULL\n" +
//                "and course_levels.deleted_at IS NULL \n" +
//                "AND (courses.name ILIKE '%" + courseName + "%' \n" +
//                "OR courses.slug ILIKE '%" + courseSlug + "%'  \n" +
//                "OR courses.short_name ILIKE '%" + courseShortName + "%' \n" +
//                "OR courses.description ILIKE  '%" + courseDescription + "%' \n" +
//                "OR courses.code ILIKE  '%" + courseCode + "%' \n" +
//                "OR course_levels.name ILIKE  '%" + searchCourseLevelName + "%' \n" +
//                "OR course_types.name ILIKE  '%" + searchCourseTypeName + "%'\n" +
//                ")" +
//                " ORDER BY courses." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomStudentCourseRegisteredMapper mapper = new SlaveCustomStudentCourseRegisteredMapper();
//
//        Flux<SlaveStudentRegistrationCourseDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, stdRegistrationDto))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveStudentRegistrationCourseDto> showMappedCourseListAgainstStudentWithStatus(UUID studentUUID, Boolean status, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page) {
//        String query = "select courses.*,\n" +
//                "course_types.name as courseTypeName,\n" +
//                "course_levels.name as courseLevelName,\n" +
//                "academic_sessions.year as academicSessionYear\n" +
//                "from courses \n" +
//                "left join course_offered on courses.uuid = course_offered.course_uuid\n" +
//                "left join student_course_registrations_pvt on course_offered.uuid=student_course_registrations_pvt.course_offered_uuid\n" +
//                "left JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//                "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//                "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//                "where student_course_registrations_pvt.student_uuid = '" + studentUUID +
//                "' and course_offered.deleted_at IS NULL\n" +
//                "and student_course_registrations_pvt.deleted_at IS NULL\n" +
//                "and courses.status  = " + status +
//                " and courses.deleted_at IS NULL\n" +
//                "and course_types.deleted_at IS NULL\n" +
//                "and academic_sessions.deleted_at IS NULL\n" +
//                "and course_levels.deleted_at IS NULL \n" +
//                "AND (courses.name ILIKE '%" + courseName + "%' \n" +
//                "OR courses.slug ILIKE '%" + courseSlug + "%'  \n" +
//                "OR courses.short_name ILIKE '%" + courseShortName + "%' \n" +
//                "OR courses.description ILIKE  '%" + courseDescription + "%' \n" +
//                "OR courses.code ILIKE  '%" + courseCode + "%' \n" +
//                "OR course_levels.name ILIKE  '%" + searchCourseLevelName + "%' \n" +
//                "OR course_types.name ILIKE  '%" + searchCourseTypeName + "%'\n" +
//                ")" +
//                " ORDER BY courses." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomStudentCourseRegisteredMapper mapper = new SlaveCustomStudentCourseRegisteredMapper();
//
//        Flux<SlaveStudentRegistrationCourseDto> result = client.sql(query)
//                .map(row -> mapper.apply(row, stdRegistrationDto))
//                .all();
//
//        return result;
//    }
//
//}
