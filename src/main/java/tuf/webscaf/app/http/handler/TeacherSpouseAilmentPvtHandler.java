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
import tuf.webscaf.app.dbContext.master.entity.AilmentEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseAilmentPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.AilmentRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSpouseAilmentPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSpouseRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAilmentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseAilmentPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "teacherSpouseAilmentPvtHandler")
public class TeacherSpouseAilmentPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSpouseAilmentPvtRepository teacherSpouseAilmentPvtRepository;

    @Autowired
    SlaveTeacherSpouseAilmentPvtRepository slaveTeacherSpouseAilmentPvtRepository;

    @Autowired
    SlaveAilmentRepository slaveAilmentRepository;

    @Autowired
    AilmentRepository ailmentRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-ailments_existing_show")
    public Mono<ServerResponse> showAilmentsAgainstTeacherSpouse(ServerRequest serverRequest) {

        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));

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
            Flux<SlaveAilmentEntity> slaveAilmentsFlux = slaveAilmentRepository
                    .existingTeacherSpouseAilmentsListWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                    .flatMap(teacherSpouseEntity -> slaveAilmentsFlux.collectList()
                            .flatMap(ailmentEntity -> slaveAilmentRepository.countExistingTeacherSpouseAilmentsRecordsWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (ailmentEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", ailmentEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Spouse Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveAilmentEntity> slaveAilmentsFlux = slaveAilmentRepository
                    .existingTeacherSpouseAilmentsList(teacherSpouseUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                    .flatMap(teacherSpouseEntity -> slaveAilmentsFlux.collectList()
                            .flatMap(ailmentEntity -> slaveAilmentRepository.countExistingTeacherSpouseAilmentsRecords(teacherSpouseUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (ailmentEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", ailmentEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Spouse Record does not exist. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-ailments_mapped_show")
    public Mono<ServerResponse> showMappedAilmentsAgainstTeacherSpouse(ServerRequest serverRequest) {

        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));

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
            Flux<SlaveAilmentEntity> slaveAilmentsFlux = slaveAilmentRepository
                    .showTeacherSpouseAilmentsListWithStatus(teacherSpouseUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,
                            directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAilmentsFlux
                    .collectList()
                    .flatMap(ailmentEntity -> slaveAilmentRepository
                            .countMappedTeacherSpouseAilmentsWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (ailmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ailmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveAilmentEntity> slaveAilmentsFlux = slaveAilmentRepository
                    .showTeacherSpouseAilmentsList(teacherSpouseUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAilmentsFlux
                    .collectList()
                    .flatMap(ailmentEntity -> slaveAilmentRepository.countMappedTeacherSpouseAilments(teacherSpouseUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (ailmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ailmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-ailments_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");
        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));

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
                .flatMap(value -> teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                        .flatMap(teacherSpouseEntity -> {

                            //getting List of Ailments From Front
                            List<String> listOfAilmentUUID = new LinkedList<>(value.get("ailmentUUID"));

                            listOfAilmentUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getHobbyUUID : listOfAilmentUUID) {
                                l_list.add(UUID.fromString(getHobbyUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return ailmentRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingAilments -> {
                                            // Ailment UUID List
                                            List<UUID> ailmentList = new ArrayList<>();

                                            for (AilmentEntity ailment : existingAilments) {
                                                ailmentList.add(ailment.getUuid());
                                            }

                                            if (!ailmentList.isEmpty()) {

                                                // ailment uuid list to show in response
                                                List<UUID> ailmentRecords = new ArrayList<>(ailmentList);

                                                List<TeacherSpouseAilmentPvtEntity> listPvt = new ArrayList<>();

                                                return teacherSpouseAilmentPvtRepository.findAllByTeacherSpouseUUIDAndAilmentUUIDInAndDeletedAtIsNull(teacherSpouseUUID, ailmentList)
                                                        .collectList()
                                                        .flatMap(teacherSpousePvtEntity -> {
                                                            for (TeacherSpouseAilmentPvtEntity pvtEntity : teacherSpousePvtEntity) {
                                                                //Removing Existing Ailment UUID in Ailment Final List to be saved that does not contain already mapped values
                                                                ailmentList.remove(pvtEntity.getAilmentUUID());
                                                            }

                                                            // iterate Ailment UUIDs for given Teacher Spouse
                                                            for (UUID ailmentUUID : ailmentList) {
                                                                TeacherSpouseAilmentPvtEntity teacherSpouseAilmentPvtEntity = TeacherSpouseAilmentPvtEntity
                                                                        .builder()
                                                                        .ailmentUUID(ailmentUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .teacherSpouseUUID(teacherSpouseUUID)
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
                                                                listPvt.add(teacherSpouseAilmentPvtEntity);
                                                            }

                                                            return teacherSpouseAilmentPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!ailmentList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", ailmentRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", ailmentRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Ailment Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Ailment Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Ailment Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Ailment First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Teacher Spouse Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-ailments_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));
        UUID ailmentUUID = UUID.fromString(serverRequest.queryParam("ailmentUUID").map(String::toString).orElse(""));
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

        return ailmentRepository.findByUuidAndDeletedAtIsNull(ailmentUUID)
                .flatMap(ailmentEntity -> teacherSpouseAilmentPvtRepository
                        .findFirstByTeacherSpouseUUIDAndAilmentUUIDAndDeletedAtIsNull(teacherSpouseUUID, ailmentUUID)
                        .flatMap(TeacherSpouseAilmentPvtEntity -> {

                            TeacherSpouseAilmentPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            TeacherSpouseAilmentPvtEntity.setDeletedBy(UUID.fromString(userId));
                            TeacherSpouseAilmentPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            TeacherSpouseAilmentPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            TeacherSpouseAilmentPvtEntity.setReqDeletedIP(reqIp);
                            TeacherSpouseAilmentPvtEntity.setReqDeletedPort(reqPort);
                            TeacherSpouseAilmentPvtEntity.setReqDeletedBrowser(reqBrowser);
                            TeacherSpouseAilmentPvtEntity.setReqDeletedOS(reqOs);
                            TeacherSpouseAilmentPvtEntity.setReqDeletedDevice(reqDevice);
                            TeacherSpouseAilmentPvtEntity.setReqDeletedReferer(reqReferer);

                            return teacherSpouseAilmentPvtRepository.save(TeacherSpouseAilmentPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", ailmentEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Ailment record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Ailment record does not exist.Please Contact Developer."));

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
