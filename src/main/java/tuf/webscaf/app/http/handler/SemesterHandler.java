package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarSemesterEntity;
import tuf.webscaf.app.dbContext.master.entity.SemesterEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSemesterEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSemesterRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.ConvertRomanToIntHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "semesterCatalogueHandler")
@Component
public class SemesterHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SlaveSemesterRepository slaveSemesterRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    FeeStructureRepository feeStructureRepository;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    NotificationDetailRepository notificationDetailRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    AcademicCalendarSemesterRepository academicCalendarSemesterRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeacherOutlineRepository teacherOutlineRepository;

    @Autowired
    ConvertRomanToIntHelper convertRomanHelper;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_semesters_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveSemesterEntity> slaveSemesterFlux = slaveSemesterRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveSemesterFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSemesterEntity> slaveSemesterFlux = slaveSemesterRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveSemesterFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID semesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSemesterRepository.findByUuidAndDeletedAtIsNull(semesterUUID)
                .flatMap(semesterEntity -> responseSuccessMsg("Record Fetched Successfully", semesterEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_academic-calendar_show_mapped")
    public Mono<ServerResponse> showMappedSemestersAgainstAcademicCalendar(ServerRequest serverRequest) {

        UUID academicCalendarUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarUUID"));

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        if (!status.isEmpty()) {
            Flux<SlaveSemesterEntity> slaveSemesterEntityFlux = slaveSemesterRepository
                    .showMappedSemestersAgainstAcademicCalendarWithStatus(searchKeyWord, academicCalendarUUID, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSemesterEntityFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository.countMappedSemestersAgainstAcademicCalendarWithStatus(academicCalendarUUID, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        } else {
            Flux<SlaveSemesterEntity> slaveSemesterEntityFlux = slaveSemesterRepository
                    .showMappedSemestersAgainstAcademicCalendarWithOutStatus(searchKeyWord, academicCalendarUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSemesterEntityFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository.countMappedSemestersAgainstAcademicCalendarWithOutStatus(academicCalendarUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_student_course_index")
    public Mono<ServerResponse> showSemesterAgainstStudentAndCourse(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Query Parameter of courseUUID
        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        // Query Parameter of courseUUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));


        if (!status.isEmpty() && !courseUUID.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveSemesterEntity> slaveSemesterFlux = slaveSemesterRepository
                    .showSemestersAgainstCourseWithStatus(searchKeyWord, UUID.fromString(courseUUID), UUID.fromString(studentUUID), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSemesterFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository
                            .countSemestersAgainstCourseWithStatus(searchKeyWord, UUID.fromString(courseUUID), UUID.fromString(studentUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSemesterEntity> slaveSemesterFlux = slaveSemesterRepository
                    .showSemestersAgainstCourseWithOutStatus(searchKeyWord, UUID.fromString(courseUUID), UUID.fromString(studentUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSemesterFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository
                            .countSemestersAgainstCourseWithOutStatus(searchKeyWord, UUID.fromString(courseUUID), UUID.fromString(studentUUID))
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_academic-calendar_show_unmapped")
    public Mono<ServerResponse> showUnMappedSemestersAgainstAcademicCalendar(ServerRequest serverRequest) {

        UUID academicSessionUUID = UUID.fromString(serverRequest.pathVariable("academicSessionUUID"));

        // Query Parameter of courseLevelUUID
        String courseLevelUUID = serverRequest.queryParam("courseLevelUUID").map(String::toString).orElse("").trim();

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        if (!status.isEmpty()) {
            Flux<SlaveSemesterEntity> slaveSemesterEntityFlux = slaveSemesterRepository
                    .unMappedSemestersAgainstAcademicCalendarWithStatus(searchKeyWord, academicSessionUUID, UUID.fromString(courseLevelUUID), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSemesterEntityFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository.countUnMappedSemestersAgainstAcademicCalendarWithStatus(academicSessionUUID, UUID.fromString(courseLevelUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        } else {
            Flux<SlaveSemesterEntity> slaveSemesterEntityFlux = slaveSemesterRepository
                    .showUnMappedSemestersAgainstAcademicCalendar(searchKeyWord, academicSessionUUID, UUID.fromString(courseLevelUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSemesterEntityFlux
                    .collectList()
                    .flatMap(semesterEntity -> slaveSemesterRepository.countUnMappedSemestersAgainstAcademicCalendarWithOutStatus(academicSessionUUID, UUID.fromString(courseLevelUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (semesterEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", semesterEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        }
    }

    @AuthHasPermission("academic_api_v1_semesters_academic-calendar_delete")
    public Mono<ServerResponse> deleteSemestersAgainstAcademicCalendar(ServerRequest serverRequest) {

        final UUID academicCalendarUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarUUID"));

        UUID semesterUUID = UUID.fromString(serverRequest.queryParam("semesterUUID").map(String::toString).orElse(""));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return semesterRepository.findByUuidAndDeletedAtIsNull(semesterUUID)
                .flatMap(semesterEntity -> academicCalendarSemesterRepository
                        .findFirstByAcademicCalendarUUIDAndSemesterUUIDAndDeletedAtIsNull(academicCalendarUUID, semesterUUID)
                        .flatMap(academicCalendarSemesterEntity -> {

                            academicCalendarSemesterEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            academicCalendarSemesterEntity.setDeletedBy(UUID.fromString(userId));
                            academicCalendarSemesterEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            academicCalendarSemesterEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            academicCalendarSemesterEntity.setReqDeletedIP(reqIp);
                            academicCalendarSemesterEntity.setReqDeletedPort(reqPort);
                            academicCalendarSemesterEntity.setReqDeletedBrowser(reqBrowser);
                            academicCalendarSemesterEntity.setReqDeletedOS(reqOs);
                            academicCalendarSemesterEntity.setReqDeletedDevice(reqDevice);
                            academicCalendarSemesterEntity.setReqDeletedReferer(reqReferer);

                            return academicCalendarSemesterRepository.save(academicCalendarSemesterEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", semesterEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Semester record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Semester record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission("academic_api_v1_semesters_academic-calendar_multiple_delete")
    public Mono<ServerResponse> deleteMultipleSemesterAgainstAcademicCalendar(ServerRequest serverRequest) {

        final UUID academicCalendarUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarUUID"));

//        List<String> semesterUUIDs = serverRequest.queryParams().get("semesterUUID");

        //This is Company List to paas in the query
//        List<UUID> semesterList = new ArrayList<>();
//        if (semesterUUIDs != null) {
//            for (String semester : semesterUUIDs) {
//                semesterList.add(UUID.fromString(semester));
//            }
//        }

        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return
//                semesterRepository.findAllByUuidInAndDeletedAtIsNull(semesterList)
//                .collectList()
//                .flatMap(semesterEntity ->
                academicCalendarSemesterRepository
                        .findAllByAcademicCalendarUUIDAndDeletedAtIsNull(academicCalendarUUID)
                        .collectList()
                        .flatMap(academicCalendarSemesterEntity -> {

                            List<AcademicCalendarSemesterEntity> deleteMultipleSemesterList = new ArrayList<>();

                            for (AcademicCalendarSemesterEntity calendarSemesterEntity : academicCalendarSemesterEntity) {
                                calendarSemesterEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                calendarSemesterEntity.setDeletedBy(UUID.fromString(userId));
                                calendarSemesterEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                calendarSemesterEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                calendarSemesterEntity.setReqDeletedIP(reqIp);
                                calendarSemesterEntity.setReqDeletedPort(reqPort);
                                calendarSemesterEntity.setReqDeletedBrowser(reqBrowser);
                                calendarSemesterEntity.setReqDeletedOS(reqOs);
                                calendarSemesterEntity.setReqDeletedDevice(reqDevice);
                                calendarSemesterEntity.setReqDeletedReferer(reqReferer);

                                deleteMultipleSemesterList.add(calendarSemesterEntity);
                            }

                            return academicCalendarSemesterRepository.saveAll(deleteMultipleSemesterList)
                                    .collectList()
                                    .flatMap(listOfSemesterEntity -> responseSuccessMsg("Record Deleted Successfully", listOfSemesterEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
//                ).switchIfEmpty(responseInfoMsg("Ailment record does not exist"))
//                .onErrorResume(err -> responseErrorMsg("Ailment record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> {

                    SemesterEntity entity = SemesterEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                            .reqCreatedIP(reqIp)
                            .reqCreatedPort(reqPort)
                            .reqCreatedBrowser(reqBrowser)
                            .reqCreatedOS(reqOs)
                            .reqCreatedDevice(reqDevice)
                            .reqCreatedReferer(reqReferer)
                            .build();

                    long semester = Long.parseLong(value.getFirst("semesterNo").trim());
                    entity.setSemesterNo(ConvertRomanToIntHelper.intToRoman(semester));

                    //check semester is unique
                    return semesterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(semesterEntity -> responseInfoMsg("Name Already Exist"))
                            .switchIfEmpty(Mono.defer(() -> semesterRepository.findFirstBySemesterNoIgnoreCaseAndDeletedAtIsNull(entity.getSemesterNo())
                                    .flatMap(semesterEntity -> responseInfoMsg("Semester No. Already Exist"))
                            ))
                            .switchIfEmpty(Mono.defer(() -> semesterRepository.save(entity)
                                    .flatMap(semesterEntity -> responseSuccessMsg("Record Stored Successfully", semesterEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID semesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> semesterRepository.findByUuidAndDeletedAtIsNull(semesterUUID)
                        .flatMap(previousEntity -> {

                            SemesterEntity updatedEntity = SemesterEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            String semesterNo = value.getFirst("semesterNo").trim();
                            if (semesterNo.matches("^[MDCLXVI]+$")) {
                                semesterNo = String.valueOf(ConvertRomanToIntHelper.romanToInteger(semesterNo));
                            }

                            long semester = Long.parseLong(semesterNo);
                            updatedEntity.setSemesterNo(ConvertRomanToIntHelper.intToRoman(semester));

                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            // check semester is unique
                            return semesterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), semesterUUID)
                                    .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> semesterRepository.findFirstBySemesterNoIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getSemesterNo(), semesterUUID)
                                            .flatMap(checkSemesterNo -> responseInfoMsg("Semester No Already Exists"))
                                    ))
                                    .switchIfEmpty(Mono.defer(() -> semesterRepository.save(previousEntity)
                                            .then(semesterRepository.save(updatedEntity))
                                            .flatMap(semesterEntity -> responseSuccessMsg("Record Updated Successfully", semesterEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID semesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }
        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return semesterRepository.findByUuidAndDeletedAtIsNull(semesterUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SemesterEntity updatedEntity = SemesterEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .semesterNo(previousEntity.getSemesterNo())
                                        .description(previousEntity.getDescription())
                                        .status(status == true ? true : false)
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return semesterRepository.save(previousEntity)
                                        .then(semesterRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_semesters_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID semesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return semesterRepository.findByUuidAndDeletedAtIsNull(semesterUUID)
                //Checks if Semester Reference exists in Fee Structures
                .flatMap(semesterEntity -> feeStructureRepository.findFirstBySemesterUUIDAndDeletedAtIsNull(semesterEntity.getUuid())
                                .flatMap(sectionEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))
                                //Checks if Semester Reference exists in Enrollments
                                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findFirstBySemesterUUIDAndDeletedAtIsNull(semesterEntity.getUuid())
                                        .flatMap(enrollmentEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))))
//                        //Checks if Semester Reference exists in Timetables
//                        .switchIfEmpty(Mono.defer(() -> timetableRepository.findFirstBySemesterUUIDAndDeletedAtIsNull(semesterEntity.getUuid())
//                                .flatMap(timetableEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    semesterEntity.setDeletedBy(UUID.fromString(userId));
                                    semesterEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    semesterEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    semesterEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    semesterEntity.setReqDeletedIP(reqIp);
                                    semesterEntity.setReqDeletedPort(reqPort);
                                    semesterEntity.setReqDeletedBrowser(reqBrowser);
                                    semesterEntity.setReqDeletedOS(reqOs);
                                    semesterEntity.setReqDeletedDevice(reqDevice);
                                    semesterEntity.setReqDeletedReferer(reqReferer);

                                    return semesterRepository.save(semesterEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist."))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
    }

    public Mono<ServerResponse> responseInfoMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );


        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()

        );
    }

    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.empty()

        );
    }


    public Mono<ServerResponse> responseErrorMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.ERROR,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }

    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg)
        );


        return appresponse.set(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }
}
