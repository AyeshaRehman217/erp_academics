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
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingHobbyPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.HobbyRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSiblingHobbyPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSiblingRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHobbyRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSiblingHobbyPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "teacherSiblingHobbyPvtHandler")
public class TeacherSiblingHobbyPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSiblingHobbyPvtRepository teacherSiblingHobbyPvtRepository;

    @Autowired
    SlaveTeacherSiblingHobbyPvtRepository slaveTeacherSiblingHobbyPvtRepository;

    @Autowired
    SlaveHobbyRepository slaveHobbyRepository;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-hobbies_existing_show")
    public Mono<ServerResponse> showHobbiesAgainstTeacherSibling(ServerRequest serverRequest) {

        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("teacherSiblingUUID"));

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
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .existingTeacherSiblingHobbiesListWithStatus(teacherSiblingUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,  directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                    .flatMap(teacherSiblingEntity -> slaveHobbiesFlux.collectList()
                            .flatMap(hobbyEntity -> slaveHobbyRepository.countExistingTeacherSiblingHobbiesRecordsWithStatus(teacherSiblingUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (hobbyEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Sibling  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Sibling  Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .existingTeacherSiblingHobbiesList(teacherSiblingUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                    .flatMap(teacherSiblingEntity -> slaveHobbiesFlux.collectList()
                            .flatMap(hobbyEntity -> slaveHobbyRepository.countExistingTeacherSiblingHobbiesRecords(teacherSiblingUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (hobbyEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Sibling  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Sibling  Record does not exist. Please contact developer."));
        }
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-hobbies_mapped_show")
    public Mono<ServerResponse> showMappedHobbiesAgainstTeacherSibling(ServerRequest serverRequest) {

        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("teacherSiblingUUID"));

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
                    .showTeacherSiblingHobbiesListWithStatus(teacherSiblingUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countMappedTeacherSiblingHobbiesWithStatus(teacherSiblingUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .showTeacherSiblingHobbiesList(teacherSiblingUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countMappedTeacherSiblingHobbies(teacherSiblingUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-hobbies_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("teacherSiblingUUID"));

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
                .flatMap(value -> teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                        .flatMap(teacherSiblingEntity -> {

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

                                                List<TeacherSiblingHobbyPvtEntity> listPvt = new ArrayList<>();

                                                return teacherSiblingHobbyPvtRepository.findAllByTeacherSiblingUUIDAndHobbyUUIDInAndDeletedAtIsNull(teacherSiblingUUID, hobbyList)
                                                        .collectList()
                                                        .flatMap(teacherSiblingPvtEntity -> {
                                                            for (TeacherSiblingHobbyPvtEntity pvtEntity : teacherSiblingPvtEntity) {
                                                                //Removing Existing Hobby UUID in Hobby Final List to be saved that does not contain already mapped values
                                                                hobbyList.remove(pvtEntity.getHobbyUUID());
                                                            }

                                                            // iterate Hobby UUIDs for given Teacher Sibling
                                                            for (UUID hobbyUUID : hobbyList) {
                                                                TeacherSiblingHobbyPvtEntity teacherSiblingHobbyPvtEntity = TeacherSiblingHobbyPvtEntity
                                                                        .builder()
                                                                        .hobbyUUID(hobbyUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .teacherSiblingUUID(teacherSiblingUUID)
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
                                                                listPvt.add(teacherSiblingHobbyPvtEntity);
                                                            }

                                                            return teacherSiblingHobbyPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!hobbyList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", hobbyRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", hobbyRecords);
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
                        }).switchIfEmpty(responseInfoMsg("Teacher Sibling Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Teacher Sibling Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-hobbies_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("teacherSiblingUUID"));
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
                .flatMap(hobbyEntity -> teacherSiblingHobbyPvtRepository
                        .findFirstByTeacherSiblingUUIDAndHobbyUUIDAndDeletedAtIsNull(teacherSiblingUUID, hobbyUUID)
                        .flatMap(teacherSiblingHobbyPvtEntity -> {
                            teacherSiblingHobbyPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherSiblingHobbyPvtEntity.setDeletedBy(UUID.fromString(userId));
                            teacherSiblingHobbyPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherSiblingHobbyPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherSiblingHobbyPvtEntity.setReqDeletedIP(reqIp);
                            teacherSiblingHobbyPvtEntity.setReqDeletedPort(reqPort);
                            teacherSiblingHobbyPvtEntity.setReqDeletedBrowser(reqBrowser);
                            teacherSiblingHobbyPvtEntity.setReqDeletedOS(reqOs);
                            teacherSiblingHobbyPvtEntity.setReqDeletedDevice(reqDevice);
                            teacherSiblingHobbyPvtEntity.setReqDeletedReferer(reqReferer);

                            return teacherSiblingHobbyPvtRepository.save(teacherSiblingHobbyPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", hobbyEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Hobby record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Hobby record does not exist.Please Contact Developer."));

    }

    public Mono<ServerResponse> responseErrorMsg(String msg) {
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
                entity
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
