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
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGroupStudentPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGroupStudentPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGroupRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGroupStudentPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "studentGroupStudentPvtHandler")
public class StudentGroupStudentPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGroupStudentPvtRepository studentGroupStudentPvtRepository;

    @Autowired
    SlaveStudentGroupStudentPvtRepository slaveStudentGroupStudentPvtRepository;

    @Autowired
    SlaveStudentRepository slaveStudentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentGroupRepository studentGroupRepository;

    @AuthHasPermission(value = "academic_api_v1_student-group-students_un-mapped_show")
    public Mono<ServerResponse> showUnMappedStudentsAgainstStudentGroup(ServerRequest serverRequest) {

        final UUID studentGroupUUID = UUID.fromString(serverRequest.pathVariable("studentGroupUUID"));

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveStudentRepository
                    .showUnMappedStudentGroupStudentsListWithStatus(studentGroupUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                    .flatMap(studentGroupEntity -> slaveStudentsFlux.collectList()
                            .flatMap(studentEntity -> slaveStudentRepository.countUnMappedStudentGroupStudentsRecordsWithStatus(studentGroupUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (studentEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("StudentGroup Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("StudentGroup Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveStudentRepository
                    .showUnMappedStudentGroupStudentsList(studentGroupUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                    .flatMap(studentGroupEntity -> slaveStudentsFlux.collectList()
                            .flatMap(studentEntity -> slaveStudentRepository.countUnMappedStudentGroupStudentsRecords(studentGroupUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (studentEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("StudentGroup Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("StudentGroup Record does not exist. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-group-students_mapped_show")
    public Mono<ServerResponse> showMappedStudentsAgainstStudentGroup(ServerRequest serverRequest) {

        final UUID studentGroupUUID = UUID.fromString(serverRequest.pathVariable("studentGroupUUID"));

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

        // Optional Query Param of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveStudentRepository
                    .showMappedStudentGroupStudentsListWithStatus(studentGroupUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,
                            directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentsFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository
                            .countMappedStudentGroupStudentsWithStatus(studentGroupUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveStudentRepository
                    .showMappedStudentGroupStudentsList(studentGroupUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentsFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countMappedStudentGroupStudents(studentGroupUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-group-students_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");
        final UUID studentGroupUUID = UUID.fromString(serverRequest.pathVariable("studentGroupUUID"));

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

        return serverRequest.formData()
                .flatMap(value -> studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                        .flatMap(studentGroupEntity -> {

                            //getting List of Students From Front
                            List<String> listOfStudentUUID = new LinkedList<>(value.get("studentUUID"));

                            listOfStudentUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getHobbyUUID : listOfStudentUUID) {
                                l_list.add(UUID.fromString(getHobbyUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return studentRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingStudents -> {

                                            // Student UUID List
                                            List<UUID> studentList = new ArrayList<>();

                                            for (StudentEntity student : existingStudents) {
                                                studentList.add(student.getUuid());
                                            }

                                            if (!studentList.isEmpty()) {

                                                // student uuid list to show in response
                                                List<UUID> studentRecords = new ArrayList<>(studentList);

                                                List<StudentGroupStudentPvtEntity> listPvt = new ArrayList<>();

                                                return studentGroupStudentPvtRepository.countByStudentGroupUUIDAndDeletedAtIsNull(studentGroupUUID)
                                                        .flatMap(mappedStudentCount -> studentGroupStudentPvtRepository.findAllByStudentGroupUUIDAndStudentUUIDInAndDeletedAtIsNull(studentGroupUUID, studentList)
                                                                .collectList()
                                                                .flatMap(studentGroupPvtEntity -> {

                                                                    for (StudentGroupStudentPvtEntity pvtEntity : studentGroupPvtEntity) {
                                                                        //Removing Existing Student UUID in Student Final List to be saved that does not contain already mapped values
                                                                        studentList.remove(pvtEntity.getStudentUUID());
                                                                    }

                                                                    if (studentGroupEntity.getMax() != null) {
                                                                        if ((mappedStudentCount + studentList.size()) > studentGroupEntity.getMax()) {
                                                                            return responseInfoMsg("Students Against a Student Group Can't Exceed its Max Limit");
                                                                        }
                                                                    }

                                                                    // iterate Student UUIDs for given StudentGroup
                                                                    for (UUID studentUUID : studentList) {

                                                                        StudentGroupStudentPvtEntity studentGroupStudentPvtEntity = StudentGroupStudentPvtEntity
                                                                                .builder()
                                                                                .studentUUID(studentUUID)
                                                                                .uuid(UUID.randomUUID())
                                                                                .studentGroupUUID(studentGroupUUID)
                                                                                .createdBy(UUID.fromString(userId))
                                                                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                                .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                                .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                                .reqCreatedIP(reqIp)
                                                                                .reqCreatedPort(reqPort)
                                                                                .reqCreatedBrowser(reqBrowser)
                                                                                .reqCreatedOS(reqOs)
                                                                                .reqCreatedDevice(reqDevice)
                                                                                .reqCreatedReferer(reqReferer)
                                                                                .build();

                                                                        listPvt.add(studentGroupStudentPvtEntity);
                                                                    }

                                                                    return studentGroupStudentPvtRepository.saveAll(listPvt)
                                                                            .collectList()
                                                                            .flatMap(groupList -> {

                                                                                if (!studentList.isEmpty()) {
                                                                                    return responseSuccessMsg("Record Stored Successfully", studentRecords)
                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                } else {
                                                                                    return responseInfoMsg("Record Already Exists", studentRecords);
                                                                                }

                                                                            }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                                })
                                                        );
                                            } else {
                                                return responseInfoMsg("Student Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Student Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Student Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Student First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Student Group Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Student Group Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-group-students_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID studentGroupUUID = UUID.fromString(serverRequest.pathVariable("studentGroupUUID"));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));
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

        return studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                .flatMap(studentEntity -> studentGroupStudentPvtRepository
                        .findFirstByStudentGroupUUIDAndStudentUUIDAndDeletedAtIsNull(studentGroupUUID, studentUUID)
                        .flatMap(StudentGroupStudentPvtEntity -> {

                            StudentGroupStudentPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            StudentGroupStudentPvtEntity.setDeletedBy(UUID.fromString(userId));
                            StudentGroupStudentPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            StudentGroupStudentPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            StudentGroupStudentPvtEntity.setReqDeletedIP(reqIp);
                            StudentGroupStudentPvtEntity.setReqDeletedPort(reqPort);
                            StudentGroupStudentPvtEntity.setReqDeletedBrowser(reqBrowser);
                            StudentGroupStudentPvtEntity.setReqDeletedOS(reqOs);
                            StudentGroupStudentPvtEntity.setReqDeletedDevice(reqDevice);
                            StudentGroupStudentPvtEntity.setReqDeletedReferer(reqReferer);

                            return studentGroupStudentPvtRepository.save(StudentGroupStudentPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", studentEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student record does not exist.Please Contact Developer."));

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

    public Mono<ServerResponse> responseInfoMsg(String msg, Object entity) {
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
                Mono.just(entity)

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
