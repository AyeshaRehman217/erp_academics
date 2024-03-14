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
import tuf.webscaf.app.dbContext.master.entity.CampusCourseEntity;
import tuf.webscaf.app.dbContext.master.repositry.CampusCourseRepository;
import tuf.webscaf.app.dbContext.master.repositry.CampusRepository;
import tuf.webscaf.app.dbContext.master.repositry.CourseOfferedRepository;
import tuf.webscaf.app.dbContext.master.repositry.CourseRepository;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCampusCourseRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "CampusCourseHandler")
@Component
public class CampusCourseHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CampusCourseRepository campusCourseRepository;

    @Autowired
    SlaveCampusCourseRepository slaveCampusCourseRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialUri;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_campus-courses_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        //Filter Records Based on Campus UUID
        String campusUUID = serverRequest.queryParam("campusUUID").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseIndexWithStatusAndCampus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository
                            .countAllCampusCourseWithCampusAndStatusFilter(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!campusUUID.isEmpty()) {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseIndexWithCampusFilter(UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository
                            .countAllCampusCourseWithCampusFilter(UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository
                            .countAllByDeletedAtIsNullAndStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository.countAllByDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_campus-courses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID campusCourseUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCampusCourseRepository.findByUuidAndDeletedAtIsNull(campusCourseUUID)
                .flatMap(campusCourseEntity -> responseSuccessMsg("Record Fetched Successfully", campusCourseEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //    Show campus-courses against campus and academic session
    @AuthHasPermission(value = "academic_api_v1_academic-session_campus_campus-courses_show")
    public Mono<ServerResponse> showCampusCoursesAgainstCampusAndAcademicSession(ServerRequest serverRequest) {

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

        UUID academicSessionUUID = UUID.fromString((serverRequest.pathVariable("academicSessionUUID")));

        UUID campusUUID = UUID.fromString(serverRequest.queryParam("campusUUID").map(String::toString).orElse("").trim());


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseListAgainstCampusAndAcademicSessionWithStatus(campusUUID, academicSessionUUID, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository
                            .countCampusCourseListAgainstCampusAndAcademicSessionWithStatus(campusUUID, academicSessionUUID, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCampusCourseDto> slaveCampusCourseFlux = slaveCampusCourseRepository
                    .campusCourseListAgainstCampusAndAcademicSession(campusUUID, academicSessionUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCampusCourseFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCampusCourseRepository.countCampusCoursesListAgainstCampusAndAcademicSession(campusUUID, academicSessionUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_campus-courses_store")
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
                    CampusCourseEntity entity = CampusCourseEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
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

                    // check campus uuid exists
                    return campusRepository.findByUuidAndDeletedAtIsNull(entity.getCampusUUID())
//                            check course uuid exists
                            .flatMap(campusEntity -> courseRepository.findByUuidAndDeletedAtIsNull(entity.getCourseUUID())
                                    .flatMap(courseEntity -> campusCourseRepository.findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(entity.getCampusUUID(), entity.getCourseUUID())
                                            .flatMap(courseSubject -> responseInfoMsg("Course already exist"))
                                            .switchIfEmpty(Mono.defer(() -> campusCourseRepository.save(entity)
                                                    .flatMap(courseSubjectEntity -> responseSuccessMsg("Record Stored Successfully", courseSubjectEntity)
                                                    ).switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Campus does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Campus does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

//    public Mono<ServerResponse> store(ServerRequest serverRequest) {
//        String userId = serverRequest.headers().firstHeader("auid");
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
//                .flatMap(value -> {
//                    // used to send financial cost center form data to request
//                    MultiValueMap<String, String> costCenterFormData = new LinkedMultiValueMap<>();
//
//                    // used to send financial profit center form data to request
//                    MultiValueMap<String, String> profitCenterFormData = new LinkedMultiValueMap<>();
//
//                    CampusCourseEntity campusCourseEntity = CampusCourseEntity.builder()
//                            .uuid(UUID.randomUUID())
//                            .courseUUID(UUID.fromString(value.getFirst("courseUUID").trim()))
//                            .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//
////                    check campus uuid exists
//                    return campusRepository.findByUuidAndDeletedAtIsNull(campusCourseEntity.getCampusUUID())
////                            check course uuid exists
//                            .flatMap(campusEntity -> courseRepository.findByUuidAndDeletedAtIsNull(campusCourseEntity.getCourseUUID())
//                                    .flatMap(courseEntity -> campusCourseRepository.findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNull
//                                                    (campusCourseEntity.getCampusUUID(), campusCourseEntity.getCourseUUID())
//                                            .flatMap(checkCampusAndCourse -> responseInfoMsg("Course already mapped for this campus"))
//
//                                    .switchIfEmpty(Mono.defer(() -> {
//                                        //add financial cost center to form data
//                                        costCenterFormData.add("name", courseEntity.getName());
//                                        costCenterFormData.add("description", courseEntity.getDescription());
//                                        costCenterFormData.add("campusUUID", String.valueOf(campusEntity.getUuid()));
//                                        costCenterFormData.add("companyUUID", String.valueOf(campusEntity.getCompanyUUID()));
//                                        costCenterFormData.add("status", String.valueOf(Boolean.TRUE));
//
//
//                                        //add financial profit center to form data
//                                        profitCenterFormData.add("name", courseEntity.getName());
//                                        profitCenterFormData.add("description", courseEntity.getDescription());
//                                        profitCenterFormData.add("campusUUID", String.valueOf(campusEntity.getUuid()));
//                                        profitCenterFormData.add("companyUUID", String.valueOf(campusEntity.getCompanyUUID()));
//                                        profitCenterFormData.add("status", String.valueOf(Boolean.TRUE));
//
//                                        return apiCallService.getData(studentFinancialUri + "api/v1/info/show")
//                                                // checks if student financial module id exists
//                                                .flatMap(moduleJsonNode -> apiCallService.getModuleId(moduleJsonNode)
////                                                        store financial cost centers
//                                                        .flatMap(moduleId -> apiCallService.postDataList(costCenterFormData, studentFinancialUri + "api/v1/financial-cost-centers/store", userId)
//                                                                .flatMap(financialCostCenterJson -> apiCallService.getUUID(financialCostCenterJson)
////                                                                        store financial  profit centers
//                                                                        .flatMap(financialCostCenter -> apiCallService.postDataList(profitCenterFormData, studentFinancialUri + "api/v1/financial-profit-centers/store", userId)
//                                                                                .flatMap(financialProfitCenterJson -> apiCallService.getUUID(financialProfitCenterJson)
//                                                                                        // store campus course
//                                                                                        .flatMap(financialProfitCenter -> campusCourseRepository.save(campusCourseEntity)
//                                                                                                .flatMap(courseSubjectEntity -> responseSuccessMsg("Record Stored Successfully", courseSubjectEntity))
//                                                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
//                                                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
//                                                                                                .switchIfEmpty(Mono.defer(() -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/delete", userId)
//                                                                                                        .flatMap(costCenterDeleted -> deletePreviousRecords(financialProfitCenterJson, studentFinancialUri + "api/v1/financial-profit-centers/delete", userId)
//                                                                                                                .flatMap(profitCenterDeleted -> responseInfoMsg("Unable to store Record.There is something wrong please try again.")
//                                                                                                                ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                        ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                ))
//                                                                                                .onErrorResume(err -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/delete", userId)
//                                                                                                        .flatMap(costCenterDeleted -> deletePreviousRecords(financialProfitCenterJson, studentFinancialUri + "api/v1/financial-profit-centers/delete", userId)
//                                                                                                                .flatMap(profitCenterDeleted -> responseInfoMsg("Unable to store Record.There is something wrong please try again.")
//                                                                                                                ).onErrorResume(ex -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                        ).onErrorResume(er -> responseErrorMsg("There is something wrong.Please Contact Developer.")))
//                                                                                        ).switchIfEmpty(Mono.defer(() -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/delete", userId)
//                                                                                                .flatMap(costCenterDeleted -> apiCallService.getResponseMsg(financialProfitCenterJson)
//                                                                                                        .flatMap(this::responseInfoMsg)
//                                                                                                ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                        )).onErrorResume(err -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/delete", userId)
//                                                                                                .flatMap(costCenterDeleted -> apiCallService.getResponseMsg(financialProfitCenterJson)
//                                                                                                        .flatMap(this::responseInfoMsg)
//                                                                                                ).onErrorResume(ex -> responseErrorMsg("There is something wrong.Please Contact Developer."))))
//                                                                        ).switchIfEmpty(apiCallService.getResponseMsg(financialCostCenterJson).flatMap(this::responseInfoMsg))
//                                                                        .onErrorResume(err -> apiCallService.getResponseMsg(financialCostCenterJson).flatMap(this::responseErrorMsg)))
//                                                        ).switchIfEmpty(responseInfoMsg("Student Financial Module does not exist")));
////                                                        .onErrorResume(er -> responseErrorMsg("Student Financial Module does not exist.Please Contact Developer.")));
//                                    }))
//                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
////                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"))
//                                   ).switchIfEmpty(responseInfoMsg("Campus does not exist"));
////                            .onErrorResume(ex -> responseErrorMsg("Campus does not exist. Please contact developer"));
//                }).switchIfEmpty(responseInfoMsg("Unable to read Request"));
////                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    // delete previous records, if some error occurred
//    public Mono<String> deletePreviousRecords(JsonNode jsonNode, String url, String userId) {
//
//        return apiCallService.getUUID(jsonNode)
//                .flatMap(getUUID -> apiCallService.deleteDataWithUUID(url, getUUID, userId)
//                        .flatMap(delJsonNode -> apiCallService.getUUID(delJsonNode)));
//
//    }

    @AuthHasPermission(value = "academic_api_v1_campus-courses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID campusCourseUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> campusCourseRepository.findByUuidAndDeletedAtIsNull(campusCourseUUID)
                                .flatMap(entity -> {
                                    CampusCourseEntity updatedEntity = CampusCourseEntity.builder()
                                            .uuid(entity.getUuid())
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                                            .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
                                            .createdAt(entity.getCreatedAt())
                                            .createdBy(entity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(entity.getReqCreatedIP())
                                            .reqCreatedPort(entity.getReqCreatedPort())
                                            .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                            .reqCreatedOS(entity.getReqCreatedOS())
                                            .reqCreatedDevice(entity.getReqCreatedDevice())
                                            .reqCreatedReferer(entity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    entity.setDeletedBy(UUID.fromString(userId));
                                    entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    entity.setReqDeletedIP(reqIp);
                                    entity.setReqDeletedPort(reqPort);
                                    entity.setReqDeletedBrowser(reqBrowser);
                                    entity.setReqDeletedOS(reqOs);
                                    entity.setReqDeletedDevice(reqDevice);
                                    entity.setReqDeletedReferer(reqReferer);


                                    // check campus uuid exists
                                    return campusRepository.findByUuidAndDeletedAtIsNull(entity.getCampusUUID())
//                            check course uuid exists
                                            .flatMap(campusEntity -> courseRepository.findByUuidAndDeletedAtIsNull(entity.getCourseUUID())
                                                    .flatMap(courseEntity -> campusCourseRepository
                                                            .findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(entity.getCampusUUID(), entity.getCourseUUID(), campusCourseUUID)
                                                            .flatMap(courseSubject -> responseInfoMsg("Course Already Exist"))
                                                            .switchIfEmpty(Mono.defer(() -> campusCourseRepository.save(entity)
                                                                    .then(campusCourseRepository.save(updatedEntity))
                                                                    .flatMap(campusCourseEntity -> responseSuccessMsg("Record Updated Successfully", campusCourseEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                            ))
                                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Campus does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Campus does not exist. Please contact developer"));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_campus-courses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID campusCourseUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return campusCourseRepository.findByUuidAndDeletedAtIsNull(campusCourseUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CampusCourseEntity campusCourseEntity = CampusCourseEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .courseUUID(previousEntity.getCourseUUID())
                                        .campusUUID(previousEntity.getCampusUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return campusCourseRepository.save(previousEntity)
                                        .then(campusCourseRepository.save(campusCourseEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_campus-courses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID cloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return campusCourseRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                .flatMap(campusCourseEntity -> {

                    campusCourseEntity.setDeletedBy(UUID.fromString(userId));
                    campusCourseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    campusCourseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    campusCourseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    campusCourseEntity.setReqDeletedIP(reqIp);
                    campusCourseEntity.setReqDeletedPort(reqPort);
                    campusCourseEntity.setReqDeletedBrowser(reqBrowser);
                    campusCourseEntity.setReqDeletedOS(reqOs);
                    campusCourseEntity.setReqDeletedDevice(reqDevice);
                    campusCourseEntity.setReqDeletedReferer(reqReferer);

                    return campusCourseRepository.save(campusCourseEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
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
