//package tuf.webscaf.app.dbContext.slave.repositry.custom.mapper;
//
//import io.r2dbc.spi.Row;
//import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegistrationCourseDto;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//import java.util.function.BiFunction;
//
//public class SlaveCustomStudentCourseRegisteredMapper implements BiFunction<Row, Object, SlaveStudentRegistrationCourseDto> {
//    @Override
//    public SlaveStudentRegistrationCourseDto apply(Row source, Object o) {
//
//        return SlaveStudentRegistrationCourseDto.builder()
//                .academicSessionYear(source.get("academicSessionYear", LocalDateTime.class))
//                .status(source.get("status", Boolean.class))
//                .courseUUID(source.get("uuid", UUID.class))
//                .courseName(source.get("name", String.class))
//                .courseCode(source.get("code", String.class))
//                .courseSlug(source.get("slug", String.class))
//                .courseShortName(source.get("short_name", String.class))
//                .courseDescription(source.get("description", String.class))
//                .courseTypeName(source.get("courseTypeName", String.class))
//                .courseLevelName(source.get("courseLevelName", String.class))
//                .courseTypeUUID(source.get("course_type_uuid", UUID.class))
//                .courseLevelUUID(source.get("course_level_uuid", UUID.class))
//                .build();
//
//    }
//}
