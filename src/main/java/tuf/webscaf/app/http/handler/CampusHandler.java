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
import tuf.webscaf.app.dbContext.master.entity.CampusEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCampusEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCampusRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "campusHandler")
@Component
public class CampusHandler {
    @Autowired
    CustomResponse appresponse;
    @Autowired
    CampusRepository campusRepository;
    @Autowired
    SlaveCampusRepository slaveCampusRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    FeeStructureRepository feeStructureRepository;
    @Autowired
    NotificationDetailRepository notificationDetailRepository;
    @Autowired
    CampusCourseOfferedPvtRepository campusCourseOfferedPvtRepository;
    @Autowired
    RegistrationRepository registrationRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    TimetableCreationRepository timetableCreationRepository;
    @Autowired
    ClassroomRepository classroomRepository;
    @Autowired
    TeacherOutlineRepository teacherOutlineRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    ApiCallService apiCallService;
    @Value("${server.zone}")
    private String zone;
    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialUri;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialModuleUri;

    @AuthHasPermission(value = "academic_api_v1_campuses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Company UUID
        String companyUUID = serverRequest.queryParam("companyUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if optional parameters of status and company uuid are present
        if (!status.isEmpty() && !companyUUID.isEmpty()) {
            Flux<SlaveCampusEntity> slaveCampusFlux = slaveCampusRepository
                    .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(companyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(companyUUID), Boolean.valueOf(status));

            return slaveCampusFlux
                    .collectList()
                    .flatMap(campusEntity -> slaveCampusRepository
                            .countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(companyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(companyUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (campusEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", campusEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of company uuid is present
        else if (!companyUUID.isEmpty()) {
            Flux<SlaveCampusEntity> slaveCampusFlux = slaveCampusRepository
                    .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(companyUUID), searchKeyWord, UUID.fromString(companyUUID));

            return slaveCampusFlux
                    .collectList()
                    .flatMap(campusEntity -> slaveCampusRepository
                            .countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(companyUUID), searchKeyWord, UUID.fromString(companyUUID))
                            .flatMap(count -> {
                                if (campusEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", campusEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of status is present
        else if (!status.isEmpty()) {
            Flux<SlaveCampusEntity> slaveCampusFlux = slaveCampusRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveCampusFlux
                    .collectList()
                    .flatMap(campusEntity -> slaveCampusRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (campusEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", campusEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional parameter is present
        else {
            Flux<SlaveCampusEntity> slaveCampusFlux = slaveCampusRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveCampusFlux
                    .collectList()
                    .flatMap(campusEntity -> slaveCampusRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (campusEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", campusEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_campuses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID campusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCampusRepository.findByUuidAndDeletedAtIsNull(campusUUID)
                .flatMap(campusEntity -> responseSuccessMsg("Record Fetched Successfully", campusEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //This Function Checks if  campus exists with Given Name
    @AuthHasPermission(value = "academic_api_v1_campuses_name_show")
    public Mono<ServerResponse> showByName(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");

        return slaveCampusRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(name)
                .flatMap(campusEntity -> responseSuccessMsg("Record Fetched Successfully", campusEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //This Function Fetch Campus UUID's List against given Company UUID
    @AuthHasPermission(value = "academic_api_v1_campuses_company_list_show")
    public Mono<ServerResponse> showList(ServerRequest serverRequest) {
        final UUID companyUUID = UUID.fromString(serverRequest.pathVariable("companyUUID"));

        return slaveCampusRepository.getAllCampusUUIDAgainstCompany(companyUUID)
                .flatMap(uuids -> {
                    List<String> listOfUUIDs = Arrays.asList(uuids.split("\\s*,\\s*"));
                    return responseSuccessMsg("Records Fetched Successfully", listOfUUIDs);
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."));
    }

    //This Function Is used By Student Financial Module to Check if Campus UUID exists in Financial Voucher Mapping
    @AuthHasPermission(value = "academic_api_v1_campuses_list_show")
    public Mono<ServerResponse> showCampusListInStudentFinancial(ServerRequest serverRequest) {

        List<String> uuids = serverRequest.queryParams().get("uuid");

        //This is Campus List to paas in the query
        List<UUID> campusList = new ArrayList<>();
        if (uuids != null) {
            for (String campus : uuids) {
                campusList.add(UUID.fromString(campus));
            }
        }

        // Used to Show Existing Campus UUIDs in Response
        List<UUID> finalList = new ArrayList<>();

        return campusRepository.findAllByUuidInAndDeletedAtIsNull(campusList)
                .collectList()
                .flatMap(campusEntities -> {
                    for (CampusEntity entity : campusEntities) {
                        finalList.add(entity.getUuid());
                    }
                    return responseSuccessMsg("Records Fetched Successfully", finalList)
                            .switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."));
                });
    }

//    //Check if Company id exists in Config Module
//    public Mono<ServerResponse> getCompanyId(ServerRequest serverRequest) {
//        final long companyId = Long.parseLong(serverRequest.pathVariable("companyId"));
//
//        return serverRequest.formData()
//                .flatMap(value -> slaveCampusRepository.findFirstByCompanyUUIDAndDeletedAtIsNull(companyId)
//                        .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
//                ).switchIfEmpty(responseInfoMsg("Record Does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Record Does not exist"));
//    }

    //    Show Mapped Campuses for Financial Voucher UUID
    @AuthHasPermission(value = "academic_api_v1_campuses_financial-voucher_mapped_show")
    public Mono<ServerResponse> listOfMappedCampusesAgainstFinancialVoucher(ServerRequest serverRequest) {

        UUID financialVoucherUUID = UUID.fromString(serverRequest.pathVariable("financialVoucherUUID"));

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Optional Query Parameter of Status
        Optional<String> status = serverRequest.queryParam("status");

        // Optional Query Parameter of Company UUID
        Optional<String> companyUUID = serverRequest.queryParam("companyUUID");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        return apiCallService.getDataWithUUID(studentFinancialUri + "api/v1/financial-voucher-campuses/list/show/", financialVoucherUUID)
                .flatMap(jsonNode -> {
                    List<UUID> listOfUUIDs = new ArrayList<>(apiCallService.getListOfUUIDs(jsonNode));

                    if (status.isPresent() && companyUUID.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidIn(searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));

                    } else if (companyUUID.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidIn(searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    } else if (status.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    } else {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, listOfUUIDs, searchKeyWord, listOfUUIDs);
                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(searchKeyWord, listOfUUIDs, searchKeyWord, listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    }
                });
    }

    //    Show Unmapped Campuses for Financial Voucher UUID
    @AuthHasPermission(value = "academic_api_v1_campuses_financial-voucher_un-mapped_show")
    public Mono<ServerResponse> listOfExistingCampusesAgainstFinancialVoucher(ServerRequest serverRequest) {

        UUID financialVoucherUUID = UUID.fromString(serverRequest.pathVariable("financialVoucherUUID"));

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Optional Query Parameter of Status
        Optional<String> status = serverRequest.queryParam("status");

        // Optional Query Parameter of Company UUID
        Optional<String> companyUUID = serverRequest.queryParam("companyUUID");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        return apiCallService.getDataWithUUID(studentFinancialUri + "api/v1/financial-voucher-campuses/list/show/", financialVoucherUUID)
                .flatMap(jsonNode -> {
                    List<UUID> listOfUUIDs = new ArrayList<>(apiCallService.getListOfUUIDs(jsonNode));

                    if (status.isPresent() && companyUUID.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotIn(pageable, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndStatusAndDeletedAtIsNullAndUuidNotIn(searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), Boolean.valueOf(status.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));

                    } else if (companyUUID.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotIn(pageable, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndCompanyUUIDAndDeletedAtIsNullAndUuidNotIn(searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs, searchKeyWord, UUID.fromString(companyUUID.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    } else if (status.isPresent()) {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotIn(pageable, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs);

                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidNotIn(searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs, searchKeyWord, Boolean.valueOf(status.get()), listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    } else {
                        Flux<SlaveCampusEntity> slaveCampusEntityFlux = slaveCampusRepository
                                .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotIn(pageable, searchKeyWord, listOfUUIDs, searchKeyWord, listOfUUIDs);
                        return slaveCampusEntityFlux
                                .collectList()
                                .flatMap(campusEntity -> slaveCampusRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotInOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullAndUuidNotIn(searchKeyWord, listOfUUIDs, searchKeyWord, listOfUUIDs)
                                        .flatMap(count -> {
                                            if (campusEntity.isEmpty()) {
                                                return responseIndexInfoMsg("Record does not exist", count);

                                            } else {

                                                return responseIndexSuccessMsg("Records Fetched Successfully", campusEntity, count);
                                            }
                                        })
                                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer"));
                    }
                });
    }

    @AuthHasPermission(value = "academic_api_v1_campuses_store")
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

                    UUID branchUUID = null;
                    if (value.containsKey("branchUUID") && (!value.getFirst("branchUUID").isEmpty())) {
                        branchUUID = UUID.fromString(value.getFirst("branchUUID").trim());
                    }

                    CampusEntity campusEntity = CampusEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .companyUUID(UUID.fromString(value.getFirst("companyUUID")))
                            .branchUUID(branchUUID)
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .code(value.getFirst("code").trim())
                            .establishmentDate(LocalDateTime.parse((value.getFirst("establishmentDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .languageUUID(UUID.fromString(value.getFirst("languageUUID")))
                            .locationUUID(UUID.fromString(value.getFirst("locationUUID")))
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

                    // check if name is unique
                    return campusRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(campusEntity.getName())
                            .flatMap(checkMsg -> responseInfoMsg("Name Already Exists"))
                            // check if code is unique
                            .switchIfEmpty(Mono.defer(() -> campusRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNull(campusEntity.getCode())
                                    .flatMap(checkMsg -> responseInfoMsg("Code Already Exists"))))
                            //  check if city uuid exists
                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(configUri + "api/v1/locations/show/", campusEntity.getLocationUUID())
                                    .flatMap(locationJsonNode -> apiCallService.getUUID(locationJsonNode)
                                            //  check if language uuid exists
                                            .flatMap(locationUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/languages/show/", campusEntity.getLanguageUUID())
                                                    .flatMap(languageJsonNode -> apiCallService.getUUID(languageJsonNode)
                                                            //  check if company uuid exists
                                                            .flatMap(languageUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/companies/show/", campusEntity.getCompanyUUID())
                                                                    .flatMap(companyJsonNode -> apiCallService.getUUID(companyJsonNode)
                                                                            .flatMap(companyUUID -> {
                                                                                        if (campusEntity.getBranchUUID() != null) {
                                                                                            //  check if branch uuid exists
                                                                                            return apiCallService.getDataWithUUID(configUri + "api/v1/branches/show/", campusEntity.getBranchUUID())
                                                                                                    .flatMap(branchJsonNode -> apiCallService.getUUID(branchJsonNode)
                                                                                                            .flatMap(branch -> apiCallService.getCompanyUUID(branchJsonNode)
                                                                                                                    .flatMap(branchCompany -> {
                                                                                                                        if (!branchCompany.equals(campusEntity.getCompanyUUID())) {
                                                                                                                            return responseInfoMsg("Branch does not exists in given Company");
                                                                                                                        } else {
                                                                                                                            return campusRepository.save(campusEntity)
                                                                                                                                    .flatMap(campusEntityDB -> responseSuccessMsg("Record Stored Successfully", campusEntityDB))
                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                                                                                                        }
                                                                                                                    })
                                                                                                            )).switchIfEmpty(responseInfoMsg("Branch does not exist"))
                                                                                                    .onErrorResume(err -> responseErrorMsg("Branch does not exist.Please Contact Developer."));
                                                                                        } else {
                                                                                            return campusRepository.save(campusEntity)
                                                                                                    .flatMap(campusEntityDB -> responseSuccessMsg("Record Stored Successfully", campusEntityDB))
                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                                                                        }
                                                                                    }
                                                                            )).switchIfEmpty(responseInfoMsg("Company does not exist"))
                                                                    .onErrorResume(err -> responseErrorMsg("Create Company First.Please Contact Developer."))
                                                            )).switchIfEmpty(responseInfoMsg("Language does not exist"))
                                                    .onErrorResume(err -> responseErrorMsg("Language does not exist.Please Contact Developer."))
                                            )).switchIfEmpty(responseInfoMsg("Location does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Location does not exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_campuses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID campusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> campusRepository.findByUuidAndDeletedAtIsNull(campusUUID)
                        .flatMap(previousCampusEntity -> {

                            UUID branchUUID = null;
                            if (value.containsKey("branchUUID") && (!value.getFirst("branchUUID").isEmpty())) {
                                branchUUID = UUID.fromString(value.getFirst("branchUUID").trim());
                            }

                            CampusEntity updatedEntity = CampusEntity
                                    .builder()
                                    .uuid(previousCampusEntity.getUuid())
                                    .companyUUID(UUID.fromString(value.getFirst("companyUUID")))
                                    .branchUUID(branchUUID)
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .code(value.getFirst("code").trim())
                                    .establishmentDate(LocalDateTime.parse((value.getFirst("establishmentDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .languageUUID(UUID.fromString(value.getFirst("languageUUID")))
                                    .locationUUID(UUID.fromString(value.getFirst("locationUUID")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousCampusEntity.getCreatedAt())
                                    .createdBy(previousCampusEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousCampusEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousCampusEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousCampusEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousCampusEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousCampusEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousCampusEntity.getReqCreatedReferer())
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
                            previousCampusEntity.setDeletedBy(UUID.fromString(userId));
                            previousCampusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousCampusEntity.setReqDeletedIP(reqIp);
                            previousCampusEntity.setReqDeletedPort(reqPort);
                            previousCampusEntity.setReqDeletedBrowser(reqBrowser);
                            previousCampusEntity.setReqDeletedOS(reqOs);
                            previousCampusEntity.setReqDeletedDevice(reqDevice);
                            previousCampusEntity.setReqDeletedReferer(reqReferer);

                            // check if name is unique
                            return campusRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), campusUUID)
                                    .flatMap(checkMsg -> responseInfoMsg("Name Already Exists"))
                                    // check if code is unique
                                    .switchIfEmpty(Mono.defer(() -> campusRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(), campusUUID)
                                            .flatMap(checkMsg -> responseInfoMsg("Code Already Exists"))))
                                    //  check if location uuid exists
                                    .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(configUri + "api/v1/locations/show/", updatedEntity.getLocationUUID())
                                            .flatMap(locationJsonNode -> apiCallService.getUUID(locationJsonNode)
                                                    //  check if language uuid exists
                                                    .flatMap(locationUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/languages/show/", updatedEntity.getLanguageUUID())
                                                            .flatMap(languageJsonNode -> apiCallService.getUUID(languageJsonNode)
                                                                    // check if company uuid exists
                                                                    .flatMap(languageUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/companies/show/", updatedEntity.getCompanyUUID())
                                                                            .flatMap(companyJsonNode -> apiCallService.getUUID(companyJsonNode)
                                                                                    .flatMap(companyUUID -> {
                                                                                                if (updatedEntity.getBranchUUID() != null) {
                                                                                                    // check if branch uuid exists
                                                                                                    return apiCallService.getDataWithUUID(configUri + "api/v1/branches/show/", updatedEntity.getBranchUUID())
                                                                                                            .flatMap(branchJsonNode -> apiCallService.getUUID(branchJsonNode)
                                                                                                                    .flatMap(branch -> apiCallService.getCompanyUUID(branchJsonNode)
                                                                                                                            .flatMap(branchCompany -> {
                                                                                                                                if (!branchCompany.equals(updatedEntity.getCompanyUUID())) {
                                                                                                                                    return responseInfoMsg("Branch does not exists in given Company");
                                                                                                                                } else {
                                                                                                                                    return campusRepository.save(previousCampusEntity)
                                                                                                                                            .then(campusRepository.save(updatedEntity))
                                                                                                                                            .flatMap(campusEntityDB -> responseSuccessMsg("Record Updated Successfully", campusEntityDB))
                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                                                                                                                }
                                                                                                                            })
                                                                                                                    )).switchIfEmpty(responseInfoMsg("Branch does not exist"))
                                                                                                            .onErrorResume(err -> responseErrorMsg("Branch does not exist.Please Contact Developer."));
                                                                                                } else {
                                                                                                    return campusRepository.save(previousCampusEntity)
                                                                                                            .then(campusRepository.save(updatedEntity))
                                                                                                            .flatMap(campusEntityDB -> responseSuccessMsg("Record Updated Successfully", campusEntityDB))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                                                                                }
                                                                                            }
                                                                                    )).switchIfEmpty(responseInfoMsg("Company does not exist"))
                                                                            .onErrorResume(err -> responseErrorMsg("Create Company First.Please Contact Developer."))
                                                                    )).switchIfEmpty(responseInfoMsg("Language does not exist"))
                                                            .onErrorResume(err -> responseErrorMsg("Language does not exist.Please Contact Developer."))
                                                    )).switchIfEmpty(responseInfoMsg("Location does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Location does not exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_campuses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID campusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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

        return campusRepository.findByUuidAndDeletedAtIsNull(campusUUID)
                .flatMap(campusEntity -> campusCourseOfferedPvtRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                //Checks if Campus Reference exists in Faculty
                                .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Campus Course Pvt."))
                                .switchIfEmpty(Mono.defer(() -> feeStructureRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Fee Structures
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Fee Structures."))))
                                .switchIfEmpty(Mono.defer(() -> registrationRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Registrations
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Fee Structures."))))
                                .switchIfEmpty(Mono.defer(() -> notificationDetailRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Notification Details
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Notification Details"))))
                                .switchIfEmpty(Mono.defer(() -> classroomRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Classrooms
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Classrooms."))))
                                .switchIfEmpty(Mono.defer(() -> teacherRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Teacher Records
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Records."))))
                                .switchIfEmpty(Mono.defer(() -> studentRepository.findFirstByCampusUUIDAndDeletedAtIsNull(campusEntity.getUuid())
                                        //Checks if Campus Reference exists in Student Records
                                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Records."))))
                                // check if campus reference exists in financial cost centers in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-cost-centers/campus/show/", campusEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchIDApiMsg -> responseInfoMsg("Unable to delete Record as Reference of record Exists")))))
                                // check if campus reference exists in financial cost profit in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-profit-centers/campus/show/", campusEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchIDApiMsg -> responseInfoMsg("Unable to delete Record as Reference of record Exists")))))
                                // check if campus reference exists in financial student accounts in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-accounts/campus/show/", campusEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchIDApiMsg -> responseInfoMsg("Unable to delete Record as Reference of record Exists")))))
                                // check if campus reference exists in financial transaction in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-transactions/campus/show/", campusEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchIDApiMsg -> responseInfoMsg("Unable to delete Record as Reference of record Exists")))))
                                // check if campus reference exists in Financial Voucher Company Pvt in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-voucher-campuses/campus/show/", campusEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchIDApiMsg -> responseInfoMsg("Unable to delete Record as Reference of record Exists")))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    campusEntity.setDeletedBy(UUID.fromString(userId));
                                    campusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    campusEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    campusEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    campusEntity.setReqDeletedIP(reqIp);
                                    campusEntity.setReqDeletedPort(reqPort);
                                    campusEntity.setReqDeletedBrowser(reqBrowser);
                                    campusEntity.setReqDeletedOS(reqOs);
                                    campusEntity.setReqDeletedDevice(reqDevice);
                                    campusEntity.setReqDeletedReferer(reqReferer);

                                    return campusRepository.save(campusEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."));
//                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));

                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"));
//                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_campuses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID campusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return campusRepository.findByUuidAndDeletedAtIsNull(campusUUID)
                            .flatMap(previousCampusEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousCampusEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CampusEntity updatedCampusEntity = CampusEntity
                                        .builder()
                                        .uuid(previousCampusEntity.getUuid())
                                        .companyUUID(previousCampusEntity.getCompanyUUID())
                                        .branchUUID(previousCampusEntity.getBranchUUID())
                                        .name(previousCampusEntity.getName())
                                        .description(previousCampusEntity.getDescription())
                                        .code(previousCampusEntity.getCode())
                                        .establishmentDate(previousCampusEntity.getEstablishmentDate())
                                        .languageUUID(previousCampusEntity.getLanguageUUID())
                                        .locationUUID(previousCampusEntity.getLocationUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousCampusEntity.getCreatedAt())
                                        .createdBy(previousCampusEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousCampusEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousCampusEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousCampusEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousCampusEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousCampusEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousCampusEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                //Deleting Previous Record and Creating a New One Based on UUID
                                previousCampusEntity.setDeletedBy(UUID.fromString(userId));
                                previousCampusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousCampusEntity.setReqDeletedIP(reqIp);
                                previousCampusEntity.setReqDeletedPort(reqPort);
                                previousCampusEntity.setReqDeletedBrowser(reqBrowser);
                                previousCampusEntity.setReqDeletedOS(reqOs);
                                previousCampusEntity.setReqDeletedDevice(reqDevice);
                                previousCampusEntity.setReqDeletedReferer(reqReferer);

                                return campusRepository.save(updatedCampusEntity)
                                        .then(campusRepository.save(previousCampusEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
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
