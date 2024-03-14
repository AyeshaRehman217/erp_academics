package tuf.webscaf.app.http.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicSessionEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicSessionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicSessionRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "academicSessionHandler")
@Component
public class AcademicSessionHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    FeeStructureRepository feeStructureRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AcademicCalendarRepository academicCalendarRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    SessionTypeRepository sessionTypeRepository;

    @Autowired
    SlaveAcademicSessionRepository slaveAcademicSessionRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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

        //  Optional status query parameter
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //  Optional isOpen query parameter
        String isOpen = serverRequest.queryParam("isOpen").map(String::toString).orElse("").trim();

        //  Optional isRegistrationOpen query parameter
        String isRegistrationOpen = serverRequest.queryParam("isRegistrationOpen").map(String::toString).orElse("").trim();

        //  Optional isEnrollmentOpen query parameter
        String isEnrollmentOpen = serverRequest.queryParam("isEnrollmentOpen").map(String::toString).orElse("").trim();

        //  Optional isTimetableAllow query parameter
        String isTimetableAllow = serverRequest.queryParam("isTimetableAllow").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //  get academic session bases on status, isOpen, isRegistrationOpen, isEnrollmentOpen and isTimetableAllow
        if (!status.isEmpty() && !isOpen.isEmpty() && !isRegistrationOpen.isEmpty() && !isEnrollmentOpen.isEmpty() && !isTimetableAllow.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndIsRegistrationOpenAndIsEnrollmentOpenAndIsTimetableAllowAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status and isOpen
        else if (!isOpen.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isOpen), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status and isRegistrationOpen
        else if (!isRegistrationOpen.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isRegistrationOpen), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status and isEnrollmentOpen
        else if (!isEnrollmentOpen.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isEnrollmentOpen), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status and isTimetableAllow
        else if (!isTimetableAllow.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(isTimetableAllow), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on isRegistrationOpen
        else if (!isRegistrationOpen.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(isRegistrationOpen), searchKeyWord, Boolean.valueOf(isRegistrationOpen));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsRegistrationOpenAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(isRegistrationOpen), searchKeyWord, Boolean.valueOf(isRegistrationOpen))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on isOpen
        else if (!isOpen.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(isOpen), searchKeyWord, Boolean.valueOf(isOpen));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsOpenAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(isOpen), searchKeyWord, Boolean.valueOf(isOpen))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on isOpen
        else if (!isEnrollmentOpen.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(isEnrollmentOpen), searchKeyWord, Boolean.valueOf(isEnrollmentOpen));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsEnrollmentOpenAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(isEnrollmentOpen), searchKeyWord, Boolean.valueOf(isEnrollmentOpen))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on isTimetableAllow
        else if (!isTimetableAllow.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(isTimetableAllow), searchKeyWord, Boolean.valueOf(isTimetableAllow));

            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsTimetableAllowAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(isTimetableAllow), searchKeyWord, Boolean.valueOf(isTimetableAllow))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status
        else if (!status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get all academic session without any filter
        else {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_academic-calendar_index")
    public Mono<ServerResponse> showAcademicSessionOfAcademicCalendar(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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

        //  Optional status query parameter
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .showAcademicSessionOfCalendarWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countShowAcademicSessionOfCalendarWithStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  get academic session bases on status and isOpen
        else {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .showAcademicSessionOfCalendarWithoutStatus(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countShowAcademicSessionOfCalendarWithOutStatus(searchKeyWord)
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_teacher_show")
    public Mono<ServerResponse> showAcademicSessionOfTeacher(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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

        UUID teacherUUID = UUID.fromString((serverRequest.pathVariable("teacherUUID")));

        //  Optional status query parameter
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .showAcademicSessionOfTeacherWithStatus(teacherUUID, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countShowAcademicSessionOfTeacherWithStatus(teacherUUID, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveAcademicSessionEntity> slaveAcademicSessionFlux = slaveAcademicSessionRepository
                    .showAcademicSessionOfTeacherWithoutStatus(teacherUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveAcademicSessionFlux
                    .collectList()
                    .flatMap(academicSessionEntity -> slaveAcademicSessionRepository
                            .countShowAcademicSessionOfTeacherWithOutStatus(teacherUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (academicSessionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicSessionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAcademicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                .flatMap(academicSessionEntity -> responseSuccessMsg("Record Fetched Successfully", academicSessionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));

    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_store")
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

                    AcademicSessionEntity entity = AcademicSessionEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .description(value.getFirst("description").trim())
                            .academicYear(LocalDateTime.parse(value.getFirst("academicYear"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .startDate(LocalDateTime.parse(value.getFirst("startDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .endDate(LocalDateTime.parse(value.getFirst("endDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .sessionTypeUUID(UUID.fromString(value.getFirst("sessionTypeUUID")))
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


                    // check if name is unique
                    return academicSessionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                            // check if start date uuid exists
                            .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.findByUuidAndDeletedAtIsNull(entity.getSessionTypeUUID())
                                    .flatMap(sessionTypeEntity -> {

                                        //set Academic Session Name as Fall-202X
                                        entity.setName(sessionTypeEntity.getName() + "-" + entity.getAcademicYear().getYear());

                                        // If start date is after the end date
                                        if (entity.getStartDate().isAfter(entity.getEndDate())) {
                                            return responseInfoMsg("Start Date Should be before the End Date");
                                        }

                                        if (entity.getEndDate().isBefore(entity.getStartDate())) {
                                            return responseInfoMsg("End Date Should be After the Start Date");
                                        }

                                        if (!sessionTypeEntity.getIsSpecial()) {
                                            // check if academic session's duration is overlapping
                                            return academicSessionRepository.findStartDateAndEndDateIsUnique(entity.getStartDate(), entity.getEndDate())
                                                    .flatMap(checkName -> responseInfoMsg("Academic Session already exist with in this duration"))
                                                    .switchIfEmpty(Mono.defer(() -> academicSessionRepository.save(entity)
                                                            .flatMap(academicSessionEntity -> responseSuccessMsg("Record Stored Successfully", academicSessionEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."))
                                                    ));
                                        }
                                        // if session type is special session duration can overlap
                                        else {
                                            return academicSessionRepository.save(entity)
                                                    .flatMap(academicSessionEntity -> responseSuccessMsg("Record Stored Successfully", academicSessionEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                        }
                                    }).switchIfEmpty(responseInfoMsg("Session Type record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Session Type record does not exist. Please contact developer"))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    public String getCalendarDate(JsonNode jsonNode) {
        // calendar start date
        String calendarDate = "";

        final JsonNode arrNode = jsonNode.get("data");
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                if (objNode.get("date") != null) {
                    calendarDate = objNode.get("date").toString().replaceAll("\"", "");
                }
            }
        }
        return calendarDate;
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                        .flatMap(previousAcademicEntity -> {

                            AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity
                                    .builder()
                                    .uuid(previousAcademicEntity.getUuid())
                                    .academicYear(LocalDateTime.parse(value.getFirst("academicYear"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .description(value.getFirst("description").trim())
                                    .startDate(LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .endDate(LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .sessionTypeUUID(UUID.fromString(value.getFirst("sessionTypeUUID")))
                                    .createdAt(previousAcademicEntity.getCreatedAt())
                                    .createdBy(previousAcademicEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousAcademicEntity.setDeletedBy(UUID.fromString(userId));
                            previousAcademicEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousAcademicEntity.setReqDeletedIP(reqIp);
                            previousAcademicEntity.setReqDeletedPort(reqPort);
                            previousAcademicEntity.setReqDeletedBrowser(reqBrowser);
                            previousAcademicEntity.setReqDeletedOS(reqOs);
                            previousAcademicEntity.setReqDeletedDevice(reqDevice);
                            previousAcademicEntity.setReqDeletedReferer(reqReferer);


                            //  check if name is unique
                            return academicSessionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedAcademicSessionEntity.getName(), academicSessionUUID)
                                    .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                                    // check if year uuid exists
                                    .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.findByUuidAndDeletedAtIsNull(updatedAcademicSessionEntity.getSessionTypeUUID())
                                            .flatMap(sessionTypeEntity -> {

                                                updatedAcademicSessionEntity.setName(sessionTypeEntity.getName() + "-" + updatedAcademicSessionEntity.getAcademicYear().getYear());

                                                // If start date is after the end date
                                                if (updatedAcademicSessionEntity.getStartDate().isAfter(updatedAcademicSessionEntity.getEndDate())) {
                                                    return responseInfoMsg("Start Date Should be before the End Date");
                                                }

                                                if (updatedAcademicSessionEntity.getEndDate().isBefore(updatedAcademicSessionEntity.getStartDate())) {
                                                    return responseInfoMsg("End Date Should be After the Start Date");
                                                }

                                                if (!sessionTypeEntity.getIsSpecial()) {

                                                    // check if academic session's duration is overlapping
                                                    return academicSessionRepository.findStartDateAndEndDateIsUniqueAndUuidIsNot(updatedAcademicSessionEntity.getStartDate(), updatedAcademicSessionEntity.getEndDate(), academicSessionUUID)
                                                            .flatMap(checkName -> responseInfoMsg("Academic Session already exist with in this duration"))
                                                            .switchIfEmpty(Mono.defer(() -> academicSessionRepository.save(previousAcademicEntity)
                                                                    .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                                                    .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                            ));
                                                }
                                                // if session type is special session duration can overlap
                                                else {
                                                    return academicSessionRepository.save(previousAcademicEntity)
                                                            .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                                            .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                }
                                            }).switchIfEmpty(responseInfoMsg("Session Type record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Session Type record does not exist. Please contact developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-open_update")
    public Mono<ServerResponse> isOpen(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    boolean isOpen = Boolean.parseBoolean(value.getFirst("isOpen"));
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                            .flatMap(academicSessionEntityDB -> {
                                // If isOpen is not Boolean value
                                if (isOpen != false && isOpen != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same isOpen exist in database.
                                if (((academicSessionEntityDB.getIsOpen() ? true : false) == isOpen)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity.builder()
                                        .uuid(academicSessionEntityDB.getUuid())
                                        .name(academicSessionEntityDB.getName())
                                        .academicYear(academicSessionEntityDB.getAcademicYear())
                                        .description(academicSessionEntityDB.getDescription())
                                        .isOpen(isOpen == true ? true : false)
                                        .status(academicSessionEntityDB.getStatus())
                                        .isEnrollmentOpen(academicSessionEntityDB.getIsEnrollmentOpen())
                                        .isRegistrationOpen(academicSessionEntityDB.getIsRegistrationOpen())
                                        .isTimetableAllow(academicSessionEntityDB.getIsTimetableAllow())
                                        .startDate(academicSessionEntityDB.getStartDate())
                                        .endDate(academicSessionEntityDB.getEndDate())
                                        .sessionTypeUUID(academicSessionEntityDB.getSessionTypeUUID())
                                        .createdAt(academicSessionEntityDB.getCreatedAt())
                                        .createdBy(academicSessionEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicSessionEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicSessionEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicSessionEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicSessionEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicSessionEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicSessionEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update isOpen
                                academicSessionEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicSessionEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicSessionEntityDB.setReqDeletedIP(reqIp);
                                academicSessionEntityDB.setReqDeletedPort(reqPort);
                                academicSessionEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicSessionEntityDB.setReqDeletedOS(reqOs);
                                academicSessionEntityDB.setReqDeletedDevice(reqDevice);
                                academicSessionEntityDB.setReqDeletedReferer(reqReferer);

                                return academicSessionRepository.save(academicSessionEntityDB)
                                        .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                        .flatMap(isOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isOpenUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the Open Academic Session.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Open Academic Session.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-registration-open_update")
    public Mono<ServerResponse> isRegistrationOpen(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    boolean isRegistrationOpen = Boolean.parseBoolean(value.getFirst("isRegistrationOpen"));
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                            .flatMap(academicSessionEntityDB -> {
                                // If isRegistrationOpen is not Boolean value
                                if (isRegistrationOpen != false && isRegistrationOpen != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same isRegistrationOpen exist in database.
                                if (((academicSessionEntityDB.getIsRegistrationOpen() ? true : false) == isRegistrationOpen)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity.builder()
                                        .uuid(academicSessionEntityDB.getUuid())
                                        .name(academicSessionEntityDB.getName())
                                        .description(academicSessionEntityDB.getDescription())
                                        .academicYear(academicSessionEntityDB.getAcademicYear())
                                        .isRegistrationOpen(isRegistrationOpen == true ? true : false)
                                        .status(academicSessionEntityDB.getStatus())
                                        .isOpen(academicSessionEntityDB.getIsOpen())
                                        .isEnrollmentOpen(academicSessionEntityDB.getIsEnrollmentOpen())
                                        .isTimetableAllow(academicSessionEntityDB.getIsTimetableAllow())
                                        .startDate(academicSessionEntityDB.getStartDate())
                                        .endDate(academicSessionEntityDB.getEndDate())
                                        .sessionTypeUUID(academicSessionEntityDB.getSessionTypeUUID())
                                        .createdAt(academicSessionEntityDB.getCreatedAt())
                                        .createdBy(academicSessionEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicSessionEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicSessionEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicSessionEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicSessionEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicSessionEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicSessionEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update isRegistrationOpen
                                academicSessionEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicSessionEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicSessionEntityDB.setReqDeletedIP(reqIp);
                                academicSessionEntityDB.setReqDeletedPort(reqPort);
                                academicSessionEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicSessionEntityDB.setReqDeletedOS(reqOs);
                                academicSessionEntityDB.setReqDeletedDevice(reqDevice);
                                academicSessionEntityDB.setReqDeletedReferer(reqReferer);

                                return academicSessionRepository.save(academicSessionEntityDB)
                                        .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                        .flatMap(isRegistrationOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isRegistrationOpenUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the Registration.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Registration.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-enrollment-open_update")
    public Mono<ServerResponse> isEnrollmentOpen(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    boolean isEnrollmentOpen = Boolean.parseBoolean(value.getFirst("isEnrollmentOpen"));
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                            .flatMap(academicSessionEntityDB -> {
                                // If isEnrollmentOpen is not Boolean value
                                if (isEnrollmentOpen != false && isEnrollmentOpen != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same isEnrollmentOpen exist in database.
                                if (((academicSessionEntityDB.getIsEnrollmentOpen() ? true : false) == isEnrollmentOpen)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity.builder()
                                        .uuid(academicSessionEntityDB.getUuid())
                                        .name(academicSessionEntityDB.getName())
                                        .description(academicSessionEntityDB.getDescription())
                                        .academicYear(academicSessionEntityDB.getAcademicYear())
                                        .isEnrollmentOpen(isEnrollmentOpen == true ? true : false)
                                        .status(academicSessionEntityDB.getStatus())
                                        .isOpen(academicSessionEntityDB.getIsOpen())
                                        .isRegistrationOpen(academicSessionEntityDB.getIsRegistrationOpen())
                                        .isTimetableAllow(academicSessionEntityDB.getIsTimetableAllow())
                                        .startDate(academicSessionEntityDB.getStartDate())
                                        .endDate(academicSessionEntityDB.getEndDate())
                                        .sessionTypeUUID(academicSessionEntityDB.getSessionTypeUUID())
                                        .createdAt(academicSessionEntityDB.getCreatedAt())
                                        .createdBy(academicSessionEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicSessionEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicSessionEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicSessionEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicSessionEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicSessionEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicSessionEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update isEnrollmentOpen
                                academicSessionEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicSessionEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicSessionEntityDB.setReqDeletedIP(reqIp);
                                academicSessionEntityDB.setReqDeletedPort(reqPort);
                                academicSessionEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicSessionEntityDB.setReqDeletedOS(reqOs);
                                academicSessionEntityDB.setReqDeletedDevice(reqDevice);
                                academicSessionEntityDB.setReqDeletedReferer(reqReferer);

                                return academicSessionRepository.save(academicSessionEntityDB)
                                        .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                        .flatMap(isEnrollmentOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isEnrollmentOpenUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the Enrollment.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Enrollment.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-timetable-allow_update")
    public Mono<ServerResponse> isTimetableAllow(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    boolean isTimetableAllow = Boolean.parseBoolean(value.getFirst("isTimetableAllow"));
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                            .flatMap(academicSessionEntityDB -> {
                                // If isTimetableAllow is not Boolean value
                                if (isTimetableAllow != false && isTimetableAllow != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same isTimetableAllow exist in database.
                                if (((academicSessionEntityDB.getIsTimetableAllow() ? true : false) == isTimetableAllow)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity.builder()
                                        .uuid(academicSessionEntityDB.getUuid())
                                        .name(academicSessionEntityDB.getName())
                                        .description(academicSessionEntityDB.getDescription())
                                        .academicYear(academicSessionEntityDB.getAcademicYear())
                                        .isTimetableAllow(isTimetableAllow == true ? true : false)
                                        .status(academicSessionEntityDB.getStatus())
                                        .isOpen(academicSessionEntityDB.getIsOpen())
                                        .isRegistrationOpen(academicSessionEntityDB.getIsRegistrationOpen())
                                        .isEnrollmentOpen(academicSessionEntityDB.getIsEnrollmentOpen())
                                        .startDate(academicSessionEntityDB.getStartDate())
                                        .endDate(academicSessionEntityDB.getEndDate())
                                        .sessionTypeUUID(academicSessionEntityDB.getSessionTypeUUID())
                                        .createdAt(academicSessionEntityDB.getCreatedAt())
                                        .createdBy(academicSessionEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicSessionEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicSessionEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicSessionEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicSessionEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicSessionEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicSessionEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update isTimetableAllow
                                academicSessionEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicSessionEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicSessionEntityDB.setReqDeletedIP(reqIp);
                                academicSessionEntityDB.setReqDeletedPort(reqPort);
                                academicSessionEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicSessionEntityDB.setReqDeletedOS(reqOs);
                                academicSessionEntityDB.setReqDeletedDevice(reqDevice);
                                academicSessionEntityDB.setReqDeletedReferer(reqReferer);

                                return academicSessionRepository.save(academicSessionEntityDB)
                                        .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                        .flatMap(isTimetableAllowUpdate -> responseSuccessMsg("Status Updated Successfully", isTimetableAllowUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the Timetable.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Timetable.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                            .flatMap(academicSessionEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((academicSessionEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicSessionEntity updatedAcademicSessionEntity = AcademicSessionEntity.builder()
                                        .name(academicSessionEntityDB.getName())
                                        .description(academicSessionEntityDB.getDescription())
                                        .academicYear(academicSessionEntityDB.getAcademicYear())
                                        .status(status == true ? true : false)
                                        .isOpen(academicSessionEntityDB.getIsOpen())
                                        .uuid(academicSessionEntityDB.getUuid())
                                        .startDate(academicSessionEntityDB.getStartDate())
                                        .endDate(academicSessionEntityDB.getEndDate())
                                        .sessionTypeUUID(academicSessionEntityDB.getSessionTypeUUID())
                                        .createdAt(academicSessionEntityDB.getCreatedAt())
                                        .createdBy(academicSessionEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicSessionEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicSessionEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicSessionEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicSessionEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicSessionEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicSessionEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                academicSessionEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicSessionEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicSessionEntityDB.setReqDeletedIP(reqIp);
                                academicSessionEntityDB.setReqDeletedPort(reqPort);
                                academicSessionEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicSessionEntityDB.setReqDeletedOS(reqOs);
                                academicSessionEntityDB.setReqDeletedDevice(reqDevice);
                                academicSessionEntityDB.setReqDeletedReferer(reqReferer);

                                return academicSessionRepository.save(academicSessionEntityDB)
                                        .then(academicSessionRepository.save(updatedAcademicSessionEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-sessions_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicSessionUUID)
                .flatMap(academicSessionEntity -> feeStructureRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionEntity.getUuid())
                                //checking if Academic Session exists in Fee Structures
                                .flatMap(feeStructureEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
//                        .switchIfEmpty(Mono.defer(() -> attendanceRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionEntity.getUuid())
//                                //checking if Academic Session exists in Attendances
//                                .flatMap(attendanceEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //checking if Academic Session exists in Academic Calendar
                                .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionEntity.getUuid())
                                        .flatMap(courseOfferedEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //checking if Academic Session exists in Subject Offered
                                .switchIfEmpty(Mono.defer(() -> subjectOfferedRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionEntity.getUuid())
                                        .flatMap(courseOfferedEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    academicSessionEntity.setDeletedBy(UUID.fromString(userId));
                                    academicSessionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    academicSessionEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    academicSessionEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    academicSessionEntity.setReqDeletedIP(reqIp);
                                    academicSessionEntity.setReqDeletedPort(reqPort);
                                    academicSessionEntity.setReqDeletedBrowser(reqBrowser);
                                    academicSessionEntity.setReqDeletedOS(reqOs);
                                    academicSessionEntity.setReqDeletedDevice(reqDevice);
                                    academicSessionEntity.setReqDeletedReferer(reqReferer);


                                    return academicSessionRepository.save(academicSessionEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg
                )
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
