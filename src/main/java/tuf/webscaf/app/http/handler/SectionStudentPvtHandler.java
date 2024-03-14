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
import tuf.webscaf.app.dbContext.master.entity.RegistrationEntity;
import tuf.webscaf.app.dbContext.master.entity.SectionStudentPvtEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSectionStudentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "sectionStudentPvtHandler")
public class SectionStudentPvtHandler {

    @Autowired
    SectionStudentPvtRepository sectionStudentPvtRepository;

    @Autowired
    SlaveSectionStudentPvtRepository slaveSectionStudentPvtRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    SlaveStudentRepository slaveStudentRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    CampusCourseRepository campusCourseRepository;

    @Autowired
    CustomResponse appresponse;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_section-students_un-mapped_show")
    public Mono<ServerResponse> showUnMappedStudentsAgainstSection(ServerRequest serverRequest) {

//        UUID sectionUUID = UUID.fromString(serverRequest.pathVariable("sectionUUID"));
        UUID courseOfferedUUID = UUID.fromString(serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse("").trim());

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveSectionStudentPvtRepository
                    .unMappedStudentListWithStatus(courseOfferedUUID, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countUnMappedRecordsWithStatus(courseOfferedUUID, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to Read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveSectionStudentPvtRepository
                    .unMappedStudentList(courseOfferedUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countUnMappedRecords(courseOfferedUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to Read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_section-students_mapped_show")
    public Mono<ServerResponse> showMappedStudentsAgainstSection(ServerRequest serverRequest) {

        UUID sectionUUID = UUID.fromString(serverRequest.pathVariable("sectionUUID"));

        UUID courseOfferedUUID = UUID.fromString(serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse("").trim());

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
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveSectionStudentPvtRepository
                    .showMappedStudentListAgainstSectionWithStatus(sectionUUID, courseOfferedUUID, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentsFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countMappedStudentAgainstSectionWithStatus(sectionUUID, courseOfferedUUID, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentsFlux = slaveSectionStudentPvtRepository
                    .showMappedStudentListAgainstSection(sectionUUID, courseOfferedUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentsFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countMappedStudentAgainstSection(sectionUUID, courseOfferedUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        }
    }


    @AuthHasPermission(value = "academic_api_v1_section-students_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        UUID sectionUUID = UUID.fromString(serverRequest.pathVariable("sectionUUID"));

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
                .flatMap(value -> sectionRepository.findByUuidAndDeletedAtIsNull(sectionUUID)
                                .flatMap(sectionEntity -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(sectionEntity.getCourseOfferedUUID())
                                                .flatMap(courseOfferedEntity -> {

                                                    //getting List of Student UUIDs From Front
                                                    List<String> listOfStudentUUID = new LinkedList<>(value.get("studentUUID"));

                                                    listOfStudentUUID.removeIf(s -> s.equals(""));

                                                    List<UUID> l_list = new ArrayList<>();
                                                    for (String getStudentUUID : listOfStudentUUID) {
                                                        l_list.add(UUID.fromString(getStudentUUID));
                                                    }

                                                    if (!l_list.isEmpty()) {
                                                        return studentRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                                                .collectList()
                                                                .flatMap(unMappedStudents -> {
                                                                    // Student UUID List
                                                                    List<UUID> studentList = new ArrayList<>();

                                                                    for (StudentEntity student : unMappedStudents) {
                                                                        studentList.add(student.getUuid());
                                                                    }

                                                                    if (!studentList.isEmpty()) {
                                                                        //check if students are registered
                                                                        return registrationRepository.findAllByStudentUUIDInAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNull(studentList, courseOfferedEntity.getCampusCourseUUID(), courseOfferedEntity.getAcademicSessionUUID())
                                                                                .collectList()
                                                                                .flatMap(registration -> {

                                                                                    // check if all students are registered in given course offered
                                                                                    if (registration.size() != studentList.size()) {
                                                                                        return responseInfoMsg("The Entered Student List are not registered in given course offered");
                                                                                    }

                                                                                    //student uuid list to show in response
                                                                                    List<UUID> studentRecords = new ArrayList<>(studentList);

                                                                                    List<SectionStudentPvtEntity> listPvt = new ArrayList<>();

                                                                                    return sectionStudentPvtRepository.countBySectionUUIDAndDeletedAtIsNull(sectionUUID)
                                                                                            .flatMap(mappedStudentCount -> sectionStudentPvtRepository.findAllBySectionUUIDAndStudentUUIDInAndDeletedAtIsNull(sectionUUID, studentList)
                                                                                                    .collectList()
                                                                                                    .flatMap(sectionPvtEntity -> {
                                                                                                        for (SectionStudentPvtEntity pvtEntity : sectionPvtEntity) {
                                                                                                            //Removing UnMapped Student UUID in Student Final List to be saved that does not contain already mapped values
                                                                                                            studentList.remove(pvtEntity.getStudentUUID());
                                                                                                        }

                                                                                                        if (sectionEntity.getMax() != null) {
                                                                                                            if ((mappedStudentCount + studentList.size()) > sectionEntity.getMax()) {
                                                                                                                return responseInfoMsg("Students Against a Section Can't Exceed its Max Limit");
                                                                                                            }
                                                                                                        }

                                                                                                        //  check student is unique against section
                                                                                                        return sectionStudentPvtRepository.findFirstByStudentUUIDInAndDeletedAtIsNull(studentList)
                                                                                                                .flatMap(getStudent -> responseInfoMsg("This Student is already added in Section"))
                                                                                                                .switchIfEmpty(Mono.defer(() -> {
                                                                                                                    // iterate Student UUIDs for given Section
                                                                                                                    for (UUID studentUUID : studentList) {
                                                                                                                        SectionStudentPvtEntity sectionStudentPvtEntity = SectionStudentPvtEntity
                                                                                                                                .builder()
                                                                                                                                .studentUUID(studentUUID)
                                                                                                                                .uuid(UUID.randomUUID())
                                                                                                                                .sectionUUID(sectionUUID)
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
                                                                                                                        listPvt.add(sectionStudentPvtEntity);
                                                                                                                    }

                                                                                                                    return sectionStudentPvtRepository.saveAll(listPvt)
                                                                                                                            .collectList()
                                                                                                                            .flatMap(groupList -> {

                                                                                                                                if (!studentList.isEmpty()) {
                                                                                                                                    return responseSuccessMsg("Record Stored Successfully", studentRecords)
                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                } else {
                                                                                                                                    return responseSuccessMsg("Record Already Exists", studentRecords);
                                                                                                                                }

                                                                                                                            }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                                                                                }));

                                                                                                    })
                                                                                            );
                                                                                });
//
                                                                    } else {
                                                                        return responseInfoMsg("Student Record does not exist");
                                                                    }
                                                                }).switchIfEmpty(responseInfoMsg("The Entered Student Does not exist."))
                                                                .onErrorResume(ex -> responseErrorMsg("The Entered Student Does not exist.Please Contact Developer."));
                                                    } else {
                                                        return responseInfoMsg("Select Student First");
                                                    }
                                                })
                                ).switchIfEmpty(responseInfoMsg("Section Record does not exist"))
                                .onErrorResume(err -> responseInfoMsg("Section Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_section-students_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID sectionUUID = UUID.fromString(serverRequest.pathVariable("sectionUUID"));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim());
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
                .flatMap(studentEntity -> sectionStudentPvtRepository
                        .findFirstBySectionUUIDAndStudentUUIDAndDeletedAtIsNull(sectionUUID, studentUUID)
                        .flatMap(sectionStudentPvtEntity -> {

                            sectionStudentPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            sectionStudentPvtEntity.setDeletedBy(UUID.fromString(userId));
                            sectionStudentPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            sectionStudentPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            sectionStudentPvtEntity.setReqDeletedIP(reqIp);
                            sectionStudentPvtEntity.setReqDeletedPort(reqPort);
                            sectionStudentPvtEntity.setReqDeletedBrowser(reqBrowser);
                            sectionStudentPvtEntity.setReqDeletedOS(reqOs);
                            sectionStudentPvtEntity.setReqDeletedDevice(reqDevice);
                            sectionStudentPvtEntity.setReqDeletedReferer(reqReferer);

                            return sectionStudentPvtRepository.save(sectionStudentPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", studentEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Record does not exist.Please Contact Developer."));

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
