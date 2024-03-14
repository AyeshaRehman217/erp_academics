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
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingHobbyPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.HobbyRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingHobbyPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHobbyRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingHobbyPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "studentSiblingHobbyPvtHandler")
@Component
public class StudentSiblingHobbyPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingHobbyPvtRepository studentSiblingHobbyPvtRepository;

    @Autowired
    SlaveStudentSiblingHobbyPvtRepository slaveStudentSiblingHobbyPvtRepository;

    @Autowired
    SlaveHobbyRepository slaveHobbyRepository;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @AuthHasPermission(value = "academic_api_v1_student-sibling-hobbies_existing_show")
    public Mono<ServerResponse> showHobbiesAgainstStudentSibling(ServerRequest serverRequest) {

        final UUID stdSiblingUUID = UUID.fromString(serverRequest.pathVariable("studentSiblingUUID"));

        //Optional Query Parameter of Status
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
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository.
                    existingStudentSiblingHobbiesListWithStatus(stdSiblingUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntityDB -> slaveHobbyRepository.countExistingStdSiblingHobbiesRecordsWithStatus(stdSiblingUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository.
                    existingHobbyList(stdSiblingUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntityDB -> slaveHobbyRepository.countExistingStdSiblingHobbiesRecords(stdSiblingUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-hobbies_mapped_show")
    public Mono<ServerResponse> showMappedHobbiesAgainstStudentSibling(ServerRequest serverRequest) {

        final UUID studentSiblingUUID = UUID.fromString(serverRequest.pathVariable("studentSiblingUUID"));

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

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .showStudentSiblingHobbiesListWithStatus(studentSiblingUUID, Boolean.valueOf(status), searchKeyWord,
                            searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository
                            .countMappedStudentSiblingHobbiesWithStatus(studentSiblingUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .showStudentSiblingHobbiesList(studentSiblingUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countMappedStudentSiblingHobbies(studentSiblingUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-hobbies_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        final UUID studentSiblingUUID = UUID.fromString(serverRequest.pathVariable("studentSiblingUUID"));

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
                .flatMap(value -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                        .flatMap(studentSiblingEntity -> {

                            //getting List of Hobbies From Front
                            List<String> listOfHobbyUUID = new LinkedList<>(value.get("hobbyUUID"));

                            listOfHobbyUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getHobbyUUID : listOfHobbyUUID) {
                                l_list.add(UUID.fromString(getHobbyUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return hobbyRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingHobbies -> {
                                            // Hobby UUID List
                                            List<UUID> hobbyList = new ArrayList<>();

                                            for (HobbyEntity hobby : existingHobbies) {
                                                hobbyList.add(hobby.getUuid());
                                            }

                                            if (!hobbyList.isEmpty()) {

                                                // hobby uuid list to show in response
                                                List<UUID> hobbyRecords = new ArrayList<>(hobbyList);

                                                List<StudentSiblingHobbyPvtEntity> listPvt = new ArrayList<>();

                                                return studentSiblingHobbyPvtRepository.findAllByStudentSiblingUUIDAndHobbyUUIDInAndDeletedAtIsNull(studentSiblingUUID, hobbyList)
                                                        .collectList()
                                                        .flatMap(studentSiblingPvtEntity -> {
                                                            for (StudentSiblingHobbyPvtEntity pvtEntity : studentSiblingPvtEntity) {
                                                                //Removing Existing Hobby UUID in Hobby Final List to be saved that does not contain already mapped values
                                                                hobbyList.remove(pvtEntity.getHobbyUUID());
                                                            }

                                                            // iterate Hobby UUIDs for given StudentSibling
                                                            for (UUID hobbyUUID : hobbyList) {
                                                                StudentSiblingHobbyPvtEntity studentSiblingHobbyPvtEntity = StudentSiblingHobbyPvtEntity
                                                                        .builder()
                                                                        .hobbyUUID(hobbyUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .studentSiblingUUID(studentSiblingUUID)
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
                                                                listPvt.add(studentSiblingHobbyPvtEntity);
                                                            }

                                                            return studentSiblingHobbyPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!hobbyList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", hobbyRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseSuccessMsg("Record Already Exists", hobbyRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Hobby Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Hobby Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Hobby Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Hobby First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Student Sibling Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Sibling Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-hobbies_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID stdSiblingUUID = UUID.fromString(serverRequest.pathVariable("studentSiblingUUID"));
        UUID hobbyUUID = UUID.fromString(serverRequest.queryParam("hobbyUUID").map(String::toString).orElse(""));

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

        return hobbyRepository.findByUuidAndDeletedAtIsNull(hobbyUUID)
                .flatMap(hobbyEntity -> studentSiblingHobbyPvtRepository
                        .findFirstByStudentSiblingUUIDAndHobbyUUIDAndDeletedAtIsNull(stdSiblingUUID, hobbyUUID)
                        .flatMap(studentSiblingPvtEntity -> {

                            studentSiblingPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentSiblingPvtEntity.setDeletedBy(UUID.fromString(userId));
                            studentSiblingPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentSiblingPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentSiblingPvtEntity.setReqDeletedIP(reqIp);
                            studentSiblingPvtEntity.setReqDeletedPort(reqPort);
                            studentSiblingPvtEntity.setReqDeletedBrowser(reqBrowser);
                            studentSiblingPvtEntity.setReqDeletedOS(reqOs);
                            studentSiblingPvtEntity.setReqDeletedDevice(reqDevice);
                            studentSiblingPvtEntity.setReqDeletedReferer(reqReferer);

                            return studentSiblingHobbyPvtRepository.save(studentSiblingPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", hobbyEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Hobby does not exist"))
                .onErrorResume(err -> responseErrorMsg("Hobby does not exist.Please Contact Developer."));

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
