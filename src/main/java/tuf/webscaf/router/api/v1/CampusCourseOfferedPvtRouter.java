package tuf.webscaf.router.api.v1;

import org.springframework.context.annotation.Configuration;


@Configuration
public class CampusCourseOfferedPvtRouter {

//    @Bean
//    @RouterOperations(
//            {
//                    @RouterOperation(
//                            path = "/academic/api/v1/campus-course-offered/existing/show/{campusUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = CampusCourseOfferedPvtHandler.class,
//                            beanMethod = "showCourseOfferedAgainstCampus",
//                            operation = @Operation(
//                                    operationId = "showCourseOfferedAgainstCampus",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveCourseEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Records not found!",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Show Course Offered Against a Given Campus",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "campusUUID"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "academicSessionUUID"),
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
//                            path = "/academic/api/v1/campus-course-offered/mapped/show/{campusUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = CampusCourseOfferedPvtHandler.class,
//                            beanMethod = "showMappedCourseOfferedAgainstCampus",
//                            operation = @Operation(
//                                    operationId = "showMappedCourseOfferedAgainstCampus",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveCourseEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Show Course Offered That Are Mapped With Given Campus",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "campusUUID"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "academicSessionUUID"),
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
//                            path = "/academic/api/v1/campus-course-offered/store/{campusUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.POST,
//                            beanClass = CampusCourseOfferedPvtHandler.class,
//                            beanMethod = "store",
//                            operation = @Operation(
//                                    operationId = "store",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveCourseEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Records not found!",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    requestBody = @RequestBody(
//                                            description = "Create Course Offered for a Campus",
//                                            required = true,
//                                            content = @Content(
//                                                    mediaType = "application/x-www-form-urlencoded",
//                                                    encoding = {
//                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                                    },
//                                                    schema = @Schema(type = "object", implementation = CourseOfferedDocImpl.class)
//                                            )),
//                                    description = "Store Course Offered Against a Given Campus",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "campusUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
//                                    }
//                            )
//                    ),
//
//                    @RouterOperation(
//                            path = "/academic/api/v1/campus-course-offered/delete/{campusUUID}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.DELETE,
//                            beanClass = CampusCourseOfferedPvtHandler.class,
//                            beanMethod = "delete",
//                            operation = @Operation(
//                                    operationId = "delete",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveCourseEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Records not found!",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Delete Course Offered Against a Given Campus",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "campusUUID"),
//                                            @Parameter(in = ParameterIn.QUERY, name = "courseOfferedUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
//                                    }
//                            )
//                    )
//            }
//    )
//
//    public RouterFunction<ServerResponse> campusCourseOfferedPvtRoutes(CampusCourseOfferedPvtHandler handle) {
//        return RouterFunctions.route(GET("academic/api/v1/campus-course-offered/existing/show/{campusUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showCourseOfferedAgainstCampus).filter(new ShowCampusDepartmentHandlerFilter())
//                .and(RouterFunctions.route(GET("academic/api/v1/campus-course-offered/mapped/show/{campusUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedCourseOfferedAgainstCampus).filter(new ShowCampusDepartmentHandlerFilter()))
//                .and(RouterFunctions.route(POST("academic/api/v1/campus-course-offered/store/{campusUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreCampusCourseHandlerFilter()))
//                .and(RouterFunctions.route(DELETE("academic/api/v1/campus-course-offered/delete/{campusUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete));
//    }

}
