//package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;
//
//
//import reactor.core.publisher.Flux;
//import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegistrationCourseDto;
//
//import java.util.UUID;
//
//// This interface wil extends in  Student Course Registrations Pvt Repository
//public interface SlaveCustomStudentCourseRegistrationPvtRepository {
//
//    //use to Show Existing Course List Against Student UUID
//    Flux<SlaveStudentRegistrationCourseDto> existingCourseList(UUID studentUUID, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveStudentRegistrationCourseDto> existingCourseListWithStatus(UUID studentUUID, Boolean status, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveStudentRegistrationCourseDto> showMappedCourseListAgainstStudent(UUID studentUUID, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveStudentRegistrationCourseDto> showMappedCourseListAgainstStudentWithStatus(UUID studentUUID, Boolean status, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName, String dp, String d, Integer size, Long page);
//
//
//}
