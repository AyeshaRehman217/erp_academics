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
import tuf.webscaf.app.dbContext.master.entity.HolidayEntity;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailHolidayPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.HolidayRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarDetailHolidayPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarDetailRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHolidayRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarDetailHolidayPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "academicCalendarDetailHolidayPvtHandler")
public class AcademicCalendarDetailHolidayPvtHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AcademicCalendarDetailHolidayPvtRepository academicCalendarDetailHolidayPvtRepository;

    @Autowired
    SlaveAcademicCalendarDetailHolidayPvtRepository slaveAcademicCalendarDetailHolidayPvtRepository;

    @Autowired
    SlaveHolidayRepository slaveHolidayRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    AcademicCalendarDetailRepository academicCalendarDetailRepository;

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-holidays_un-mapped_show")
    public Mono<ServerResponse> showUnMappedHolidaysAgainstAcademicCalendarDetail(ServerRequest serverRequest) {

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
            Flux<SlaveHolidayEntity> slaveHolidaysFlux = slaveHolidayRepository
                    .unmappedAcademicCalendarDetailHolidaysListWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                    .flatMap(academicCalendarDetailEntity -> slaveHolidaysFlux.collectList()
                            .flatMap(holidayEntity -> slaveHolidayRepository.countExistingAcademicCalendarDetailHolidaysRecordsWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (holidayEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", holidayEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Academic Calendar Detail Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveHolidayEntity> slaveHolidaysFlux = slaveHolidayRepository
                    .unmappedAcademicCalendarDetailHolidaysList(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                    .flatMap(academicCalendarDetailEntity -> slaveHolidaysFlux.collectList()
                            .flatMap(holidayEntity -> slaveHolidayRepository.countExistingAcademicCalendarDetailHolidaysRecords(academicCalendarDetailUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (holidayEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", holidayEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Academic Calendar Detail Record does not exist. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-holidays_mapped_show")
    public Mono<ServerResponse> showMappedHolidaysAgainstAcademicCalendarDetail(ServerRequest serverRequest) {

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
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveHolidayEntity> slaveHolidaysFlux = slaveHolidayRepository
                    .showAcademicCalendarDetailHolidaysListWithStatus(academicCalendarDetailUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,
                            directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHolidaysFlux
                    .collectList()
                    .flatMap(holidayEntity -> slaveHolidayRepository
                            .countMappedAcademicCalendarDetailHolidaysWithStatus(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (holidayEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", holidayEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveHolidayEntity> slaveHolidaysFlux = slaveHolidayRepository
                    .showAcademicCalendarDetailHolidaysList(academicCalendarDetailUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHolidaysFlux
                    .collectList()
                    .flatMap(holidayEntity -> slaveHolidayRepository.countMappedAcademicCalendarDetailHolidays(academicCalendarDetailUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (holidayEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", holidayEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-holidays_store")
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

                            //getting List of Holidays From Front
                            List<String> listOfHolidayUUID = new LinkedList<>(value.get("holidayUUID"));

                            listOfHolidayUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getHolidayUUID : listOfHolidayUUID) {
                                l_list.add(UUID.fromString(getHolidayUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return holidayRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingHolidays -> {
                                            // Holiday UUID List
                                            List<UUID> holidayList = new ArrayList<>();

                                            for (HolidayEntity holiday : existingHolidays) {
                                                holidayList.add(holiday.getUuid());
                                            }

                                            if (!holidayList.isEmpty()) {

                                                // holiday uuid list to show in response
                                                List<UUID> holidayRecords = new ArrayList<>(holidayList);

                                                List<AcademicCalendarDetailHolidayPvtEntity> listPvt = new ArrayList<>();

                                                return academicCalendarDetailHolidayPvtRepository.findAllByAcademicCalendarDetailUUIDAndHolidayUUIDInAndDeletedAtIsNull(academicCalendarDetailUUID, holidayList)
                                                        .collectList()
                                                        .flatMap(academicCalendarDetailPvtEntity -> {
                                                            for (AcademicCalendarDetailHolidayPvtEntity pvtEntity : academicCalendarDetailPvtEntity) {
                                                                //Removing Existing Holiday UUID in Holiday Final List to be saved that does not contain already mapped values
                                                                holidayList.remove(pvtEntity.getHolidayUUID());
                                                            }

                                                            // iterate Holiday UUIDs for given Academic Calendar Detail
                                                            for (UUID holidayUUID : holidayList) {
                                                                AcademicCalendarDetailHolidayPvtEntity academicCalendarDetailHolidayPvtEntity = AcademicCalendarDetailHolidayPvtEntity
                                                                        .builder()
                                                                        .holidayUUID(holidayUUID)
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
                                                                listPvt.add(academicCalendarDetailHolidayPvtEntity);
                                                            }

                                                            return academicCalendarDetailHolidayPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!holidayList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", holidayRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", holidayRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Holiday Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Holiday Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Holiday Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Holiday First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Academic Calendar Detail Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Academic Calendar Detail Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-detail-holidays_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID academicCalendarDetailUUID = UUID.fromString(serverRequest.pathVariable("academicCalendarDetailUUID"));
        UUID holidayUUID = UUID.fromString(serverRequest.queryParam("holidayUUID").map(String::toString).orElse("").trim());
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

        return holidayRepository.findByUuidAndDeletedAtIsNull(holidayUUID)
                .flatMap(holidayEntity -> academicCalendarDetailHolidayPvtRepository
                        .findFirstByAcademicCalendarDetailUUIDAndHolidayUUIDAndDeletedAtIsNull(academicCalendarDetailUUID, holidayUUID)
                        .flatMap(AcademicCalendarDetailHolidayPvtEntity -> {

                            AcademicCalendarDetailHolidayPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            AcademicCalendarDetailHolidayPvtEntity.setDeletedBy(UUID.fromString(userId));
                            AcademicCalendarDetailHolidayPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            AcademicCalendarDetailHolidayPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedIP(reqIp);
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedPort(reqPort);
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedBrowser(reqBrowser);
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedOS(reqOs);
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedDevice(reqDevice);
                            AcademicCalendarDetailHolidayPvtEntity.setReqDeletedReferer(reqReferer);

                            return academicCalendarDetailHolidayPvtRepository.save(AcademicCalendarDetailHolidayPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", holidayEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Holiday record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Holiday record does not exist.Please Contact Developer."));

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
