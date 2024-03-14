package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;

import java.util.UUID;

// This interface wil extends in  Slave Subject Repository
public interface SlaveCustomSubjectRepository {
    /**
     * Fetch Subjects Against Academic Sessions with and without status filter
     **/
    Flux<SlaveSubjectEntity> indexSubjectAgainstAcademicSession(UUID academicSessionUUID, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectEntity> indexSubjectAgainstAcademicSessionWithStatusFilter(UUID academicSessionUUID, Boolean status, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subjects Based on Enrolled Students With & Without Status Filter
     **/
    Flux<SlaveEnrolledSubjectDto> fetchEnrolledSubjectWithStatusFilter(UUID studentUUID, Boolean status, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page);

    Flux<SlaveEnrolledSubjectDto> fetchEnrolledSubjectWithoutStatusFilter(UUID studentUUID, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subjects Based on Enrolled Students && Course  With & Without Status Filter
     **/
    Flux<SlaveEnrolledSubjectDto> fetchSubjectAgainstCourseAndStudentWithStatusFilter(UUID studentUUID, UUID courseUUID, Boolean status, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page);

    Flux<SlaveEnrolledSubjectDto> fetchSubjectAgainstCourseAndStudentWithoutStatusFilter(UUID studentUUID, UUID courseUUID, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subjects Based on Course With & Without Status Filter
     **/
    Flux<SlaveEnrolledCourseSubjectDto> fetchSubjectAgainstCourseWithStatusFilter(UUID courseUUID, Boolean status, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String dp, String d, Integer size, Long page);

    Flux<SlaveEnrolledCourseSubjectDto> fetchSubjectAgainstCourseWithoutStatusFilter(UUID courseUUID, String subjectName, String subjectShortName, String description, String slug, String subjectCode, String courseName, String courseCode, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subject With &  Without Status Filter
     **/
    Flux<SlaveSubjectEntity> fetchSubjectWithStatusFilter(Boolean status, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectEntity> fetchSubjectWithoutStatusFilter(String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subject With and Without Status and openLMS Filter (Used by LMS Module) Against Academic Session and Teacher
     **/
    Flux<SlaveSubjectDto> fetchSubjectWithStatusAndOpenLMSFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, Boolean status, String key, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectDto> fetchSubjectWithOpenLMSFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean openLMS, String key, String name, String shortName, String description, String slug, String code, String dp, String d, Integer size, Long page);

    /**
     * This Function is used in Timetable Creation & Recreation Handler it is used to fetch subjects against academic session and Teacher
     **/
    Flux<SlaveSubjectDto> showSubjectAgainstTeacherAndAcademicSession(UUID academicSessionUUID, UUID teacherUUID, String key, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page);

    /**
     * This Function is used in Timetable Creation & Recreation Handler it is used to fetch subjects against academic session and Teacher (With Status Filter)
     **/
    Flux<SlaveSubjectDto> showSubjectAgainstTeacherAndAcademicSessionWithStatusFilter(UUID academicSessionUUID, UUID teacherUUID, Boolean status, String key, String name, String shortName, String description, String code, String dp, String d, Integer size, Long page);

    /**
     * Fetch Subject Against Student, Course and Semester With &  Without Status Filter
     **/
    Flux<SlaveSubjectEntity> fetchSubjectAgainstStudentCourseAndSemesterWithStatusFilter(Boolean status, String name, String shortName, String description, String slug, String code, UUID studentUUID, UUID courseUUID, UUID semesterUUID, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectEntity> fetchSubjectAgainstStudentCourseAndSemesterWithoutStatusFilter(String name, String shortName, String description, String slug, String code, UUID studentUUID, UUID courseUUID, UUID semesterUUID, String dp, String d, Integer size, Long page);

}
