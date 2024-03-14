package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;

import java.util.UUID;

// This interface wil extends in  Course Subject Repository
public interface SlaveCustomCourseSubjectRepository {

    Flux<SlaveCourseSubjectDto> courseSubjectIndex(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithStatusAndObe(String name, Boolean status, Boolean obe, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithObe(String name, Boolean obe, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourse(String name, UUID courseUUID, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndStatus(String name, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndObe(String name, UUID courseUUID, Boolean obe, String dp, String d, Integer size, Long page);

    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithCourseAndObeAndStatus(String name, UUID courseUUID, Boolean obe, Boolean status, String dp, String d, Integer size, Long page);

    //    return mapped subjects
    Flux<SlaveSubjectEntity> showMappedCourseSubjectList(String name, String description, String code, UUID courseUUID, String dp, String d, Integer size, Long page);

    //    return mapped subjects with status
    Flux<SlaveSubjectEntity> showMappedCourseSubjectListWithStatus(String name, String description, String code, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page);

    //    return un-mapped subjects
    Flux<SlaveSubjectEntity> showUnmappedCourseSubjectList(String name, String description, String code, UUID courseUUID, String dp, String d, Integer size, Long page);

    //    return un-mapped subjects with status
    Flux<SlaveSubjectEntity> showUnmappedCourseSubjectListWithStatus(String name, String description, String code, UUID courseUUID, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject against department
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartment(String name, UUID departmentUUID, String dp, String d, Integer size, Long page);

    //   return course-subject against department and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndStatus(String name, UUID departmentUUID, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject against department and obe filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndObe(String name, UUID departmentUUID, Boolean obe, String dp, String d, Integer size, Long page);

    //   return course-subject against department,Obe and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithDepartmentAndObeAndStatus(String name, UUID departmentUUID, Boolean obe, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithAcademicSession(String name, UUID academicSessionUUID, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject of offered courses against academicSession
    Flux<SlaveCourseSubjectDto> courseSubjectIndexOfOfferedCoursesWithAcademicSession(String name, UUID academicSessionUUID, String dp, String d, Integer size, Long page);

    //   return course-subject of offered courses against academicSession and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexOfOfferedCoursesWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession and teacher
    Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacher(String key, UUID academicSessionUUID, UUID teacherUUID, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession and teacher and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithStatus(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession, teacher, openLMS and status filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithStatusAndOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status, Boolean openLMS, String dp, String d, Integer size, Long page);

    //   return course-subject against academicSession, teacher, openLMS filter
    Flux<SlaveCourseSubjectDto> courseSubjectIndexAgainstSessionAndTeacherWithOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, String dp, String d, Integer size, Long page);

}
