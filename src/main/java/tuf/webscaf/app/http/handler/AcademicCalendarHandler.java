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
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarEntity;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarSemesterEntity;
import tuf.webscaf.app.dbContext.master.entity.SemesterEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Tag(name = "academicCalendarHandler")
@Component
public class AcademicCalendarHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    AcademicCalendarRepository academicCalendarRepository;

    @Autowired
    SlaveAcademicCalendarRepository slaveAcademicCalendarRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    AcademicCalendarDetailRepository academicCalendarDetailRepository;

    @Autowired
    CourseLevelRepository courseLevelRepository;

    @Autowired
    AcademicCalendarSemesterRepository academicCalendarSemesterRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

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

//      Optional Query Parameter
        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicCalendarEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarRepository
                    .findAllByNameContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(academicSessionUUID), Boolean.valueOf(status));

            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarRepository
                            .countByNameContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(academicSessionUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!academicSessionUUID.isEmpty()) {
            Flux<SlaveAcademicCalendarEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarRepository
                    .findAllByNameContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(academicSessionUUID), searchKeyWord, UUID.fromString(academicSessionUUID));

            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarRepository
                            .countByNameContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(academicSessionUUID), searchKeyWord, UUID.fromString(academicSessionUUID))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveAcademicCalendarEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveAcademicCalendarEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID academicCalendarUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAcademicCalendarRepository.findByUuidAndDeletedAtIsNull(academicCalendarUUID)
                .flatMap(academicCalendarEntity -> responseSuccessMsg("Record Fetched Successfully", academicCalendarEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));

    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_store")
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

                    AcademicCalendarEntity academicCalendarEntity = AcademicCalendarEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .courseLevelUUID(UUID.fromString(value.getFirst("courseLevelUUID")))
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

                    //getting List of semester UUID From Front
                    List<String> semesterList = new ArrayList<>(value.get("semesterUUID"));

                    //remove empty space from list
                    semesterList.removeIf(s -> s.equals(""));

                    List<UUID> parsedSemesterUUID = new ArrayList<>();
                    for (String getSemesterUUID : semesterList) {
                        parsedSemesterUUID.add(UUID.fromString(getSemesterUUID));
                    }

                    // check academic session exist
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getAcademicSessionUUID())
                            // check course level exist
                            .flatMap(academicSessionEntity -> courseLevelRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getCourseLevelUUID())
                                    // check academic Calendar name is unique
                                    .flatMap(courseLevelEntity -> academicCalendarRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(academicCalendarEntity.getName())
                                            .flatMap(checkNameAlreadyExist -> responseInfoMsg("Name already Exist"))
                                            // check academic Calendar is unique against course level and academic session
                                            .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.checkAcademicCalendarIsUnique(academicCalendarEntity.getAcademicSessionUUID(), academicCalendarEntity.getCourseLevelUUID(), parsedSemesterUUID)
                                                    .flatMap(checkCalenderIsUnique -> responseInfoMsg("Calendar already Exist Against Course Level and Academic Session"))))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                //create a new list to add String of UUID's
                                                List<UUID> semesterUUIDList = new ArrayList<>();
                                                for (String getSemesterUUID : semesterList) {
                                                    semesterUUIDList.add(UUID.fromString(getSemesterUUID));
                                                }

                                                if (!semesterUUIDList.isEmpty()) {
                                                    // check semester exist
                                                    return semesterRepository.findAllByUuidInAndDeletedAtIsNull(semesterUUIDList)
                                                            .collectList()
                                                            .flatMap(savedSemesterEntities -> {

                                                                // Semester UUID List
                                                                List<UUID> finalSemesterUUIDsList = new ArrayList<>();

                                                                for (SemesterEntity semester : savedSemesterEntities) {
                                                                    finalSemesterUUIDsList.add(semester.getUuid());
                                                                }

                                                                if (!finalSemesterUUIDsList.isEmpty()) {

                                                                    // semester uuid list to show in response
                                                                    List<UUID> semesterRecords = new ArrayList<>();

                                                                    // Create AcademicCalendarSemesterEntities list
                                                                    List<AcademicCalendarSemesterEntity> academicCalendarSemesterEntityList = new ArrayList<>();

                                                                    return academicCalendarSemesterRepository.findAllByAcademicCalendarUUIDAndSemesterUUIDInAndDeletedAtIsNull(academicCalendarEntity.getUuid(), finalSemesterUUIDsList)
                                                                            .collectList()
                                                                            .flatMap(academicCalendarSemesterEntities -> {

                                                                                for (AcademicCalendarSemesterEntity academicCalendarSemester : academicCalendarSemesterEntities) {
                                                                                    // Removing Existing savedSemesterUUID from semesterUUIDList
                                                                                    finalSemesterUUIDsList.remove(academicCalendarSemester.getSemesterUUID());
                                                                                }

                                                                                // iterate Semester UUIDs for given Academic Calendar
                                                                                for (UUID semesterUUID : finalSemesterUUIDsList) {

                                                                                    AcademicCalendarSemesterEntity academicCalendarSemesterEntity = AcademicCalendarSemesterEntity
                                                                                            .builder()
                                                                                            .uuid(UUID.randomUUID())
                                                                                            .academicCalendarUUID(academicCalendarEntity.getUuid())
                                                                                            .semesterUUID(semesterUUID)
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

                                                                                    academicCalendarSemesterEntityList.add(academicCalendarSemesterEntity);
                                                                                }

                                                                                return academicCalendarRepository.save(academicCalendarEntity)
                                                                                        .then(academicCalendarSemesterRepository.saveAll(academicCalendarSemesterEntityList)
                                                                                                .collectList())
                                                                                        .flatMap(savedAcademicCalendarEntity -> responseSuccessMsg("Record Stored Successfully", academicCalendarEntity))
                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));

                                                                            });
                                                                } else {
                                                                    return responseInfoMsg("Semester Record does not exist");
                                                                }
                                                            }).switchIfEmpty(responseInfoMsg("Semester Record does not exist."))
                                                            .onErrorResume(ex -> responseErrorMsg("Semester Record does not exist.Please Contact Developer."));
                                                } else {
                                                    return responseInfoMsg("Select Semester First");
                                                }
                                            }))
                                    ).switchIfEmpty(responseInfoMsg("Course Level record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course Level record does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID academicCalendarUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(academicCalendarUUID)
                        .flatMap(previousAcademicEntity -> {

                            AcademicCalendarEntity updatedAcademicCalendarEntity = AcademicCalendarEntity.builder()
                                    .uuid(previousAcademicEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .description(value.getFirst("description").trim())
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                    .courseLevelUUID(UUID.fromString(value.getFirst("courseLevelUUID").trim()))
                                    .createdAt(previousAcademicEntity.getCreatedAt())
                                    .createdBy(previousAcademicEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
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
                            previousAcademicEntity.setDeletedBy(UUID.fromString(userId));
                            previousAcademicEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousAcademicEntity.setReqDeletedIP(reqIp);
                            previousAcademicEntity.setReqDeletedPort(reqPort);
                            previousAcademicEntity.setReqDeletedBrowser(reqBrowser);
                            previousAcademicEntity.setReqDeletedOS(reqOs);
                            previousAcademicEntity.setReqDeletedDevice(reqDevice);
                            previousAcademicEntity.setReqDeletedReferer(reqReferer);

                            return academicCalendarRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarEntity.getName(), updatedAcademicCalendarEntity.getUuid())
                                    .flatMap(academicCalendarEntity -> responseInfoMsg("Name Already Exist"))
                                    // check if academic session uuid exists
                                    .switchIfEmpty(Mono.defer(() -> academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarEntity.getAcademicSessionUUID())
                                            //check if entered Course Level Exists or not
                                            .flatMap(academicSessionEntity -> courseLevelRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarEntity.getCourseLevelUUID())
                                                    .flatMap(courseLevelEntity -> {
                                                                //getting List of semester UUID From Front
                                                                List<String> semesterList = new LinkedList<>(value.get("semesterUUID"));

                                                                //remove empty space from list
                                                                semesterList.removeIf(s -> s.equals(""));

                                                                //create a new list to add String of UUID's (Getting Semester UUID List from Front)
                                                                List<UUID> l_list = new ArrayList<>();
                                                                for (String getSemesterUUID : semesterList) {
                                                                    l_list.add(UUID.fromString(getSemesterUUID));
                                                                }

                                                                if (!l_list.isEmpty()) {
                                                                    return semesterRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                                                            .collectList()
                                                                            .flatMap(savedSemesterEntities -> {
                                                                                // Creating a list to add semester UUID's that exist in Semester Table
                                                                                List<UUID> existingSemesterListUUID = new ArrayList<>();

                                                                                for (SemesterEntity semester : savedSemesterEntities) {
                                                                                    existingSemesterListUUID.add(semester.getUuid());
                                                                                }

                                                                                if (!existingSemesterListUUID.isEmpty()) {

                                                                                    List<AcademicCalendarSemesterEntity> listPvt = new ArrayList<>();

                                                                                    //iterating over the Calendar Semester Entity to check if the entered semester list already exists against the academic Calendar UUID
                                                                                    return academicCalendarRepository.checkAcademicCalendarIsUniqueAndAcademicCalendarIsNot(updatedAcademicCalendarEntity.getAcademicSessionUUID(), updatedAcademicCalendarEntity.getCourseLevelUUID(), existingSemesterListUUID, academicCalendarUUID)
                                                                                            .flatMap(checkMsg -> responseInfoMsg("The entered semester list already exists against the Course level and Academic Calendar"))
                                                                                            .switchIfEmpty(Mono.defer(() -> academicCalendarSemesterRepository.findAllByAcademicCalendarUUIDAndDeletedAtIsNull(academicCalendarUUID)
                                                                                                    .collectList()
                                                                                                    .flatMap(previousCalendarSemesterEntities -> {

                                                                                                        for (AcademicCalendarSemesterEntity facadeEntity : previousCalendarSemesterEntities) {
                                                                                                            //Deleting Previous Record and Creating a New One Based on UUID
                                                                                                            facadeEntity.setDeletedBy(UUID.fromString(userId));
                                                                                                            facadeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                            facadeEntity.setReqDeletedIP(reqIp);
                                                                                                            facadeEntity.setReqDeletedPort(reqPort);
                                                                                                            facadeEntity.setReqDeletedBrowser(reqBrowser);
                                                                                                            facadeEntity.setReqDeletedOS(reqOs);
                                                                                                            facadeEntity.setReqDeletedDevice(reqDevice);
                                                                                                            facadeEntity.setReqDeletedReferer(reqReferer);
                                                                                                        }

                                                                                                        //Creating New Academic Calendar Semester Entity
                                                                                                        for (UUID semesterUUID : existingSemesterListUUID) {

                                                                                                            AcademicCalendarSemesterEntity academicCalendarSemesterEntity = AcademicCalendarSemesterEntity
                                                                                                                    .builder()
                                                                                                                    .academicCalendarUUID(academicCalendarUUID)
                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                    .semesterUUID(semesterUUID)
                                                                                                                    .createdAt(previousAcademicEntity.getCreatedAt())
                                                                                                                    .createdBy(previousAcademicEntity.getCreatedBy())
                                                                                                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
                                                                                                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
                                                                                                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
                                                                                                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
                                                                                                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
                                                                                                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
                                                                                                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                                                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                                                                    .updatedBy(UUID.fromString(userId))
                                                                                                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                                                                    .reqUpdatedIP(reqIp)
                                                                                                                    .reqUpdatedPort(reqPort)
                                                                                                                    .reqUpdatedBrowser(reqBrowser)
                                                                                                                    .reqUpdatedOS(reqOs)
                                                                                                                    .reqUpdatedDevice(reqDevice)
                                                                                                                    .reqUpdatedReferer(reqReferer)
                                                                                                                    .build();

                                                                                                            listPvt.add(academicCalendarSemesterEntity);
                                                                                                        }

                                                                                                        //check if semester is unique against the academic calendar and course level
                                                                                                        return academicCalendarRepository.save(previousAcademicEntity)
                                                                                                                .then(academicCalendarRepository.save(updatedAcademicCalendarEntity))
                                                                                                                .then(academicCalendarSemesterRepository.saveAll(previousCalendarSemesterEntities)
                                                                                                                        .collectList())
                                                                                                                .flatMap(saveUpdatedEntity -> academicCalendarSemesterRepository.saveAll(listPvt)
                                                                                                                        .collectList()
                                                                                                                        .flatMap(updatedEntityList -> responseSuccessMsg("Record Updated Successfully", updatedAcademicCalendarEntity))
                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                                                                    })));
                                                                                } else {
                                                                                    return responseInfoMsg("Semester Record does not exist");
                                                                                }
                                                                            }).switchIfEmpty(responseInfoMsg("List of Semester Records Does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Semester Records Does not exist.Please Contact Developer."));
                                                                } else {
                                                                    return responseInfoMsg("Select Semesters First");
                                                                }
                                                            }
                                                    ).switchIfEmpty(responseInfoMsg("Course Level record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Course Level record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_academic-calendars_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID academicCalendarUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(academicCalendarUUID)
//                        .flatMap(previousAcademicEntity -> {
//
//                            UUID courseLevelUUID = null;
//                            if ((value.getFirst("courseLevelUUID")) != null && (value.getFirst("courseLevelUUID") != "")) {
//                                courseLevelUUID = UUID.fromString(value.getFirst("courseLevelUUID"));
//                            }
//
//                            AcademicCalendarEntity updatedAcademicCalendarEntity = AcademicCalendarEntity.builder()
//                                    .uuid(previousAcademicEntity.getUuid())
//                                    .name(value.getFirst("name").trim())
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .description(value.getFirst("description").trim())
//                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
//                                    .courseLevelUUID(UUID.fromString(value.getFirst("courseLevelUUID").trim()))
//                                    .createdAt(previousAcademicEntity.getCreatedAt())
//                                    .createdBy(previousAcademicEntity.getCreatedBy())
//                                    .updatedBy(UUID.fromString(userId))
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
//                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
//                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
//                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
//                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
//                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
//                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                    .reqUpdatedIP(reqIp)
//                                    .reqUpdatedPort(reqPort)
//                                    .reqUpdatedBrowser(reqBrowser)
//                                    .reqUpdatedOS(reqOs)
//                                    .reqUpdatedDevice(reqDevice)
//                                    .reqUpdatedReferer(reqReferer)
//                                    .build();
//
//                            //Deleting Previous Record and Creating a New One Based on UUID
//                            previousAcademicEntity.setDeletedBy(UUID.fromString(userId));
//                            previousAcademicEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                            previousAcademicEntity.setReqDeletedIP(reqIp);
//                            previousAcademicEntity.setReqDeletedPort(reqPort);
//                            previousAcademicEntity.setReqDeletedBrowser(reqBrowser);
//                            previousAcademicEntity.setReqDeletedOS(reqOs);
//                            previousAcademicEntity.setReqDeletedDevice(reqDevice);
//                            previousAcademicEntity.setReqDeletedReferer(reqReferer);
//
//                            // check if academic calendar name already exists
//                            UUID finalCourseLevelUUID = courseLevelUUID;
//                            return academicCalendarRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarEntity.getName(), updatedAcademicCalendarEntity.getUuid())
//                                    .flatMap(academicCalendarEntity -> responseInfoMsg("Name Already Exist"))
//                                    // check course level is unique against this academic session
//                                    .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findFirstByAcademicSessionUUIDAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarEntity.getAcademicSessionUUID(), updatedAcademicCalendarEntity.getCourseLevelUUID(), academicCalendarUUID)
//                                            .flatMap(academicSessionEntity -> responseInfoMsg("Calendar already Exist Against Course Level"))))
//                                    // check if academic session uuid exists
//                                    .switchIfEmpty(Mono.defer(() -> {
//                                                if (finalCourseLevelUUID != null) {
//                                                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarEntity.getAcademicSessionUUID())
//                                                            .flatMap(academicSessionEntity -> courseLevelRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarEntity.getCourseLevelUUID())
//                                                                    .flatMap(courseLevelEntity -> academicCalendarRepository.save(previousAcademicEntity)
//                                                                            .then(academicCalendarRepository.save(updatedAcademicCalendarEntity))
//                                                                            .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
//                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
//                                                                    ).switchIfEmpty(responseInfoMsg("Course Level record does not exist"))
//                                                                    .onErrorResume(ex -> responseErrorMsg("Course Level record does not exist. Please contact developer"))
//                                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
//                                                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"));
//                                                } else {
//                                                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarEntity.getAcademicSessionUUID())
//                                                            .flatMap(academicSessionEntity -> academicCalendarRepository.save(previousAcademicEntity)
//                                                                    .then(academicCalendarRepository.save(updatedAcademicCalendarEntity))
//                                                                    .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
//                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
//                                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
//                                                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"));
//                                                }
//                                            }
//                                    ));
//                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
//    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID academicCalendarUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return academicCalendarRepository.findByUuidAndDeletedAtIsNull(academicCalendarUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicCalendarEntity entity = AcademicCalendarEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .description(val.getDescription())
                                        .academicSessionUUID(val.getAcademicSessionUUID())
                                        .courseLevelUUID(val.getCourseLevelUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(val.getCreatedAt())
                                        .createdBy(val.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(val.getReqCreatedIP())
                                        .reqCreatedPort(val.getReqCreatedPort())
                                        .reqCreatedBrowser(val.getReqCreatedBrowser())
                                        .reqCreatedOS(val.getReqCreatedOS())
                                        .reqCreatedDevice(val.getReqCreatedDevice())
                                        .reqCreatedReferer(val.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return academicCalendarRepository.save(val)
                                        .then(academicCalendarRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_status_show")
    public Mono<ServerResponse> showActiveAcademicCalendar(ServerRequest serverRequest) {
        return academicCalendarRepository.findByStatusAndDeletedAtIsNull(true)
                .flatMap(academicCalendarEntity -> responseSuccessMsg("Record Fetched Successfully", academicCalendarEntity))
                .switchIfEmpty(responseInfoMsg("Currently No Academic Calendar is Active"))
                .onErrorResume(ex -> responseErrorMsg("No Academic Calendar is Active Currently.Please Contact Developer."));

    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendars_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID academicCalendarUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return academicCalendarRepository.findByUuidAndDeletedAtIsNull(academicCalendarUUID)
                .flatMap(academicCalendarEntity -> {

                    academicCalendarEntity.setDeletedBy(UUID.fromString(userId));
                    academicCalendarEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    academicCalendarEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    academicCalendarEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    academicCalendarEntity.setReqDeletedIP(reqIp);
                    academicCalendarEntity.setReqDeletedPort(reqPort);
                    academicCalendarEntity.setReqDeletedBrowser(reqBrowser);
                    academicCalendarEntity.setReqDeletedOS(reqOs);
                    academicCalendarEntity.setReqDeletedDevice(reqDevice);
                    academicCalendarEntity.setReqDeletedReferer(reqReferer);

                    // check if academic calendar reference exists in academic calendar detail
                    return academicCalendarDetailRepository.findFirstByAcademicCalendarUUIDAndDeletedAtIsNull(academicCalendarEntity.getUuid())
                            .flatMap(studentProfileAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                            .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.save(academicCalendarEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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