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
import tuf.webscaf.app.dbContext.master.entity.StudentChildFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildFinancialHistoryEntity;
import tuf.webscaf.app.http.handler.StudentChildFinancialHistoryHandler;
import tuf.webscaf.app.http.validationFilters.studentChildFinancialHistoryHandler.IndexStudentChildFinancialHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentChildFinancialHistoryHandler.ShowStudentChildFinancialHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentChildFinancialHistoryHandler.StoreStudentChildFinancialHistoryHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentChildFinancialHistoryHandler.UpdateStudentChildFinancialHistoryHandlerFilter;
import tuf.webscaf.springDocImpl.FinancialHistoryDocImpl;
import tuf.webscaf.springDocImpl.StatusDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class StudentChildFinancialHistoriesRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
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
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with asset name or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
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
                            path = "/academic/api/v1/student-child-financial-histories/currency/show/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "getCurrencyUUID",
                            operation = @Operation(
                                    operationId = "getCurrencyUUID",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "This Route is used by delete function of Currency handler in Config Module to Check " +
                                            "If Currency Exists in Student Child Financial History",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Student Child Financial History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StudentChildFinancialHistoryEntity.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "update",
                            operation = @Operation(
                                    operationId = "update",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                    },
                                    description = "Update the Record for given uuid",
                                    requestBody = @RequestBody(
                                            description = "Update Student Child Financial History",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = FinancialHistoryDocImpl.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/status/update/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "status",
                            operation = @Operation(
                                    operationId = "status",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
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
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-financial-histories/delete/{uuid}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentChildFinancialHistoryHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentChildFinancialHistoryEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete the Record for given uuid",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "uuid"),
                                    }
                            )
                    )
            }
    )
    public RouterFunction<ServerResponse> studentChildFinancialHistoryRoutes(StudentChildFinancialHistoryHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-child-financial-histories/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexStudentChildFinancialHistoryHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-child-financial-histories/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowStudentChildFinancialHistoryHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/student-child-financial-histories/currency/show/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::getCurrencyUUID).filter(new ShowStudentChildFinancialHistoryHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-child-financial-histories/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentChildFinancialHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/student-child-financial-histories/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateStudentChildFinancialHistoryHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/student-child-financial-histories/status/update/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::status).filter(new ShowStudentChildFinancialHistoryHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-child-financial-histories/delete/{uuid}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowStudentChildFinancialHistoryHandlerFilter()));
    }
}
