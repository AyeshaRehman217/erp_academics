package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.repositry.CampusCourseOfferedPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.CampusRepository;
import tuf.webscaf.app.dbContext.master.repositry.CourseOfferedRepository;
import tuf.webscaf.app.dbContext.master.repositry.CourseRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCampusCourseOfferedPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseOfferedRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.List;

@Tag(name = "CampusCourseOfferedPvtHandler")
@Component
public class CampusCourseOfferedPvtHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CampusCourseOfferedPvtRepository campusCourseOfferedPvtRepository;

    @Autowired
    SlaveCampusCourseOfferedPvtRepository slaveCampusCourseOfferedPvtRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    SlaveCourseOfferedRepository slaveCourseOfferedRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SlaveCourseRepository slaveCourseRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialUri;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

//
//    public Mono<ServerResponse> showCourseOfferedAgainstCampus(ServerRequest serverRequest) {
//
//        final UUID campusUUID = UUID.fromString(serverRequest.pathVariable("campusUUID"));
//
//        UUID academicSessionUUID = UUID.fromString(serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse(""));
//
//        Optional<String> status = serverRequest.queryParam("status");
//
//        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
//
//        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
//        if (size > 100) {
//            size = 100;
//        }
//        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
//        int page = pageRequest - 1;
//
//        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
//        Sort.Direction direction;
//        switch (d.toLowerCase()) {
//            case "asc":
//                direction = Sort.Direction.ASC;
//                break;
//            case "desc":
//                direction = Sort.Direction.DESC;
//                break;
//            default:
//                direction = Sort.Direction.ASC;
//        }
//
//        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//        if (status.isPresent()) {
//            Flux<SlaveCourseOfferedEntity> slaveCourseOfferedEntityFlux = slaveCampusCourseOfferedPvtRepository
//                    .existingCourseOfferedListWithStatus(campusUUID, academicSessionUUID, Boolean.valueOf(status.get()), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveCourseOfferedRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionUUID)
//                    .flatMap(courseOffered -> slaveCourseOfferedEntityFlux
//                            .collectList()
//                            .flatMap(courseEntityDB -> slaveCourseOfferedRepository.countExistingCourseOfferedRecordsAgainstCampusWithStatus(campusUUID, academicSessionUUID, Boolean.valueOf(status.get()))
//                                    .flatMap(count -> {
//                                        if (courseEntityDB.isEmpty()) {
//                                            return responseIndexInfoMsg("Record does not exist", count);
//                                        } else {
//                                            return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntityDB, count);
//                                        }
//                                    })
//                            )
//                    )
//                    .switchIfEmpty(responseInfoMsg("Academic Session Record Does not exist in Course Offered"))
//                    .onErrorResume(ex -> responseErrorMsg("Academic Session Record Does not exist in Course Offered.Please Contact Developer."));
//        } else {
//            Flux<SlaveCourseOfferedEntity> slaveCourseOfferedEntityFlux = slaveCampusCourseOfferedPvtRepository
//                    .existingCourseOfferedList(campusUUID, academicSessionUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveCourseOfferedRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionUUID)
//                    .flatMap(courseOffered -> slaveCourseOfferedEntityFlux
//                            .collectList()
//                            .flatMap(courseEntityDB -> slaveCourseOfferedRepository.countExistingCourseOfferedAgainstCampusRecords(campusUUID, academicSessionUUID)
//                                    .flatMap(count -> {
//                                        if (courseEntityDB.isEmpty()) {
//                                            return responseIndexInfoMsg("Record does not exist", count);
//                                        } else {
//                                            return responseIndexSuccessMsg("All Records fetched successfully", courseEntityDB, count);
//                                        }
//                                    })
//                            )
//                    )
//                    .switchIfEmpty(responseInfoMsg("Academic Session Record Does not exist in Course Offered"))
//                    .onErrorResume(ex -> responseErrorMsg("Academic Session Record Does not exist in Course Offered.Please Contact Developer."));
//        }
//
//    }
//
//    public Mono<ServerResponse> showMappedCourseOfferedAgainstCampus(ServerRequest serverRequest) {
//
//        final UUID campusUUID = UUID.fromString(serverRequest.pathVariable("campusUUID"));
//
//        UUID academicSessionUUID = UUID.fromString(serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse(""));
//
//        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
//
//        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
//        if (size > 100) {
//            size = 100;
//        }
//        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
//        int page = pageRequest - 1;
//
//        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
//        Sort.Direction direction;
//        switch (d.toLowerCase()) {
//            case "asc":
//                direction = Sort.Direction.ASC;
//                break;
//            case "desc":
//                direction = Sort.Direction.DESC;
//                break;
//            default:
//                direction = Sort.Direction.ASC;
//        }
//
//        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
//        Optional<String> status = serverRequest.queryParam("status");
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//        if (status.isPresent()) {
//            Flux<SlaveCourseOfferedEntity> slaveCourseOfferedEntityFlux = slaveCourseOfferedRepository
//                    .showCourseOfferedListWithStatus(campusUUID, academicSessionUUID, Boolean.valueOf(status.get()), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveCourseOfferedRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionUUID)
//                    .flatMap(courseOffered -> slaveCourseOfferedEntityFlux
//                            .collectList()
//                            .flatMap(courseEntityData -> slaveCourseOfferedRepository
//                                    .countMappedCampusCourseOfferedWithStatus(campusUUID, academicSessionUUID, Boolean.valueOf(status.get()))
//                                    .flatMap(count -> {
//                                        if (courseEntityData.isEmpty()) {
//                                            return responseIndexInfoMsg("Record does not exist", count);
//
//                                        } else {
//
//                                            return responseIndexSuccessMsg("All Records fetched successfully", courseEntityData, count);
//                                        }
//                                    })
//                            )
//                    ).switchIfEmpty(responseInfoMsg("Academic Session Record Does not exist in Course Offered"))
//                    .onErrorResume(ex -> responseErrorMsg("Academic Session Record Does not exist in Course Offered.Please Contact Developer."))
//                    .switchIfEmpty(responseInfoMsg("Unable to read request"))
//                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
//        } else {
//            Flux<SlaveCourseOfferedEntity> slaveCourseOfferedEntityFlux = slaveCourseOfferedRepository
//                    .showMappedCourseOfferedList(campusUUID, academicSessionUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//            return slaveCourseOfferedRepository.findFirstByAcademicSessionUUIDAndDeletedAtIsNull(academicSessionUUID)
//                    .flatMap(courseOffered -> slaveCourseOfferedEntityFlux
//                            .collectList()
//                            .flatMap(courseEntityData -> slaveCourseOfferedRepository
//                                    .countMappedCourseOfferedAgainstCampus(campusUUID, academicSessionUUID)
//                                    .flatMap(count -> {
//                                        if (courseEntityData.isEmpty()) {
//                                            return responseIndexInfoMsg("Record does not exist", count);
//
//                                        } else {
//
//                                            return responseIndexSuccessMsg("All Records fetched successfully", courseEntityData, count);
//                                        }
//                                    })
//                            )
//                    )
//                    .switchIfEmpty(responseInfoMsg("Academic Session Record Does not exist in Course Offered"))
//                    .onErrorResume(ex -> responseErrorMsg("Academic Session Record Does not exist in Course Offered.Please Contact Developer."))
//                    .switchIfEmpty(responseInfoMsg("Unable to read request"))
//                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
//        }
//    }
//
//    public Mono<ServerResponse> store(ServerRequest serverRequest) {
//
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        final UUID campusUUID = UUID.fromString(serverRequest.pathVariable("campusUUID"));
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown user");
//        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//            return responseWarningMsg("Unknown user");
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> campusRepository.findByUuidAndDeletedAtIsNull(campusUUID)
//                                .flatMap(campusEntity -> {
//                                    //getting List of Course UUID From Front
//                                    List<String> listOfCourseOffered = value.get("courseOfferedUUID");
//
//                                    List<UUID> l_list = new ArrayList<>();
//
//                                    List<CampusCourseOfferedPvtEntity> listPvt = new ArrayList<>();
//
//                                    if (value.containsKey("courseOfferedUUID")) {
//                                        listOfCourseOffered.removeIf(s -> s.equals(""));
//
//                                        for (String getCourseOfferedUUID : listOfCourseOffered) {
//                                            l_list.add(UUID.fromString(getCourseOfferedUUID));
//                                        }
//
////                                Check existing records of campus and list of Course Offered
//
//                                        return campusCourseOfferedPvtRepository.findAllByCampusUUIDAndCourseOfferedUUIDInAndDeletedAtIsNull(campusUUID, l_list)
//                                                .collectList()
//                                                .flatMap(courseListRemove -> {
//
//                                                    // Course Offered UUID List to Get Records in Response
//                                                    List<UUID> courseOfferedList = new ArrayList<>(l_list);
//
//                                                    //Removing Already Existing Course Offered From List to Avoid Duplicate Entries
//                                                    for (CampusCourseOfferedPvtEntity pvtEntity : courseListRemove) {
//                                                        l_list.remove(pvtEntity.getCourseOfferedUUID());
//                                                    }
//
//                                                    if (!l_list.isEmpty()) {
//
//                                                        // used to send financial cost center form data to request
//                                                        MultiValueMap<String, String> costCenterFormData = new LinkedMultiValueMap<>();
//
//                                                        // used to send financial profit center form data to request
//                                                        MultiValueMap<String, String> profitCenterFormData = new LinkedMultiValueMap<>();
//
//                                                        return courseOfferedRepository.showAllCourseOfferedByUuidIn(getUuidList(l_list))
//                                                                .collectList()
//                                                                .flatMap(CourseOfferedEntityDB -> {
//                                                                    for (CourseOfferedDto courseOffer : CourseOfferedEntityDB) {
//
//                                                                        CampusCourseOfferedPvtEntity campusCourseOfferedPvtEntity = CampusCourseOfferedPvtEntity
//                                                                                .builder()
//                                                                                .campusUUID(campusUUID)
//                                                                                .courseOfferedUUID(courseOffer.getUuid())
//                                                                                .createdBy(UUID.fromString(userId))
//                                                                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                                                                                .build();
//
//
//                                                                        FinancialCostCenterDto financialCostCenterDto = FinancialCostCenterDto.builder()
//                                                                                .status(Boolean.TRUE)
//                                                                                .name(courseOffer.getCourseName())
//                                                                                .description(courseOffer.getCourseDescription())
//                                                                                .campusUUID(campusEntity.getUuid())
//                                                                                .companyUUID(campusEntity.getCompanyUUID())
//                                                                                .build();
//
//                                                                        FinancialProfitCenterDto financialProfitCenterDto = FinancialProfitCenterDto.builder()
//                                                                                .status(Boolean.TRUE)
//                                                                                .name(courseOffer.getCourseName())
//                                                                                .description(courseOffer.getCourseDescription())
//                                                                                .campusUUID(campusEntity.getUuid())
//                                                                                .companyUUID(campusEntity.getCompanyUUID())
//                                                                                .build();
//
//                                                                        listPvt.add(campusCourseOfferedPvtEntity);
//
//                                                                        //add financial cost center dto to form data
//                                                                        costCenterFormData.add("financialCostCenter", objectToString(financialCostCenterDto));
//
//                                                                        //add financial profit center dto to form data
//                                                                        profitCenterFormData.add("financialProfitCenter", objectToString(financialProfitCenterDto));
//
//                                                                    }
//
//                                                                    return apiCallService.getData(studentFinancialUri + "api/v1/info/show")
//                                                                            // checks if student financial module Id exists
//                                                                            .flatMap(moduleJsonNode -> apiCallService.getModuleId(moduleJsonNode)
//                                                                                    // if student financial module Id exists, store financial cost centers and profit centers
//                                                                                    .flatMap(moduleId -> apiCallService.postDataList(costCenterFormData, studentFinancialUri + "api/v1/financial-cost-centers/list/store", userId)
//                                                                                            .flatMap(financialCostCenterJson -> apiCallService.getUUID(financialCostCenterJson)
//                                                                                                    .flatMap(financialCostCenter -> apiCallService.postDataList(profitCenterFormData, studentFinancialUri + "api/v1/financial-profit-centers/list/store", userId)
//                                                                                                            .flatMap(financialProfitCenterJson -> apiCallService.getUUID(financialProfitCenterJson)
//                                                                                                                    // store all pvt records
//                                                                                                                    .flatMap(financialProfitCenter -> campusCourseOfferedPvtRepository.saveAll(listPvt)
//                                                                                                                            .collectList()
//                                                                                                                            .flatMap(groupList -> {
//                                                                                                                                if (!groupList.isEmpty()) {
//                                                                                                                                    return getMappedRecords(courseOfferedList, "Record Stored Successfully");
//                                                                                                                                } else {
//                                                                                                                                    return getMappedRecords(courseOfferedList, "Records Are Not Stored");
//                                                                                                                                }
//                                                                                                                            }).switchIfEmpty(Mono.defer(() -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/list/delete", userId)
//                                                                                                                                    .flatMap(costCenterDeleted -> deletePreviousRecords(financialProfitCenterJson, studentFinancialUri + "api/v1/financial-profit-centers/list/delete", userId)
//                                                                                                                                            .flatMap(profitCenterDeleted -> responseInfoMsg("Unable to store Record.There is something wrong please try again.")
//                                                                                                                                            ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                                                    ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                                            ))
//                                                                                                                            .onErrorResume(err -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/list/delete", userId)
//                                                                                                                                    .flatMap(costCenterDeleted -> deletePreviousRecords(financialProfitCenterJson, studentFinancialUri + "api/v1/financial-profit-centers/list/delete", userId)
//                                                                                                                                            .flatMap(profitCenterDeleted -> responseInfoMsg("Unable to store Record.There is something wrong please try again.")
//                                                                                                                                            ).onErrorResume(ex -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                                                    ).onErrorResume(er -> responseErrorMsg("There is something wrong.Please Contact Developer.")))
//                                                                                                                    ).switchIfEmpty(Mono.defer(() -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/list/delete", userId)
//                                                                                                                            .flatMap(costCenterDeleted -> apiCallService.getResponseMsg(financialProfitCenterJson)
//                                                                                                                                    .flatMap(this::responseInfoMsg)
//                                                                                                                            ).onErrorResume(err -> responseErrorMsg("There is something wrong.Please Contact Developer."))
//                                                                                                                    ))
//                                                                                                                    .onErrorResume(err -> deletePreviousRecords(financialCostCenterJson, studentFinancialUri + "api/v1/financial-cost-centers/list/delete", userId)
//                                                                                                                            .flatMap(costCenterDeleted -> apiCallService.getResponseMsg(financialProfitCenterJson)
//                                                                                                                                    .flatMap(this::responseInfoMsg)
//                                                                                                                            ).onErrorResume(ex -> responseErrorMsg("There is something wrong.Please Contact Developer."))))
//                                                                                                    ).switchIfEmpty(apiCallService.getResponseMsg(financialCostCenterJson)
//                                                                                                            .flatMap(this::responseInfoMsg))
//                                                                                                    .onErrorResume(err -> apiCallService.getResponseMsg(financialCostCenterJson)
//                                                                                                            .flatMap(this::responseInfoMsg)))
//                                                                                            ).switchIfEmpty(responseInfoMsg("Student Financial Module does not exist"))
//                                                                                            .onErrorResume(er -> responseErrorMsg("Student Financial Module does not exist.Please Contact Developer.")));
//                                                                }).switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
//                                                    } else {
//                                                        return getMappedRecords(courseOfferedList, "Record Already Exists");
//                                                    }
//                                                }).switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
//                                    } else {
//                                        return responseInfoMsg("Select Course Offered First");
//                                    }
//                                }).switchIfEmpty(responseInfoMsg("Campus record does not exist"))
//                                .onErrorResume(err -> responseInfoMsg("Campus record does not exist.Please Contact Developer"))
//
//                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer"));
//    }
//
//    // delete previous records, if some error occurred
//    public Mono<String> deletePreviousRecords(JsonNode jsonNode, String url, String userId) {
//        List<String> delList = new ArrayList<>();
//        try {
//            delList = apiCallService.getListUUID(jsonNode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return apiCallService.deleteMultipleRecordWithQueryParams(url, userId, "uuid", delList)
//                .flatMap(delJsonNode -> apiCallService.getUUID(delJsonNode));
//
//    }
//
//    // show the list of mapped courses in response
//    public Mono<ServerResponse> getMappedRecords(List<UUID> resultList, String msg) {
//
//        return courseOfferedRepository.findAllByUuidInAndDeletedAtIsNull(resultList)
//                .collectList()
//                .flatMap(courseOfferedData -> {
//                    List<UUID> courseEntityFinalList = new ArrayList<>();
//                    for (CourseOfferedEntity offerCourse : courseOfferedData) {
//                        courseEntityFinalList.add(offerCourse.getCourseUUID());
//                    }
//
//                    return courseRepository.findAllByUuidInAndDeletedAtIsNull(courseEntityFinalList)
//                            .collectList()
//                            .flatMap(stdGroupRecords -> responseSuccessMsg(msg, stdGroupRecords));
//                });
//    }
//
//    // used in custom query to generate the string from list
//    public String getUuidList(List<UUID> uuidList) {
//        String uuidString = "";
//        for (UUID uuid : uuidList) {
//            if (uuid.equals(uuidList.get(uuidList.size() - 1))) {
//                uuidString = uuidString + "'" + uuid + "'";
//            } else {
//                uuidString = uuidString + "'" + uuid + "', ";
//            }
//        }
//        return uuidString;
//    }
//
//    // convert object to string
//    public String objectToString(Object obj) {
//        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
//        String str = "";
//        try {
//            str = objectWriter.writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return str;
//    }
//
//
//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        final UUID campusUUID = UUID.fromString(serverRequest.pathVariable("campusUUID"));
//        UUID courseOfferedUUID = UUID.fromString(serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse(""));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown user");
//        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//            return responseWarningMsg("Unknown user");
//        }
//
//        return courseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
//                .flatMap(courseOfferedEntity -> campusCourseOfferedPvtRepository.findFirstByCampusUUIDAndCourseOfferedUUIDAndDeletedAtIsNull(campusUUID, courseOfferedUUID)
//                        .flatMap(campusPvtEntity -> {
//                            campusPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                            campusPvtEntity.setDeletedBy(UUID.fromString(userId));
//                            return campusCourseOfferedPvtRepository.save(campusPvtEntity)
//                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", courseOfferedEntity))
//                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
//                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
//                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Course Offered does not exist"))
//                .onErrorResume(err -> responseErrorMsg("Course Offered does not exist.Please Contact Developer."));
//    }

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
