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
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianNationalityPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.NationalityRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianNationalityPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveNationalityRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianNationalityPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "teacherGuardianNationalityPvtHandler")
@Component
public class TeacherGuardianNationalityPvtHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianNationalityPvtRepository teacherGuardianNationalityPvtRepository;

    @Autowired
    SlaveTeacherGuardianNationalityPvtRepository slaveTeacherGuardianNationalityPvtRepository;

    @Autowired
    SlaveNationalityRepository slaveNationalityRepository;

    @Autowired
    NationalityRepository nationalityRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-nationalities_existing_show")
    public Mono<ServerResponse> showNationalitiesAgainstTeacherGuardian(ServerRequest serverRequest) {

        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("teacherGuardianUUID"));

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
                    .existingTeacherGuardianNationalitiesListWithStatus(teacherGuardianUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                    .flatMap(teacherGuardianEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherGuardianNationalityRecordsWithStatus(teacherGuardianUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .existingTeacherGuardianNationalitiesList(teacherGuardianUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                    .flatMap(teacherGuardianEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherGuardianNationalityRecords(teacherGuardianUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record does not exist. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-nationalities_mapped_show")
    public Mono<ServerResponse> showMappedNationalitiesAgainstTeacherGuardian(ServerRequest serverRequest) {

        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("teacherGuardianUUID"));

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
                    .showTeacherGuardianNationalitiesListWithStatus(teacherGuardianUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherGuardianNationalitiesWithStatus(teacherGuardianUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
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
                    .showTeacherGuardianNationalitiesList(teacherGuardianUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherGuardianNationalities(teacherGuardianUUID, searchKeyWord, searchKeyWord)
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

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-nationalities_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("teacherGuardianUUID"));

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
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                        .flatMap(teacherGuardianEntity -> {

                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Nationalities. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
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

                                                    List<TeacherGuardianNationalityPvtEntity> listPvt = new ArrayList<>();

                                                    return teacherGuardianNationalityPvtRepository.findAllByTeacherGuardianUUIDAndNationalityUUIDInAndDeletedAtIsNull(teacherGuardianUUID, nationalityList)
                                                            .collectList()
                                                            .flatMap(teacherGuardianPvtEntity -> {
                                                                for (TeacherGuardianNationalityPvtEntity pvtEntity : teacherGuardianPvtEntity) {
                                                                    //Removing Existing Nationality UUID in Nationality Final List to be saved that does not contain already mapped values
                                                                    nationalityList.remove(pvtEntity.getNationalityUUID());
                                                                }

                                                                // iterate Nationality UUIDs for given Teacher Guardian
                                                                for (UUID nationalityUUID : nationalityList) {
                                                                    TeacherGuardianNationalityPvtEntity teacherGuardianNationalityPvtEntity = TeacherGuardianNationalityPvtEntity
                                                                            .builder()
                                                                            .nationalityUUID(nationalityUUID)
                                                                            .uuid(UUID.randomUUID())
                                                                            .teacherGuardianUUID(teacherGuardianUUID)
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
                                                                    listPvt.add(teacherGuardianNationalityPvtEntity);
                                                                }

                                                                return teacherGuardianNationalityPvtRepository.saveAll(listPvt)
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
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Teacher Guardian Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-nationalities_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("teacherGuardianUUID"));
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
                .flatMap(nationalityEntity -> teacherGuardianNationalityPvtRepository
                        .findFirstByTeacherGuardianUUIDAndNationalityUUIDAndDeletedAtIsNull(teacherGuardianUUID, nationalityUUID)
                        .flatMap(teacherGuardianNationalityPvtEntity -> {
                            teacherGuardianNationalityPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherGuardianNationalityPvtEntity.setDeletedBy(UUID.fromString(userId));
                            teacherGuardianNationalityPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherGuardianNationalityPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherGuardianNationalityPvtEntity.setReqDeletedIP(reqIp);
                            teacherGuardianNationalityPvtEntity.setReqDeletedPort(reqPort);
                            teacherGuardianNationalityPvtEntity.setReqDeletedBrowser(reqBrowser);
                            teacherGuardianNationalityPvtEntity.setReqDeletedOS(reqOs);
                            teacherGuardianNationalityPvtEntity.setReqDeletedDevice(reqDevice);
                            teacherGuardianNationalityPvtEntity.setReqDeletedReferer(reqReferer);

                            return teacherGuardianNationalityPvtRepository.save(teacherGuardianNationalityPvtEntity)
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
