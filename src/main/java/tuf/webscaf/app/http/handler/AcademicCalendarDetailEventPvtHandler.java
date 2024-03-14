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
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailEventPvtEntity;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarEventEntity;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarDetailEventPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarDetailRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarEventRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarDetailEventPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarEventRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "academicCalendarDetailEventPvtHandler")
public class AcademicCalendarDetailEventPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AcademicCalendarDetailEventPvtRepository academicCalendarDetailEventPvtRepository;

    @Autowired
    SlaveAcademicCalendarDetailEventPvtRepository slaveAcademicCalendarDetailEventPvtRepository;

    @Autowired
    SlaveAcademicCalendarEventRepository slaveAcademicCalendarEventRepository;

    @Autowired
    AcademicCalendarEventRepository academicCalendarEventRepository;

    @Autowired
    AcademicCalendarDetailRepository academicCalendarDetailRepository;

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-events_un-mapped_show")
    public Mono<ServerResponse> showUnMappedEventsAgainstAcademicCalendarDetail(ServerRequest serverRequest) {

        final UUID academicCalendarDetailUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarDetailUUID"));

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
            Flux<SlaveAcademicCalendarEventEntity> slaveAcademicCalendarEventsFlux = slaveAcademicCalendarEventRepository
                    .unmappedAcademicCalendarDetailEventsListWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarEventsFlux.collectList()
                            .flatMap(academicCalendarEventEntity -> slaveAcademicCalendarEventRepository.countExistingAcademicCalendarDetailEventsRecordsWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (academicCalendarEventEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEventEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Academic Calendar Detail Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveAcademicCalendarEventEntity> slaveAcademicCalendarEventsFlux = slaveAcademicCalendarEventRepository
                    .unmappedAcademicCalendarDetailEventsList(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarEventsFlux.collectList()
                            .flatMap(academicCalendarEventEntity -> slaveAcademicCalendarEventRepository.countExistingAcademicCalendarDetailEventsRecords(academicCalendarDetailUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (academicCalendarEventEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEventEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Academic Calendar Detail Record does not exist. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-events_mapped_show")
    public Mono<ServerResponse> showMappedEventsAgainstAcademicCalendarDetail(ServerRequest serverRequest) {

        final UUID academicCalendarDetailUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarDetailUUID"));

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
            Flux<SlaveAcademicCalendarEventEntity> slaveAcademicCalendarEventsFlux = slaveAcademicCalendarEventRepository
                    .showAcademicCalendarDetailEventsListWithStatus(academicCalendarDetailUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,
                            directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarEventsFlux
                    .collectList()
                    .flatMap(academicCalendarEventEntity -> slaveAcademicCalendarEventRepository
                            .countMappedAcademicCalendarDetailEventsWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicCalendarEventEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", academicCalendarEventEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveAcademicCalendarEventEntity> slaveAcademicCalendarEventsFlux = slaveAcademicCalendarEventRepository
                    .showAcademicCalendarDetailEventsList(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarEventsFlux
                    .collectList()
                    .flatMap(academicCalendarEventEntity -> slaveAcademicCalendarEventRepository.countMappedAcademicCalendarDetailEvents(academicCalendarDetailUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarEventEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", academicCalendarEventEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-events_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");
        final UUID academicCalendarDetailUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarDetailUUID"));

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
                .flatMap(value -> academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                        .flatMap(academicCalendarDetailEntity -> {

                            //getting List of Academic Calendar Events From Front
                            List<String> listOfAcademicCalendarEventUUID = new LinkedList<>(value.get("academicCalendarEventUUID"));

                            listOfAcademicCalendarEventUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getAcademicCalendarEventUUID : listOfAcademicCalendarEventUUID) {
                                l_list.add(UUID.fromString(getAcademicCalendarEventUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return academicCalendarEventRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingAcademicCalendarEvents -> {
                                            // Academic Calendar Event UUID List
                                            List<UUID> academicCalendarEventList = new ArrayList<>();

                                            for (AcademicCalendarEventEntity academicCalendarEvent : existingAcademicCalendarEvents) {
                                                academicCalendarEventList.add(academicCalendarEvent.getUuid());
                                            }

                                            if (!academicCalendarEventList.isEmpty()) {

                                                // academic calendar event uuid list to show in response
                                                List<UUID> academicCalendarEventRecords = new ArrayList<>(academicCalendarEventList);

                                                List<AcademicCalendarDetailEventPvtEntity> listPvt = new ArrayList<>();

                                                return academicCalendarDetailEventPvtRepository.findAllByAcademicCalendarDetailUUIDAndAcademicCalendarEventUUIDInAndDeletedAtIsNull(academicCalendarDetailUUID, academicCalendarEventList)
                                                        .collectList()
                                                        .flatMap(academicCalendarDetailPvtEntity -> {
                                                            for (AcademicCalendarDetailEventPvtEntity pvtEntity : academicCalendarDetailPvtEntity) {
                                                                //Removing Existing Academic Calendar Event UUID in Academic Calendar Event Final List to be saved that does not contain already mapped values
                                                                academicCalendarEventList.remove(pvtEntity.getAcademicCalendarEventUUID());
                                                            }

                                                            // iterate Academic Calendar Event UUIDs for given Academic Calendar Detail
                                                            for (UUID academicCalendarEventUUID : academicCalendarEventList) {
                                                                AcademicCalendarDetailEventPvtEntity academicCalendarDetailEventPvtEntity = AcademicCalendarDetailEventPvtEntity
                                                                        .builder()
                                                                        .academicCalendarEventUUID(academicCalendarEventUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .academicCalendarDetailUUID(academicCalendarDetailUUID)
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
                                                                listPvt.add(academicCalendarDetailEventPvtEntity);
                                                            }

                                                            return academicCalendarDetailEventPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!academicCalendarEventList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", academicCalendarEventRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", academicCalendarEventRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Academic Calendar Event Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Academic Calendar Event Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Academic Calendar Event Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Academic Calendar Event First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Academic Calendar Detail Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-events_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID academicCalendarDetailUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarDetailUUID"));
        UUID academicCalendarEventUUID = UUID.fromString(serverRequest.queryParam("academicCalendarEventUUID").map(String::toString).orElse("").trim());
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

        return academicCalendarEventRepository.findByUuidAndDeletedAtIsNull(academicCalendarEventUUID)
                .flatMap(academicCalendarEventEntity -> academicCalendarDetailEventPvtRepository
                        .findFirstByAcademicCalendarDetailUUIDAndAcademicCalendarEventUUIDAndDeletedAtIsNull(academicCalendarDetailUUID, academicCalendarEventUUID)
                        .flatMap(AcademicCalendarDetailEventPvtEntity -> {

                            AcademicCalendarDetailEventPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            AcademicCalendarDetailEventPvtEntity.setDeletedBy(UUID.fromString(userId));
                            AcademicCalendarDetailEventPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            AcademicCalendarDetailEventPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedIP(reqIp);
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedPort(reqPort);
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedBrowser(reqBrowser);
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedOS(reqOs);
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedDevice(reqDevice);
                            AcademicCalendarDetailEventPvtEntity.setReqDeletedReferer(reqReferer);

                            return academicCalendarDetailEventPvtRepository.save(AcademicCalendarDetailEventPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", academicCalendarEventEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Academic Calendar Event record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Academic Calendar Event record does not exist.Please Contact Developer."));

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
