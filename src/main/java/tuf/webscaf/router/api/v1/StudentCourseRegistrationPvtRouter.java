package tuf.webscaf.router.api.v1;

import org.springframework.context.annotation.Configuration;


@Configuration
public class StudentCourseRegistrationPvtRouter {

//    @Bean
//    @RouterOperations(
//            {
//                    @RouterOperation(
//                            path = "/academic/api/v1/student-registered-courses/existing/show/{studentUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = StudentCourseRegistrationPvtHandler.class,
//                            beanMethod = "showCourseOfferedAgainstStudents",
//                            operation = @Operation(
//                                    operationId = "showCourseOfferedAgainstStudents",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveStudentRegistrationCourseDto.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Records not found!",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Show Courses In Which Student is not Registered Yet Against a Given student",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "dp"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "skw"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "status")
//                                    }
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/student-registered-courses/mapped/show/{studentUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = StudentCourseRegistrationPvtHandler.class,
//                            beanMethod = "showMappedCoursesAgainstStudent",
//                            operation = @Operation(
//                                    operationId = "showMappedCoursesAgainstStudent",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveStudentRegistrationCourseDto.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Show Courses That Are Mapped With Given Student",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "dp"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "skw"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "status")
//                                    }
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/student-registered-courses/store",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.POST,
//                            beanClass = StudentCourseRegistrationPvtHandler.class,
//                            beanMethod = "store",
//                            operation = @Operation(
//                                    operationId = "store",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = CourseEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Records not found!",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    requestBody = @RequestBody(
//                                            description = "Create Course for Student UUID",
//                                            required = true,
//                                            content = @Content(
//                                                    mediaType = "application/x-www-form-urlencoded",
//                                                    encoding = {
//                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                                    },
//                                                    schema = @Schema(type = "object", implementation = StudentCourseRegistrationPvtEntity.class)
//                                            )),
//                                    description = "Store Course for Student UUID",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
//                                    }
//                            )
//                    ), @RouterOperation(
//                    path = "/academic/api/v1/student-registered-courses/update/{studentUUID}",
//                    produces = {
//                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                    },
//                    method = RequestMethod.POST,
//                    beanClass = StudentCourseRegistrationPvtHandler.class,
//                    beanMethod = "update",
//                    operation = @Operation(
//                            operationId = "update",
//                            security = {@SecurityRequirement(name = "bearer")},
//                            responses = {
//                                    @ApiResponse(
//                                            responseCode = "200",
//                                            description = "successful operation",
//                                            content = @Content(schema = @Schema(
//                                                    implementation = CourseEntity.class
//                                            ))
//                                    ),
//                                    @ApiResponse(responseCode = "404", description = "Records not found!",
//                                            content = @Content(schema = @Schema(hidden = true))
//                                    )
//                            },
//                            requestBody = @RequestBody(
//                                    description = "Update Courses for Student UUID",
//                                    required = true,
//                                    content = @Content(
//                                            mediaType = "application/x-www-form-urlencoded",
//                                            encoding = {
//                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                            },
//                                            schema = @Schema(type = "object", implementation = StudentCourseRegistrationDocImpl.class)
//                                    )),
//                            description = "Update Course for a Given Student UUID",
//                            parameters = {
//                                    @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
//                                    @Parameter(in = ParameterIn.HEADER, name = "auid")
//                            }
//                    )
//            ), @RouterOperation(
//                    path = "/academic/api/v1/student-registered-courses/delete/{studentUUID}",
//                    produces = {
//                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                    },
//                    method = RequestMethod.DELETE,
//                    beanClass = StudentCourseRegistrationPvtHandler.class,
//                    beanMethod = "delete",
//                    operation = @Operation(
//                            operationId = "delete",
//                            security = {@SecurityRequirement(name = "bearer")},
//                            responses = {
//                                    @ApiResponse(
//                                            responseCode = "200",
//                                            description = "successful operation",
//                                            content = @Content(schema = @Schema(
//                                                    implementation = CourseEntity.class
//                                            ))
//                                    ),
//                                    @ApiResponse(responseCode = "404", description = "Records not found!",
//                                            content = @Content(schema = @Schema(hidden = true))
//                                    )
//                            },
//                            description = "Delete Course for Student",
//                            parameters = {
//                                    @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
//                                    @Parameter(in = ParameterIn.QUERY, name = "courseOfferedUUID"),
//                                    @Parameter(in = ParameterIn.HEADER, name = "auid")
//                            }
//                    )
//            )
//            }
//    )
//
//    public RouterFunction<ServerResponse> studentCourseRegistrationPvtRoutes(StudentCourseRegistrationPvtHandler handle) {
//        return RouterFunctions.route(GET("academic/api/v1/student-registered-courses/existing/show/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showCourseOfferedAgainstStudents).filter(new ShowStudentCourseRegistrationHandlerFilter())
//                .and(RouterFunctions.route(GET("academic/api/v1/student-registered-courses/mapped/show/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedCoursesAgainstStudent).filter(new ShowStudentCourseRegistrationHandlerFilter()))
//                .and(RouterFunctions.route(POST("academic/api/v1/student-registered-courses/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentCourseRegistrationHandlerFilter()))
//                .and(RouterFunctions.route(POST("academic/api/v1/student-registered-courses/update/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateStudentCourseRegistrationHandlerFilter()))
//                .and(RouterFunctions.route(DELETE("academic/api/v1/student-registered-courses/delete/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentCourseRegistrationHandlerFilter()));
//    }

}
