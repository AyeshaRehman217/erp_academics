//package tuf.webscaf.app.http.handler;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.master.repositry.CourseOfferedRepository;
//import tuf.webscaf.app.dbContext.master.repositry.CourseRepository;
//import tuf.webscaf.app.dbContext.master.repositry.StudentCourseRegistrationPvtRepository;
//import tuf.webscaf.app.dbContext.master.repositry.StudentRepository;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentCourseRegistrationPvtRepository;
//import tuf.webscaf.app.service.ApiCallService;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.util.List;
//
//@Component
//@Tag(name = "studentCourseRegistrationHandler")
//public class StudentCourseRegistrationPvtHandler {
//
//    @Value("${server.zone}")
//    private String zone;
//
//    @Autowired
//    CustomResponse appresponse;
//
//    @Autowired
//    StudentCourseRegistrationPvtRepository studentCourseRegistrationPvtRepository;
//
//    @Autowired
//    SlaveStudentCourseRegistrationPvtRepository slaveStudentCourseRegistrationPvtRepository;
//
//    @Autowired
//    StudentRepository studentRepository;
//
//    @Autowired
//    CourseOfferedRepository courseOfferedRepository;
//
//    @Autowired
//    CourseRepository courseRepository;
//
//    @Autowired
//    ApiCallService apiCallService;
//
//    @Value("${server.ssl-status}")
//    private String sslStatus;
//
//    @Value("${server.erp_student_financial_module.uri}")
//    private String studentFinancialModuleUri;
//
//
////    public Mono<ServerResponse> showCourseOfferedAgainstStudents(ServerRequest serverRequest) {
////
////        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));
////
////        Optional<String> status = serverRequest.queryParam("status");
////
////        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
////
////        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
////        if (size > 100) {
////            size = 100;
////        }
////        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
////        int page = pageRequest - 1;
////
////        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
////        Sort.Direction direction;
////        switch (d.toLowerCase()) {
////            case "asc":
////                direction = Sort.Direction.ASC;
////                break;
////            case "desc":
////                direction = Sort.Direction.DESC;
////                break;
////            default:
////                direction = Sort.Direction.ASC;
////        }
////
////        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
////        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
////
////        if (status.isPresent()) {
////            Flux<SlaveStudentRegistrationCourseDto> slaveCourseListResultFlux = slaveStudentCourseRegistrationPvtRepository
////                    .existingCourseListWithStatus(studentUUID, Boolean.valueOf(status.get()), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
////
////            return slaveCourseListResultFlux
////                    .collectList()
////                    .flatMap(courseEntity -> slaveStudentCourseRegistrationPvtRepository.countExistingCoursesForStudentRegistrationWithStatus(studentUUID, Boolean.valueOf(status.get()), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
////                            .flatMap(count -> {
////                                if (courseEntity.isEmpty()) {
////                                    return responseIndexInfoMsg("Record does not exist", count);
////
////                                } else {
////
////                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
////                                }
////                            })
////                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
////                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
////        } else {
////            Flux<SlaveStudentRegistrationCourseDto> slaveCourseListResultFlux = slaveStudentCourseRegistrationPvtRepository
////                    .existingCourseList(studentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
////
////            return slaveCourseListResultFlux
////                    .collectList()
////                    .flatMap(courseEntity -> slaveStudentCourseRegistrationPvtRepository.countExistingCoursesForStudentRegistration(studentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
////                            .flatMap(count -> {
////                                if (courseEntity.isEmpty()) {
////                                    return responseIndexInfoMsg("Record does not exist", count);
////
////                                } else {
////
////                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
////                                }
////                            })
////                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
////                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
////        }
////
////    }
////
////    public Mono<ServerResponse> showMappedCoursesAgainstStudent(ServerRequest serverRequest) {
////
////        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));
////
////        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
////
////        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
////        if (size > 100) {
////            size = 100;
////        }
////        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
////        int page = pageRequest - 1;
////
////        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
////        Sort.Direction direction;
////        switch (d.toLowerCase()) {
////            case "asc":
////                direction = Sort.Direction.ASC;
////                break;
////            case "desc":
////                direction = Sort.Direction.DESC;
////                break;
////            default:
////                direction = Sort.Direction.ASC;
////        }
////
////        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
////        Optional<String> status = serverRequest.queryParam("status");
////
////        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
////
////        if (status.isPresent()) {
////            Flux<SlaveStudentRegistrationCourseDto> slaveCourseRegistrationFlux = slaveStudentCourseRegistrationPvtRepository
////                    .showMappedCourseListAgainstStudentWithStatus(studentUUID, Boolean.valueOf(status.get()), searchKeyWord,
////                            searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
////
////            return slaveCourseRegistrationFlux
////                    .collectList()
////                    .flatMap(hobbyEntity -> slaveStudentCourseRegistrationPvtRepository
////                            .countMappedStudentCoursesWithStatus(studentUUID, Boolean.valueOf(status.get()), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
////                            .flatMap(count -> {
////                                if (hobbyEntity.isEmpty()) {
////                                    return responseIndexInfoMsg("Record does not exist", count);
////
////                                } else {
////
////                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
////                                }
////                            })
////                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
////                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
////        } else {
////            Flux<SlaveStudentRegistrationCourseDto> slaveCourseRegistrationFlux = slaveStudentCourseRegistrationPvtRepository
////                    .showMappedCourseListAgainstStudent(studentUUID, searchKeyWord,
////                            searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
////
////            return slaveCourseRegistrationFlux
////                    .collectList()
////                    .flatMap(hobbyEntity -> slaveStudentCourseRegistrationPvtRepository.countMappedStudentCourses(studentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
////                            .flatMap(count -> {
////                                if (hobbyEntity.isEmpty()) {
////                                    return responseIndexInfoMsg("Record does not exist", count);
////
////                                } else {
////
////                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
////                                }
////                            })
////                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
////                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
////        }
////    }
////
////    public Mono<ServerResponse> store(ServerRequest serverRequest) {
////        String userId = serverRequest.headers().firstHeader("auid");
////
////
////        if (userId == null) {
////            return responseWarningMsg("Unknown user");
////        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
////            return responseWarningMsg("Unknown user");
////        }
////
////
////        return serverRequest.formData()
////
////                .flatMap(value -> {
////
////                    StudentCourseRegistrationPvtEntity studentCourseRegistrationPvtEntity = StudentCourseRegistrationPvtEntity
////                            .builder()
////                            .uuid(UUID.randomUUID())
////                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
////                            .courseOfferedUUID(UUID.fromString(value.getFirst("courseOfferedUUID").trim()))
////                            .rollNo(value.getFirst("rollNo").trim())
////                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
////                            .createdBy(UUID.fromString(userId))
////                            .build();
////
////                    //Check if Student Record Exists
////                    return studentRepository.findByUuidAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getStudentUUID())
////                            //Check if entered Course Offered Exists
////                            .flatMap(studentEntity -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getCourseOfferedUUID())
////                                    //Check if Entered The Entered Student is Already Registered Against this Course Offered
////                                    .flatMap(courseOfferedEntity -> studentCourseRegistrationPvtRepository.findFirstByCourseOfferedUUIDAndStudentUUIDAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getCourseOfferedUUID(), studentCourseRegistrationPvtEntity.getStudentUUID())
////                                            .flatMap(checkCourseRecordMsg -> responseInfoMsg("This Course Against Student Already Exists."))
////                                            //If The Entered Student and Course Offered Does not Exist Already then get Student Roll No Against the Entered Course Offered
////                                            .switchIfEmpty(Mono.defer(() -> studentCourseRegistrationPvtRepository.findAllByCourseOfferedUUIDAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getCourseOfferedUUID())
////                                                    .collectList()
////                                                    .flatMap(stdCourseEntityDB -> {
////
////                                                        // Empty List of Roll No For the Specific/same Course Offered
////                                                        List<String> courseRollNoList = new ArrayList<String>();
////
////                                                        for (StudentCourseRegistrationPvtEntity courseRegistration : stdCourseEntityDB) {
////                                                            //check if roll No exists with given student against the same Course
////                                                            if ((value.getFirst("rollNo").trim()).equals(courseRegistration.getRollNo())) {
////                                                                courseRollNoList.add(courseRegistration.getRollNo());
////                                                            }
////                                                        }
////
////                                                        //   if rollNo exist in any of records with given Course Offered
////                                                        if (!courseRollNoList.isEmpty()) {
////                                                            return responseInfoMsg("This RollNo. has already been assigned to student against this Course.Enter Another One.");
////                                                        }
////
////                                                        //get The Given Course Entity Against the Registered Course Offered
////                                                        return courseRepository.findByUuidAndDeletedAtIsNull(courseOfferedEntity.getCourseUUID())
////                                                                //Save Student Course Registration Record
////                                                                .flatMap(courseEntity -> studentCourseRegistrationPvtRepository.save(studentCourseRegistrationPvtEntity)
////                                                                        //Check if Student Financial Module Exists
////                                                                        .flatMap(stdCourseSaveEntity -> apiCallService.getData(studentFinancialModuleUri + "api/v1/info/show")
////                                                                                .flatMap(moduleJson -> apiCallService.getModuleId(moduleJson)
////                                                                                        // If Student Financial Module Exists Store Student Against the Student Group
////                                                                                        //The Student Group Will be Fetched if the Entered Course Offered Matched with the Course Offered in Financial Student Group in Other Module
////                                                                                        .flatMap(stdFinancialModule -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-groups/groups/show/", stdCourseSaveEntity.getCourseOfferedUUID())
////                                                                                                .flatMap(financialStdGroupJson -> apiCallService.getUUID(financialStdGroupJson)
////                                                                                                        .flatMap(checkMsg -> {
////                                                                                                                    //Adding Student UUID in form Data from the Stored Student Registered Course
////                                                                                                                    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
////                                                                                                                    formData.add("studentUUID", stdCourseSaveEntity.getStudentUUID().toString());
////
////                                                                                                                    //Get the Student Group UUID from Financial Student Group Where the Course Offered Matches
////                                                                                                                    return apiCallService.getUUID(financialStdGroupJson)
////                                                                                                                            .flatMap(stdGroupUUID -> {
////                                                                                                                                        //If The Student Group UUID  match OR not equals to Null
////                                                                                                                                        //Post Data to Financial Student Group Student Pvt Handler in Student Financial Module
////                                                                                                                                        if (!stdGroupUUID.equals("") || !stdGroupUUID.equals(null)) {
////                                                                                                                                            return apiCallService.postData(formData, studentFinancialModuleUri + "api/v1/financial-student-group-student/store/", userId, stdGroupUUID)
////                                                                                                                                                    .flatMap(saveData -> responseSuccessMsg("Record Stored Successfully", courseEntity))
////                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
////                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
////                                                                                                                                        } else {
////                                                                                                                                            //else Find the Already Saved Record and Hard Delete the Records
////                                                                                                                                            return studentCourseRegistrationPvtRepository.findByUuidAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getUuid())
////                                                                                                                                                    .flatMap(deleteRegisteredCourse -> studentCourseRegistrationPvtRepository.delete(deleteRegisteredCourse)
////                                                                                                                                                            .flatMap(delMsg -> responseInfoMsg("Unable to Store Record There is some Error in Financial"))
////                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Requested Student Course Registration does not exist.")))
////                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Requested Student Course Registration does not exist. Please Contact Developer."));
////                                                                                                                                        }
////                                                                                                                                    }
////                                                                                                                            ).switchIfEmpty(responseInfoMsg("Unable to Store Record There is something wrong Please Try Again"))
////                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
////                                                                                                                }
////                                                                                                        ).switchIfEmpty(Mono.defer(() ->
////                                                                                                                //if The Module Status is Error From Student Financial Module then Find the Entered Student Course Registration Data
////                                                                                                                //and Hard delete the Record
////                                                                                                                studentCourseRegistrationPvtRepository.findByUuidAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getUuid())
////                                                                                                                        .flatMap(deleteRegisteredCourse -> studentCourseRegistrationPvtRepository.delete(deleteRegisteredCourse)
////                                                                                                                                .flatMap(delMsg -> responseInfoMsg("Financial Student Group Does not Exist Against Course Offered"))
////                                                                                                                                .switchIfEmpty(responseInfoMsg("Financial Student Group Does not Exist Against Course Offered."))
////                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Financial Student Group Does not Exist Against Course Offered. Please Contact Developer."))
////                                                                                                                        )))
////                                                                                                ))
////                                                                                ).switchIfEmpty(responseInfoMsg("Module Does not Exist"))
////                                                                                .onErrorResume(ex -> responseErrorMsg("Error Connecting With Module.Please Contact Developer."))
////                                                                        ).switchIfEmpty(Mono.defer(() ->
////                                                                                //if The Module Status is Error From Student Financial Module then Find the Entered Student Course Registration Data
////                                                                                //and Hard delete the Record
////                                                                                studentCourseRegistrationPvtRepository.findByUuidAndDeletedAtIsNull(studentCourseRegistrationPvtEntity.getUuid())
////                                                                                        .flatMap(deleteRegisteredCourse -> studentCourseRegistrationPvtRepository.delete(deleteRegisteredCourse)
////                                                                                                .flatMap(delMsg -> responseInfoMsg("Unable to Store Record There is some Error in Financial"))
////                                                                                                .switchIfEmpty(responseInfoMsg("Requested Student Course Registration does not exist."))
////                                                                                                .onErrorResume(ex -> responseErrorMsg("Requested Student Course Registration does not exist. Please Contact Developer."))
////                                                                                        )))
////                                                                );
////                                                    })
////                                            ))
////                                    )
////                                    .switchIfEmpty(responseInfoMsg("Course Does not Exist."))
////                                    .onErrorResume(ex -> responseErrorMsg("Course Does not Exist.Please Contact Developer."))
////                            )
////                            .switchIfEmpty(responseInfoMsg("Student Does not Exist."))
////                            .onErrorResume(ex -> responseErrorMsg("Student Does not Exist.Please Contact Developer."));
////
////                }).switchIfEmpty(responseInfoMsg("Unable to Read Request"))
////                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
////    }
////
////    public Mono<ServerResponse> update(ServerRequest serverRequest) {
////
////        String userId = serverRequest.headers().firstHeader("auid");
////
////        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));
////
////        if (userId == null) {
////            return responseWarningMsg("Unknown user");
////        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
////            return responseWarningMsg("Unknown user");
////        }
////
////
////        // Empty List of Roll No For the Specific/same Course
////        List<String> courseRollNoList = new ArrayList<String>();
////
////        return serverRequest.formData()
////                .flatMap(value -> studentCourseRegistrationPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
////                                .flatMap(previousStdEntity -> {
////
////                                    StudentCourseRegistrationPvtEntity updatedEntity = StudentCourseRegistrationPvtEntity
////                                            .builder()
////                                            .uuid(previousStdEntity.getUuid())
////                                            .studentUUID(previousStdEntity.getStudentUUID())
////                                            .courseOfferedUUID(previousStdEntity.getCourseOfferedUUID())
////                                            .rollNo(value.getFirst("rollNo").trim())
////                                            .createdAt(previousStdEntity.getCreatedAt())
////                                            .createdBy(previousStdEntity.getCreatedBy())
////                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
////                                            .updatedBy(UUID.fromString(userId))
////                                            .build();
////
////                                    previousStdEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
////                                    previousStdEntity.setDeletedBy(UUID.fromString(userId));
////
//////                            return studentCourseRegistrationPvtRepository.findFirstByCourseOfferedUUIDAndStudentUUIDIsNot(updatedEntity.getCourseOfferedUUID(), studentUUID)
//////                                    .flatMap(checkStudentCourse -> responseInfoMsg("This Course Already Exists for this student"))
//////                                    .switchIfEmpty(Mono.defer(() ->
////                                    return courseOfferedRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseOfferedUUID())
////                                            .flatMap(courseOfferedEntity -> courseRepository.findByUuidAndDeletedAtIsNull(courseOfferedEntity.getCourseUUID())
////                                                    .flatMap(courseEntity -> studentCourseRegistrationPvtRepository.findAllByCourseOfferedUUIDAndDeletedAtIsNull(updatedEntity.getCourseOfferedUUID())
////                                                            .collectList()
////                                                            .flatMap(stdCourseRegistrationEntity -> {
////
////                                                                for (StudentCourseRegistrationPvtEntity courseRegistration : stdCourseRegistrationEntity) {
////                                                                    //check if roll No exists with given student against the same Course
////                                                                    if ((value.getFirst("rollNo").trim()).equals(courseRegistration.getRollNo())) {
////                                                                        courseRollNoList.add(courseRegistration.getRollNo());
////                                                                    }
////                                                                }
////
////                                                                //   if rollNo exist in any of records with given Course
////                                                                if (!courseRollNoList.isEmpty()) {
////                                                                    return responseInfoMsg("This RollNo. has already been assigned to student against this Course.Enter Another One.");
////                                                                }
////                                                                return studentCourseRegistrationPvtRepository.save(previousStdEntity)
////                                                                        .then(studentCourseRegistrationPvtRepository.save(updatedEntity))
////                                                                        .flatMap(stdCourseEntity -> responseSuccessMsg("Record Updated Successfully.", courseEntity))
////                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record,There is something wrong please try again."))
////                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.There is something wrong please try again."));
////                                                            })
////                                                    )
////                                            ).switchIfEmpty(responseInfoMsg("Course Does not exist."))
////                                            .onErrorResume(ex -> responseErrorMsg("Course Does not exist.Please Contact Developer."));
//////                                    ));
////                                })
////                                .switchIfEmpty(responseInfoMsg("Student Does not exist."))
////                                .onErrorResume(ex -> responseErrorMsg("Student Does not exist.Please Contact Developer."))
////                ).switchIfEmpty(responseInfoMsg("Unable to Read Request."))
////                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
////
////    }
////
////
////    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
////
////        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));
////
////        UUID courseOfferedUUID = UUID.fromString(serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse(""));
////        String userId = serverRequest.headers().firstHeader("auid");
////
////        if (userId == null) {
////            return responseWarningMsg("Unknown user");
////        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
////            return responseWarningMsg("Unknown user");
////        }
////
////        return studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
////                .flatMap(studentEntity -> studentCourseRegistrationPvtRepository.findFirstByStudentUUIDAndCourseOfferedUUIDAndDeletedAtIsNull(studentUUID, courseOfferedUUID)
////                        .flatMap(stdCoursePvtEntity -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(stdCoursePvtEntity.getCourseOfferedUUID())
////                                .flatMap(courseOfferedEntity -> apiCallService.getData(studentFinancialModuleUri + "api/v1/info/show")
////                                                .flatMap(moduleJson -> apiCallService.getModuleId(moduleJson)
////                                                        .flatMap(moduleCheck -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-groups/groups/show/", stdCoursePvtEntity.getCourseOfferedUUID())
////                                                                .flatMap(financialStdGroupJson -> apiCallService.getUUID(financialStdGroupJson)
////                                                                        .flatMap(stdGroupUUID -> apiCallService.deleteRecordWithQueryParam(studentFinancialModuleUri + "api/v1/financial-student-group-student/delete/", stdGroupUUID, userId, "studentUUID", stdCoursePvtEntity.getStudentUUID().toString())
////                                                                                .flatMap(deleteStdCourseEntity -> {
////                                                                                    stdCoursePvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
////                                                                                    stdCoursePvtEntity.setDeletedBy(UUID.fromString(userId));
////
////                                                                                    return courseRepository.findByUuidAndDeletedAtIsNull(courseOfferedEntity.getCourseUUID())
////                                                                                            .flatMap(courseEntity -> studentCourseRegistrationPvtRepository.save(stdCoursePvtEntity)
////                                                                                                    .flatMap(stdRegEntity -> responseSuccessMsg("Record Deleted Successfully", courseEntity))
////                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
////                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer.")));
////                                                                                })
////                                                                        )
////                                                                ).switchIfEmpty(responseInfoMsg("Student Group Does not exist Against Course offered."))
////                                                                .onErrorResume(ex -> responseErrorMsg("Student Group Does not exist Against Course offered.Please Contact Developer."))
////                                                        )
////                                                        .switchIfEmpty(responseInfoMsg("Module Does not Exist."))
////                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Connect to Module.Please Contact Developer."))
////                                                ).switchIfEmpty(responseInfoMsg("Module Does not Exist."))
////                                                .onErrorResume(ex -> responseErrorMsg("Unable to Connect to Module.Please Contact Developer."))
////                                        //Check if Module Does not Exist
////                                ).switchIfEmpty(responseInfoMsg("Unable to Connect to Student Financial Module.Please Contact Developer."))
////                                .onErrorResume(ex -> responseErrorMsg("Unable to Connect to Student Financial Module.Please Contact Developer."))
////                        ).switchIfEmpty(responseInfoMsg("Record does not exist"))
////                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
////                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
////                .onErrorResume(err -> responseErrorMsg("Student Record does not exist.Please Contact Developer."));
////
////    }
//
//    public Mono<ServerResponse> responseInfoMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.empty()
//
//        );
//    }
//
//
//    public Mono<ServerResponse> responseErrorMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.ERROR,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//
//    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg)
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg)
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseWarningMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.WARNING,
//                        msg)
//        );
//
//
//        return appresponse.set(
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                HttpStatus.UNPROCESSABLE_ENTITY.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//}
