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
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherSpouseHandler")
@Component
public class TeacherSpouseHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    SlaveTeacherSpouseRepository slaveTeacherSpouseRepository;

    @Autowired
    TeacherSpouseProfileRepository teacherSpouseProfileRepository;

    @Autowired
    TeacherSpouseDocumentRepository teacherSpouseDocumentRepository;

    @Autowired
    TeacherSpouseAcademicHistoryRepository teacherSpouseAcademicHistoryRepository;

    @Autowired
    TeacherSpouseJobHistoryRepository teacherSpouseJobHistoryRepository;

    @Autowired
    TeacherSpouseFinancialHistoryRepository teacherSpouseFinancialHistoryRepository;

    @Autowired
    TeacherSpouseAddressRepository teacherSpouseAddressRepository;

    @Autowired
    TeacherSpouseFamilyDoctorRepository teacherSpouseFamilyDoctorRepository;

    @Autowired
    TeacherSpouseAilmentPvtRepository teacherSpouseAilmentPvtRepository;

    @Autowired
    TeacherSpouseHobbyPvtRepository teacherSpouseHobbyPvtRepository;

    @Autowired
    TeacherSpouseNationalityPvtRepository teacherSpouseNationalityPvtRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentRepository studentRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher UUID
        String teacherUUID = serverRequest.queryParam("teacherUUID").map(String::toString).orElse("").trim();

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


        // if both teacher uuid and status are present in optional query params
        if (!status.isEmpty() && !teacherUUID.isEmpty()) {
            Flux<SlaveTeacherSpouseEntity> slaveTeacherSpouseEntityFlux = slaveTeacherSpouseRepository
                    .findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID), Boolean.valueOf(status));

            return slaveTeacherSpouseEntityFlux
                    .collectList()
                    .flatMap(teacherSpouseEntity -> slaveTeacherSpouseRepository.countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(teacherUUID), Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only teacher uuid is present in optional query params
        else if (!teacherUUID.isEmpty()) {
            Flux<SlaveTeacherSpouseEntity> slaveTeacherSpouseEntityFlux = slaveTeacherSpouseRepository
                    .findAllByTeacherUUIDAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID));

            return slaveTeacherSpouseEntityFlux
                    .collectList()
                    .flatMap(teacherSpouseEntity -> slaveTeacherSpouseRepository.countByTeacherUUIDAndDeletedAtIsNull(UUID.fromString(teacherUUID))
                            .flatMap(count ->
                            {
                                if (teacherSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only status is present in optional query params
        else if (!status.isEmpty()) {
            Flux<SlaveTeacherSpouseEntity> slaveTeacherSpouseEntityFlux = slaveTeacherSpouseRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherSpouseEntityFlux
                    .collectList()
                    .flatMap(teacherSpouseEntity -> slaveTeacherSpouseRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional query params are present
        else {
            Flux<SlaveTeacherSpouseEntity> slaveTeacherSpouseEntityFlux = slaveTeacherSpouseRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherSpouseEntityFlux
                    .collectList()
                    .flatMap(teacherSpouseEntity -> slaveTeacherSpouseRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherSpouseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherSpouseEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_teacher_show")
    public Mono<ServerResponse> showByTeacherUUID(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("teacherUUID"));

        return slaveTeacherSpouseRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherSpouseEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_store")
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

                    UUID studentUUID = null;
                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                    }

                    UUID teacherSpouseUUID = null;
                    if ((value.containsKey("teacherSpouseUUID") && (value.getFirst("teacherSpouseUUID") != ""))) {
                        teacherSpouseUUID = UUID.fromString(value.getFirst("teacherSpouseUUID").trim());
                    }

                    TeacherSpouseEntity teacherSpouseEntity = TeacherSpouseEntity.builder()
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                            .studentUUID(studentUUID)
                            .teacherSpouseUUID(teacherSpouseUUID)
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

                    // checks if teacher uuid exists
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID())
                            .flatMap(teacherEntity -> {

                                // if same teacher uuid is given as both teacher and teacher spouse's teacher uuid
                                if (teacherSpouseEntity.getTeacherUUID().equals(teacherSpouseEntity.getTeacherSpouseUUID())) {
                                    return responseInfoMsg("The teacher spouse cannot be the same as the given teacher");
                                }

                                // if teacher spouse is student and teacher
                                else if (teacherSpouseEntity.getStudentUUID() != null && teacherSpouseEntity.getTeacherSpouseUUID() != null) {
                                    return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getTeacherSpouseUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getStudentUUID())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getStudentUUID())
                                                    .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherSpouseUUID())
                                                            .flatMap(teacherRecord -> teacherSpouseRepository.save(teacherSpouseEntity)
                                                                    .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                            ));
                                }

                                // if teacher spouse is student
                                else if (teacherSpouseEntity.getStudentUUID() != null) {
                                    return teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getStudentUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getStudentUUID())
                                                    .flatMap(studentEntity -> teacherSpouseRepository.save(teacherSpouseEntity)
                                                            .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                            ));
                                }

                                // if teacher spouse is teacher
                                else if (teacherSpouseEntity.getTeacherSpouseUUID() != null) {
                                    return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getTeacherSpouseUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                            .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherSpouseUUID())
                                                    .flatMap(teacherRecord -> teacherSpouseRepository.save(teacherSpouseEntity)
                                                            .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                            ));
                                }

                                // else store the record
                                else {
                                    return teacherSpouseRepository.save(teacherSpouseEntity)
                                            .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                }
                            }).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));

                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                        .flatMap(entity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                            }

                            UUID teacherUUID = null;
                            if ((value.containsKey("teacherSpouseUUID") && (value.getFirst("teacherSpouseUUID") != ""))) {
                                teacherUUID = UUID.fromString(value.getFirst("teacherSpouseUUID").trim());
                            }

                            TeacherSpouseEntity updatedEntity = TeacherSpouseEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                                    .studentUUID(studentUUID)
                                    .teacherSpouseUUID(teacherUUID)
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
                            return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                    .flatMap(teacherEntity -> {

                                        // if same teacher uuid is given as both teacher and teacher spouse's teacher uuid
                                        if (updatedEntity.getTeacherUUID().equals(updatedEntity.getTeacherSpouseUUID())) {
                                            return responseInfoMsg("The teacher spouse cannot be the same as the given teacher");
                                        }

                                        // if teacher spouse is student and teacher
                                        if (updatedEntity.getStudentUUID() != null && updatedEntity.getTeacherSpouseUUID() != null) {
                                            return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getTeacherSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))))
                                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                            .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherSpouseUUID())
                                                                    .flatMap(teacherRecord -> teacherSpouseRepository.save(entity)
                                                                            .then(teacherSpouseRepository.save(updatedEntity))
                                                                            .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // if teacher spouse is student
                                        else if (updatedEntity.getStudentUUID() != null) {
                                            return teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))
                                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                            .flatMap(studentEntity -> teacherSpouseRepository.save(entity)
                                                                    .then(teacherSpouseRepository.save(updatedEntity))
                                                                    .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // if teacher spouse is teacher
                                        else if (updatedEntity.getTeacherSpouseUUID() != null) {
                                            return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getTeacherSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherSpouseUUID())
                                                            .flatMap(teacherRecord -> teacherSpouseRepository.save(entity)
                                                                    .then(teacherSpouseRepository.save(updatedEntity))
                                                                    .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSpouseEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                                    ));
                                        }

                                        // else store the record
                                        else {
                                            return teacherSpouseRepository.save(entity)
                                                    .then(teacherSpouseRepository.save(updatedEntity))
                                                    .flatMap(teacherSpouseEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherSpouseEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                        }
                                    }).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                            .flatMap(teacherSpouseEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherSpouseEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherSpouseEntity updatedTeacherSpouseEntity = TeacherSpouseEntity.builder()
                                        .uuid(teacherSpouseEntityDB.getUuid())
                                        .teacherUUID(teacherSpouseEntityDB.getTeacherUUID())
                                        .teacherSpouseUUID(teacherSpouseEntityDB.getTeacherSpouseUUID())
                                        .studentUUID(teacherSpouseEntityDB.getStudentUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherSpouseEntityDB.getCreatedAt())
                                        .createdBy(teacherSpouseEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherSpouseEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherSpouseEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherSpouseEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherSpouseEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherSpouseEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherSpouseEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherSpouseEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherSpouseEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherSpouseEntityDB.setReqDeletedIP(reqIp);
                                teacherSpouseEntityDB.setReqDeletedPort(reqPort);
                                teacherSpouseEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherSpouseEntityDB.setReqDeletedOS(reqOs);
                                teacherSpouseEntityDB.setReqDeletedDevice(reqDevice);
                                teacherSpouseEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherSpouseRepository.save(teacherSpouseEntityDB)
                                        .then(teacherSpouseRepository.save(updatedTeacherSpouseEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-spouses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                //Checks if Teacher Spouse Reference exists in Teacher Spouse Profiles
                .flatMap(teacherSpouseEntity -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                        .flatMap(teacherSpouseProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Documents
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseDocumentRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseAcademicHistoryRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseFinancialHistoryRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Job History
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseJobHistoryRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseAddressRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseFamilyDoctorRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseHobbyPvtRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseAilmentPvtRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Spouse Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseNationalityPvtRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherSpouseNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Spouse Reference exists in Teacher Guardian
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findFirstByGuardianUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .flatMap(teacherGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherSpouseEntity.setDeletedBy(UUID.fromString(userId));
                            teacherSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherSpouseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherSpouseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherSpouseEntity.setReqDeletedIP(reqIp);
                            teacherSpouseEntity.setReqDeletedPort(reqPort);
                            teacherSpouseEntity.setReqDeletedBrowser(reqBrowser);
                            teacherSpouseEntity.setReqDeletedOS(reqOs);
                            teacherSpouseEntity.setReqDeletedDevice(reqDevice);
                            teacherSpouseEntity.setReqDeletedReferer(reqReferer);

                            return teacherSpouseRepository.save(teacherSpouseEntity)
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
