package tuf.webscaf.router.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildAcademicHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildAcademicHistoryEntity;
import tuf.webscaf.app.http.handler.TeacherChildAcademicHistoryHandler;
import tuf.webscaf.app.http.validationFilters.teacherChildAcademicHistoryHandler.IndexTeacherChildAcademicHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherChildAcademicHistoryHandler.ShowTeacherChildAcademicHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherChildAcademicHistoryHandler.StoreTeacherChildAcademicHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherChildAcademicHistoryHandler.UpdateTeacherChildAcademicHistoryHandlerFilter;
import tuf.webscaf.springDocImpl.AcademicHistoryDocImpl;
import tuf.webscaf.springDocImpl.StatusDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherChildAcademicHistoriesRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Get the Records With Pagination",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by createdAt"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with grade"),
                                            @Parameter(in = ParameterIn.QUERY, name = "teacherChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/country/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "getCountryUUID",
                            operation = @Operation(
                                    operationId = "getCountryUUID",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "This Route is Used by Delete Function of Country Handler in Config Module to Check " +
                                            "If Country Exists in Teacher Child Academic History",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/state/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "getStateUUID",
                            operation = @Operation(
                                    operationId = "getStateUUID",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "This Route is Used by Delete Function of State Handler in Config Module to Check " +
                                            "If State Exists in Teacher Child Academic History",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/city/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "getCityUUID",
                            operation = @Operation(
                                    operationId = "getCityUUID",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "This Route is Used by Delete Function of City Handler in Config Module to Check " +
                                            "If City Exists in Teacher Child Academic History",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    parameters = {
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Child Academic History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherChildAcademicHistoryEntity.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "update",
                            operation = @Operation(
                                    operationId = "update",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    },
                                    description = "Update the Record for given uuid",
                                    requestBody = @RequestBody(
                                            description = "Update Teacher Child Academic History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = AcademicHistoryDocImpl.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/status/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "status",
                            operation = @Operation(
                                    operationId = "status",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Update the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    },
                                    requestBody = @RequestBody(
                                            description = "Update the Status",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StatusDocImpl.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-academic-histories/delete/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = TeacherChildAcademicHistoryHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherChildAcademicHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    )
            }
    )
    public RouterFunction<ServerResponse> teacherChildAcademicHistoryRoutes(TeacherChildAcademicHistoryHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/teacher-child-academic-histories/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexTeacherChildAcademicHistoryHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-child-academic-histories/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-child-academic-histories/country/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::getCountryUUID).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-child-academic-histories/state/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::getStateUUID).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-child-academic-histories/city/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::getCityUUID).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/teacher-child-academic-histories/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/teacher-child-academic-histories/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/teacher-child-academic-histories/status/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::status).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/teacher-child-academic-histories/delete/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowTeacherChildAcademicHistoryHandlerFilter()));
    }
}
