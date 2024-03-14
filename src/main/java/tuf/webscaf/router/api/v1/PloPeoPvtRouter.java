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
import tuf.webscaf.app.dbContext.master.entity.PeoEntity;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloPeoPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlavePloPeoPvtEntity;
import tuf.webscaf.app.http.handler.PloPeoPvtHandler;
import tuf.webscaf.app.http.validationFilters.ploPeoPvtHandler.DeletePloPeoPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ploPeoPvtHandler.IndexPloPeoPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ploPeoPvtHandler.ShowPloPeoPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ploPeoPvtHandler.StorePloPeoPvtHandlerFilter;
import tuf.webscaf.springDocImpl.PeoDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class PloPeoPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/plo-peos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PloPeoPvtHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlavePloPeoPvtDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show records with Pagination",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with name"),
                                            @Parameter(in = ParameterIn.QUERY, name = "departmentUUID"),
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/plo-peos/un-mapped/show/{ploUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PloPeoPvtHandler.class,
                            beanMethod = "showUnMappedPeosAgainstPlo",
                            operation = @Operation(
                                    operationId = "showUnMappedPeosAgainstPlo",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlavePeoEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show PEO That Are Un-Mapped With Given PLO's",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with code or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status"),
                                            @Parameter(in = ParameterIn.PATH, name = "ploUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "departmentUUID"),
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/plo-peos/mapped/show/{ploUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = PloPeoPvtHandler.class,
                            beanMethod = "showMappedPeoAgainstPlo",
                            operation = @Operation(
                                    operationId = "showMappedPeoAgainstPlo",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlavePeoEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show PEO That Are Mapped With Given PLO's",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with code or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status"),
                                            @Parameter(in = ParameterIn.PATH, name = "ploUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "departmentUUID"),
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/plo-peos/store/{ploUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = PloPeoPvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = PeoEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create PEOs for a PLO",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = PeoDocImpl.class)
                                            )),
                                    description = "Store PEOs Against a Given PLO",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "ploUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/plo-peos/delete/{ploUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = PloPeoPvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = PeoEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete PEOs Against a Given PLO",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "ploUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "peoUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> ploPeoPvtRoutes(PloPeoPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/plo-peos/un-mapped/show/{ploUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showUnMappedPeosAgainstPlo).filter(new ShowPloPeoPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/plo-peos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexPloPeoPvtHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/plo-peos/mapped/show/{ploUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedPeoAgainstPlo).filter(new ShowPloPeoPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/plo-peos/store/{ploUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StorePloPeoPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/plo-peos/delete/{ploUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeletePloPeoPvtHandlerFilter()));
    }

}
