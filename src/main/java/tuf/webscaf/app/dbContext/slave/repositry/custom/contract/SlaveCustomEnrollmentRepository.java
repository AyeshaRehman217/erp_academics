package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrollmentDto;

import java.util.UUID;

public interface SlaveCustomEnrollmentRepository {

    Mono<SlaveEnrollmentDto> showRecordByUUID(UUID uuid);

    //fetch All Records without status filter
    Flux<SlaveEnrollmentDto> indexAllRecordsWithOutStatusFilter(String key, String dp, String d, Integer size, Long page);

    //fetch All Records with Status Filter
    Flux<SlaveEnrollmentDto> indexAllRecordsWithStatusFilter(String key, Boolean status, String dp, String d, Integer size, Long page);

    //fetch All Records with Academic Session Filter
    Flux<SlaveEnrollmentDto> indexAllRecordsWithAcademicSessionFilter(UUID academicSessionUUID, String key, String dp, String d, Integer size, Long page);

    //fetch All Records with Status and Academic session filter
    Flux<SlaveEnrollmentDto> indexAllRecordsWithStatusAndAcademicSessionFilter(String key, Boolean status, UUID academicSessionUUID, String dp, String d, Integer size, Long page);

    /**
     * Fetch All Enrollments Against the Subject And Academic Session (With & Without Status Filter)
     **/
    //fetch All Records with Subject Filter and Academic Session
    Flux<SlaveEnrollmentDto> indexWithSubjectAndAcademicSessionFilter(UUID academicSessionUUID, UUID subjectUUID, String key, String dp, String d, Integer size, Long page);

    //fetch All Records with Status and Subject filter and Academic Session
    Flux<SlaveEnrollmentDto> indexWithSubjectAndAcademicSessionWithStatusFilter(UUID academicSessionUUID, UUID subjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page);

    /**
     * Fetch All Enrollments Against the Subject (With & Without Status Filter)
     **/
    //fetch All Records with Subject Filter
    Flux<SlaveEnrollmentDto> indexWithSubjectFilter(UUID subjectUUID, String key, String dp, String d, Integer size, Long page);

    //fetch All Records with Status and Subject filter
    Flux<SlaveEnrollmentDto> indexWithSubjectWithStatusFilter(UUID subjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page);

    //fetch All Records with Academic Session Filter and Course Subject
//    Flux<SlaveEnrollmentDto> indexWithAcademicSessionAndCourseSubjectFilter(UUID academicSessionUUID, UUID courseSubjectUUID, String key, String dp, String d, Integer size, Long page);
//
//    //fetch All Records with Status and Academic session filter and Course Subject
//    Flux<SlaveEnrollmentDto> indexWithStatusAndAcademicSessionAndCourseSubjectFilter(UUID academicSessionUUID, UUID courseSubjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page);


    //fetch All Records with Course Subject Filter
//    Flux<SlaveEnrollmentDto> indexWithCourseSubjectFilter(UUID courseSubjectUUID, String key, String dp, String d, Integer size, Long page);
//
//    //fetch All Records with Status and Course Subject filter
//    Flux<SlaveEnrollmentDto> indexWithStatusAndCourseSubjectFilter(UUID courseSubjectUUID, Boolean status, String key, String dp, String d, Integer size, Long page);


}
