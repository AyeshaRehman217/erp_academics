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
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseNationalityPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.NationalityRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSpouseNationalityPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherSpouseRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveNationalityRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseNationalityPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "teacherSpouseNationalityPvtHandler")
@Component
public class TeacherSpouseNationalityPvtHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSpouseNationalityPvtRepository teacherSpouseNationalityPvtRepository;

    @Autowired
    SlaveTeacherSpouseNationalityPvtRepository slaveTeacherSpouseNationalityPvtRepository;

    @Autowired
    SlaveNationalityRepository slaveNationalityRepository;

    @Autowired
    NationalityRepository nationalityRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-nationalities_existing_show")
    public Mono<ServerResponse> showNationalitiesAgainstTeacherSpouse(ServerRequest serverRequest) {

        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));

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
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .existingTeacherSpouseNationalitiesListWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                    .flatMap(teacherSpouseEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherSpouseNationalityRecordsWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Spouse  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Spouse  Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .existingTeacherSpouseNationalitiesList(teacherSpouseUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                    .flatMap(teacherSpouseEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherSpouseNationalityRecords(teacherSpouseUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Spouse  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Spouse  Record does not exist. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-nationalities_mapped_show")
    public Mono<ServerResponse> showMappedNationalitiesAgainstTeacherSpouse(ServerRequest serverRequest) {

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

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .showTeacherSpouseNationalitiesListWithStatus(teacherSpouseUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherSpouseNationalitiesWithStatus(teacherSpouseUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));

        } else {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .showTeacherSpouseNationalitiesList(teacherSpouseUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherSpouseNationalities(teacherSpouseUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-nationalities_store")
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

                            //getting List of Nationalities From Front
                            List<String> listOfNationalityUUID = new LinkedList<>(value.get("nationalityUUID"));

                            listOfNationalityUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getNationalityUUID : listOfNationalityUUID) {
                                l_list.add(UUID.fromString(getNationalityUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return nationalityRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingNationalities -> {
                                            // Nationality UUID List
                                            List<UUID> nationalityList = new ArrayList<>();

                                            for (NationalityEntity nationality : existingNationalities) {
                                                nationalityList.add(nationality.getUuid());
                                            }

                                            if (!nationalityList.isEmpty()) {

                                                // nationality uuid list to show in response
                                                List<UUID> nationalityRecords = new ArrayList<>(nationalityList);

                                                List<TeacherSpouseNationalityPvtEntity> listPvt = new ArrayList<>();

                                                return teacherSpouseNationalityPvtRepository.findAllByTeacherSpouseUUIDAndNationalityUUIDInAndDeletedAtIsNull(teacherSpouseUUID, nationalityList)
                                                        .collectList()
                                                        .flatMap(teacherSpousePvtEntity -> {
                                                            for (TeacherSpouseNationalityPvtEntity pvtEntity : teacherSpousePvtEntity) {
                                                                //Removing Existing Nationality UUID in Nationality Final List to be saved that does not contain already mapped values
                                                                nationalityList.remove(pvtEntity.getNationalityUUID());
                                                            }

                                                            // iterate Nationality UUIDs for given Teacher Spouse
                                                            for (UUID nationalityUUID : nationalityList) {
                                                                TeacherSpouseNationalityPvtEntity teacherSpouseNationalityPvtEntity = TeacherSpouseNationalityPvtEntity
                                                                        .builder()
                                                                        .nationalityUUID(nationalityUUID)
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
                                                                listPvt.add(teacherSpouseNationalityPvtEntity);
                                                            }

                                                            return teacherSpouseNationalityPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!nationalityList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", nationalityRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", nationalityRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Nationality Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Nationality Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Nationality Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Nationality First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Teacher Spouse Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-nationalities_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));
        UUID nationalityUUID = UUID.fromString(serverRequest.queryParam("nationalityUUID").map(String::toString).orElse(""));
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

        return nationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                .flatMap(nationalityEntity -> teacherSpouseNationalityPvtRepository
                        .findFirstByTeacherSpouseUUIDAndNationalityUUIDAndDeletedAtIsNull(teacherSpouseUUID, nationalityUUID)
                        .flatMap(teacherSpouseNationalityPvtEntity -> {
                            teacherSpouseNationalityPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherSpouseNationalityPvtEntity.setDeletedBy(UUID.fromString(userId));
                            teacherSpouseNationalityPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherSpouseNationalityPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherSpouseNationalityPvtEntity.setReqDeletedIP(reqIp);
                            teacherSpouseNationalityPvtEntity.setReqDeletedPort(reqPort);
                            teacherSpouseNationalityPvtEntity.setReqDeletedBrowser(reqBrowser);
                            teacherSpouseNationalityPvtEntity.setReqDeletedOS(reqOs);
                            teacherSpouseNationalityPvtEntity.setReqDeletedDevice(reqDevice);
                            teacherSpouseNationalityPvtEntity.setReqDeletedReferer(reqReferer);

                            return teacherSpouseNationalityPvtRepository.save(teacherSpouseNationalityPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", nationalityEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Nationality does not exist"))
                .onErrorResume(err -> responseErrorMsg("Nationality does not exist.Please Contact Developer."));

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
