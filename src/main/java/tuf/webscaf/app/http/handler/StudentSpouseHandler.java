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
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSpouseHandler")
@Component
public class StudentSpouseHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    SlaveStudentSpouseRepository slaveStudentSpouseRepository;

    @Autowired
    StudentSpouseProfileRepository studentSpouseProfileRepository;

    @Autowired
    StudentSpouseDocumentRepository studentSpouseDocumentRepository;

    @Autowired
    StudentSpouseFinancialHistoryRepository studentSpouseFinancialHistoryRepository;

    @Autowired
    StudentSpouseJobHistoryRepository studentSpouseJobHistoryRepository;

    @Autowired
    StudentSpouseFamilyDoctorRepository studentSpouseFamilyDoctorRepository;

    @Autowired
    StudentSpouseHobbyPvtRepository studentSpouseHobbyPvtRepository;

    @Autowired
    StudentSpouseNationalityPvtRepository studentSpouseNationalityPvtRepository;

    @Autowired
    StudentSpouseAilmentPvtRepository studentSpouseAilmentPvtRepository;

    @Autowired
    StudentSpouseAddressRepository studentSpouseAddressRepository;

    @Autowired
    StudentSpouseAcademicHistoryRepository studentSpouseAcademicHistoryRepository;

    @Autowired
    StudentSpouseLanguagePvtRepository studentSpouseLanguagePvtRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @AuthHasPermission(value = "academic_api_v1_student-spouses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student UUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if both student uuid and status are present in optional query params
        if (!status.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveStudentSpouseEntity> slaveStudentSpouseEntityFlux = slaveStudentSpouseRepository
                    .findAllByStudentUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID), Boolean.valueOf(status));

            return slaveStudentSpouseEntityFlux
                    .collectList()
                    .flatMap(studentSpouseEntity -> slaveStudentSpouseRepository.countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(studentUUID), Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only student uuid is present in optional query params
        else if (!studentUUID.isEmpty()) {
            Flux<SlaveStudentSpouseEntity> slaveStudentSpouseEntityFlux = slaveStudentSpouseRepository
                    .findAllByStudentUUIDAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID));

            return slaveStudentSpouseEntityFlux
                    .collectList()
                    .flatMap(studentSpouseEntity -> slaveStudentSpouseRepository.countByStudentUUIDAndDeletedAtIsNull(UUID.fromString(studentUUID))
                            .flatMap(count ->
                            {
                                if (studentSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only status is present in optional query params
        else if (!status.isEmpty()) {
            Flux<SlaveStudentSpouseEntity> slaveStudentSpouseEntityFlux = slaveStudentSpouseRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveStudentSpouseEntityFlux
                    .collectList()
                    .flatMap(studentSpouseEntity -> slaveStudentSpouseRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional query params are present
        else {
            Flux<SlaveStudentSpouseEntity> slaveStudentSpouseEntityFlux = slaveStudentSpouseRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveStudentSpouseEntityFlux
                    .collectList()
                    .flatMap(studentSpouseEntity -> slaveStudentSpouseRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (studentSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID studentSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Fetched Successfully", studentSpouseEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_student_show")
    public Mono<ServerResponse> showByStudentUUID(ServerRequest serverRequest) {
        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));

        return slaveStudentSpouseRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Fetched Successfully", studentSpouseEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_store")
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

                    UUID studentSpouseUUID = null;
                    if ((value.containsKey("studentSpouseUUID") && (value.getFirst("studentSpouseUUID") != ""))) {
                        studentSpouseUUID = UUID.fromString(value.getFirst("studentSpouseUUID").trim());
                    }

                    UUID teacherUUID = null;
                    if ((value.containsKey("teacherUUID") && (value.getFirst("teacherUUID") != ""))) {
                        teacherUUID = UUID.fromString(value.getFirst("teacherUUID").trim());
                    }

                    StudentSpouseEntity studentSpouseEntity = StudentSpouseEntity.builder()
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
                            .teacherUUID(teacherUUID)
                            .studentSpouseUUID(studentSpouseUUID)
                            .uuid(UUID.randomUUID())
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

                    // checks if student uuid exists
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID())
                            .flatMap(studentEntity -> {

                                // if same student uuid is given as both student and student spouse's student uuid
                                if (studentSpouseEntity.getStudentUUID().equals(studentSpouseEntity.getStudentSpouseUUID())) {
                                    return responseInfoMsg("The student spouse cannot be the same as the given student");
                                }

                                // if student spouse is student and teacher
                                else if (studentSpouseEntity.getTeacherUUID() != null && studentSpouseEntity.getStudentSpouseUUID() != null) {
                                    return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getStudentSpouseUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
                                            .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getTeacherUUID())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentSpouseUUID())
                                                    .flatMap(studentRecord -> teacherRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getTeacherUUID())
                                                            .flatMap(teacherEntity -> studentSpouseRepository.save(studentSpouseEntity)
                                                                    .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                            ));
                                }

                                // if student spouse is teacher
                                else if (studentSpouseEntity.getTeacherUUID() != null) {
                                    return studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getTeacherUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))
                                            .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getTeacherUUID())
                                                    .flatMap(teacherEntity -> studentSpouseRepository.save(studentSpouseEntity)
                                                            .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                            ));
                                }

                                // if student spouse is student
                                else if (studentSpouseEntity.getStudentSpouseUUID() != null) {
                                    return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getStudentSpouseUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentSpouseUUID())
                                                    .flatMap(studentRecord -> studentSpouseRepository.save(studentSpouseEntity)
                                                            .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                            ));
                                }

                                // else store the record
                                else {
                                    return studentSpouseRepository.save(studentSpouseEntity)
                                            .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                }
                            }).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));

                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID studentSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                        .flatMap(entity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentSpouseUUID") && (value.getFirst("studentSpouseUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentSpouseUUID").trim());
                            }

                            UUID teacherUUID = null;
                            if ((value.containsKey("teacherUUID") && (value.getFirst("teacherUUID") != ""))) {
                                teacherUUID = UUID.fromString(value.getFirst("teacherUUID").trim());
                            }

                            StudentSpouseEntity updatedEntity = StudentSpouseEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
                                    .teacherUUID(teacherUUID)
                                    .studentSpouseUUID(studentUUID)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(entity.getReqCreatedIP())
                                    .reqCreatedPort(entity.getReqCreatedPort())
                                    .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                    .reqCreatedOS(entity.getReqCreatedOS())
                                    .reqCreatedDevice(entity.getReqCreatedDevice())
                                    .reqCreatedReferer(entity.getReqCreatedReferer())
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
                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //Storing Deleted Previous Entity First and Then Updated Entity
                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                    .flatMap(studentEntity -> {

                                        // if same student uuid is given as both student and student spouse's student uuid
                                        if (updatedEntity.getStudentUUID().equals(updatedEntity.getStudentSpouseUUID())) {
                                            return responseInfoMsg("The student spouse cannot be the same as the given student");
                                        }

                                        // if student spouse is student and teacher
                                        else if (updatedEntity.getTeacherUUID() != null && updatedEntity.getStudentSpouseUUID() != null) {
                                            return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getStudentSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists For Given Student"))
                                                    .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getTeacherUUID(), updatedEntity.getUuid())
                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))))
                                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSpouseUUID())
                                                            .flatMap(studentRecord -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                                                    .flatMap(teacherEntity -> studentSpouseRepository.save(entity)
                                                                            .then(studentSpouseRepository.save(updatedEntity))
                                                                            .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // if student spouse is teacher
                                        else if (updatedEntity.getTeacherUUID() != null) {
                                            return studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getTeacherUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                                            .flatMap(teacherEntity -> studentSpouseRepository.save(entity)
                                                                    .then(studentSpouseRepository.save(updatedEntity))
                                                                    .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // if student spouse is student
                                        else if (updatedEntity.getStudentSpouseUUID() != null) {
                                            return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getStudentSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
                                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSpouseUUID())
                                                            .flatMap(studentRecord -> studentSpouseRepository.save(entity)
                                                                    .then(studentSpouseRepository.save(updatedEntity))
                                                                    .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // else store the record
                                        else {
                                            return studentSpouseRepository.save(entity)
                                                    .then(studentSpouseRepository.save(updatedEntity))
                                                    .flatMap(studentSpouseEntityDB -> responseSuccessMsg("Record Updated Successfully", studentSpouseEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                        }
                                    }).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                            .flatMap(previousStudentSpouseEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStudentSpouseEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSpouseEntity updatedStudentSpouseEntity = StudentSpouseEntity.builder()
                                        .uuid(previousStudentSpouseEntity.getUuid())
                                        .studentUUID(previousStudentSpouseEntity.getStudentUUID())
                                        .studentSpouseUUID(previousStudentSpouseEntity.getStudentSpouseUUID())
                                        .teacherUUID(previousStudentSpouseEntity.getTeacherUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousStudentSpouseEntity.getCreatedAt())
                                        .createdBy(previousStudentSpouseEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStudentSpouseEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStudentSpouseEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStudentSpouseEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStudentSpouseEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStudentSpouseEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStudentSpouseEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousStudentSpouseEntity.setDeletedBy(UUID.fromString(userId));
                                previousStudentSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStudentSpouseEntity.setReqDeletedIP(reqIp);
                                previousStudentSpouseEntity.setReqDeletedPort(reqPort);
                                previousStudentSpouseEntity.setReqDeletedBrowser(reqBrowser);
                                previousStudentSpouseEntity.setReqDeletedOS(reqOs);
                                previousStudentSpouseEntity.setReqDeletedDevice(reqDevice);
                                previousStudentSpouseEntity.setReqDeletedReferer(reqReferer);

                                return studentSpouseRepository.save(previousStudentSpouseEntity)
                                        .then(studentSpouseRepository.save(updatedStudentSpouseEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                //Checks if Student Spouse Reference exists in Student Spouse Profiles
                .flatMap(studentSpouseEntity -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                        .flatMap(studentSpouseProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Spouse Reference exists in Student Spouse Documents
                        .switchIfEmpty(Mono.defer(() -> studentSpouseDocumentRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Financial History
                        .switchIfEmpty(Mono.defer(() -> studentSpouseFinancialHistoryRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Job History
                        .switchIfEmpty(Mono.defer(() -> studentSpouseJobHistoryRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentSpouseFamilyDoctorRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSpouseHobbyPvtRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSpouseNationalityPvtRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSpouseAilmentPvtRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Address
                        .switchIfEmpty(Mono.defer(() -> studentSpouseAddressRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Academic History
                        .switchIfEmpty(Mono.defer(() -> studentSpouseAcademicHistoryRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Spouse Reference exists in Student Spouse Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSpouseLanguagePvtRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .flatMap(studentSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentSpouseEntity.setDeletedBy(UUID.fromString(userId));
                            studentSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentSpouseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentSpouseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentSpouseEntity.setReqDeletedIP(reqIp);
                            studentSpouseEntity.setReqDeletedPort(reqPort);
                            studentSpouseEntity.setReqDeletedBrowser(reqBrowser);
                            studentSpouseEntity.setReqDeletedOS(reqOs);
                            studentSpouseEntity.setReqDeletedDevice(reqDevice);
                            studentSpouseEntity.setReqDeletedReferer(reqReferer);

                            return studentSpouseRepository.save(studentSpouseEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
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
