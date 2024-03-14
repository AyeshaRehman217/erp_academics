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
import tuf.webscaf.app.dbContext.master.entity.StudentFatherJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherJobHistoryEntity;
import tuf.webscaf.app.http.handler.StudentFatherJobHistoryHandler;
import tuf.webscaf.app.http.validationFilters.studentFatherJobHistoryHandler.*;
import tuf.webscaf.springDocImpl.JobHistoryDocImpl;
import tuf.webscaf.springDocImpl.StatusDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class StudentFatherJobHistoriesRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by createdAt"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with occupation, organization or designation"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentFatherUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record with given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student/student-father/student-father-job-histories/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "showJobHistoryForStudentFatherAndStudent",
                            operation = @Operation(
                                    operationId = "showJobHistoryForStudentFatherAndStudent",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful Operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record Against the given Student , father and father Job History UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentFatherUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/currency/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "getCurrencyUUID",
                            operation = @Operation(
                                    operationId = "getCurrencyUUID",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "This Route is used by delete function of Currency handler in Config Module to Check " +
                                            "If Currency Exists in Student Father Job History",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Student Father Job History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StudentFatherJobHistoryEntity.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "update",
                            operation = @Operation(
                                    operationId = "update",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Update the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                    },
                                    requestBody = @RequestBody(
                                            description = "Update Student Father Job History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = JobHistoryDocImpl.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/delete/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete the Record with given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-father-job-histories/status/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = StudentFatherJobHistoryHandler.class,
                            beanMethod = "status",
                            operation = @Operation(
                                    operationId = "status",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentFatherJobHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Update the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
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
                    )
            }
    )
    public RouterFunction<ServerResponse> studentFatherJobHistoryRoutes(StudentFatherJobHistoryHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-father-job-histories/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexStudentFatherJobHistoryHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-father-job-histories/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowStudentFatherJobHistoryHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/student/student-father/student-father-job-histories/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showJobHistoryForStudentFatherAndStudent).filter(new ShowStudentFatherJobAgainstFatherAndStudentHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/student-father-job-histories/currency/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::getCurrencyUUID).filter(new ShowStudentFatherJobHistoryHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-father-job-histories/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentFatherJobHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/student-father-job-histories/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateStudentFatherJobHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/student-father-job-histories/status/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::status).filter(new ShowStudentFatherJobHistoryHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-father-job-histories/delete/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowStudentFatherJobHistoryHandlerFilter()));
    }
}
