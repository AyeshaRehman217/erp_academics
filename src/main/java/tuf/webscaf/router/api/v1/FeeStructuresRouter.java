//package tuf.webscaf.router.api.v1;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.enums.ParameterIn;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Encoding;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springdoc.core.annotations.RouterOperation;
//import org.springdoc.core.annotations.RouterOperations;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import tuf.webscaf.app.dbContext.master.entity.FeeStructureEntity;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveFeeStructureEntity;
//import tuf.webscaf.app.http.handler.FeeStructureHandler;
//import tuf.webscaf.app.http.validationFilters.feeStructureHandler.IndexFeeStructureHandlerFilter;
//import tuf.webscaf.app.http.validationFilters.feeStructureHandler.StoreFeeStructureHandlerFilter;
//import tuf.webscaf.app.http.validationFilters.feeStructureHandler.UpdateFeeStructureHandlerFilter;
//import tuf.webscaf.springDocImpl.StatusDocImpl;
//
//import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
//import static org.springframework.web.reactive.function.server.RequestPredicates.*;
//import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
//
//@Configuration
//public class FeeStructuresRouter {
//    @Bean
//    @RouterOperations(
//            {
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/index",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "index",
//                            operation = @Operation(
//                                    operationId = "index",
//                                    security = { @SecurityRequirement(name = "bearer") },
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404",description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Get the Records With Pagination",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.QUERY,name = "s"),
//                                            @Parameter(in = ParameterIn.QUERY,name = "p"),
//                                            @Parameter(in = ParameterIn.QUERY,name = "d"),
//                                            @Parameter(in = ParameterIn.QUERY,name = "dp"),
//                                            @Parameter(in = ParameterIn.QUERY,name = "skw")
//                                    }
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/show/{id}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.GET,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "show",
//                            operation = @Operation(
//                                    operationId = "show",
//                                    security = { @SecurityRequirement(name = "bearer") },
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404",description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Show the Record for given Id",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH,name = "id")
//                                    }
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/store",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.POST,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "store",
//                            operation = @Operation(
//                                    operationId = "store",
//                                    security = { @SecurityRequirement(name = "bearer") },
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404",description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.HEADER,name = "auid")
//                                    },
//                                    description = "Store the Record",
//                                    requestBody = @RequestBody(
//                                            description = "Create Course Type",
//                                            required = true,
//                                            content = @Content(
//                                                    mediaType = "application/x-www-form-urlencoded",
//                                                    encoding = {
//                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                                    },
//                                                    schema = @Schema(type = "object", implementation = FeeStructureEntity.class)
//                                            ))
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/update/{id}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.PUT,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "update",
//                            operation = @Operation(
//                                    operationId = "update",
//                                    security = { @SecurityRequirement(name = "bearer") },
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404",description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH,name = "id"),
//                                            @Parameter(in = ParameterIn.HEADER,name = "auid")
//                                    },
//                                    description = "Update the Record",
//                                    requestBody = @RequestBody(
//                                            description = "Update Course Type",
//                                            required = true,
//                                            content = @Content(
//                                                    mediaType = "application/x-www-form-urlencoded",
//                                                    encoding = {
//                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                                    },
//                                                    schema = @Schema(type = "object", implementation = FeeStructureEntity.class)
//                                            ))
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/status/update/{id}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.PUT,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "status",
//                            operation = @Operation(
//                                    operationId = "status",
//                                    security = {@SecurityRequirement(name = "bearer")},
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Update the Record",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "id"),
//                                            @Parameter(in = ParameterIn.HEADER,name = "auid")
//                                    },
//                                    requestBody = @RequestBody(
//                                            description = "Update the Status",
//                                            required = true,
//                                            content = @Content(
//                                                    mediaType = "application/x-www-form-urlencoded",
//                                                    encoding = {
//                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
//                                                    },
//                                                    schema = @Schema(type = "object", implementation = StatusDocImpl.class)
//                                            ))
//                            )
//                    ),
//                    @RouterOperation(
//                            path = "/academic/api/v1/fee-structures/delete/{id}",
//                            produces = {
//                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
//                            },
//                            method = RequestMethod.DELETE,
//                            beanClass = FeeStructureHandler.class,
//                            beanMethod = "delete",
//                            operation = @Operation(
//                                    operationId = "delete",
//                                    security = { @SecurityRequirement(name = "bearer") },
//                                    responses = {
//                                            @ApiResponse(
//                                                    responseCode = "200",
//                                                    description = "successful operation",
//                                                    content = @Content(schema = @Schema(
//                                                            implementation = SlaveFeeStructureEntity.class
//                                                    ))
//                                            ),
//                                            @ApiResponse(responseCode = "404",description = "Record does not exist",
//                                                    content = @Content(schema = @Schema(hidden = true))
//                                            )
//                                    },
//                                    description = "Delete the Record for given Id",
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH,name = "id"),
//                                            @Parameter(in = ParameterIn.HEADER,name = "auid")
//                                    }
//                            )
//                    )
//            }
//    )
//    public RouterFunction<ServerResponse> feeStructureRoutes(FeeStructureHandler handle) {
//        return RouterFunctions.route(GET("academic/api/v1/fee-structures/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexFeeStructureHandlerFilter())
//                .and(RouterFunctions.route(GET("academic/api/v1/fee-structures/show/{id}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show))
//                .and(RouterFunctions.route(POST("academic/api/v1/fee-structures/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreFeeStructureHandlerFilter()))
//                .and(RouterFunctions.route(PUT("academic/api/v1/fee-structures/update/{id}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateFeeStructureHandlerFilter()))
//                .and(RouterFunctions.route(PUT("academic/api/v1/fee-structures/status/update/{id}").and(accept(APPLICATION_FORM_URLENCODED)), handle::status))
//                .and(RouterFunctions.route(DELETE("academic/api/v1/fee-structures/delete/{id}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete));
//    }
//}
