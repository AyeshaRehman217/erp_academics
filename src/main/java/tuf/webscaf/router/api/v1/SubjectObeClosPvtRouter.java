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
import tuf.webscaf.app.dbContext.master.entity.CloEntity;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectObeCloPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectObeCloPvtEntity;
import tuf.webscaf.app.http.handler.SubjectObeCloPvtHandler;
import tuf.webscaf.app.http.validationFilters.subjectObeCloPvtHandler.DeleteSubjectObeCloPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.subjectObeCloPvtHandler.IndexSubjectObeCloPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.subjectObeCloPvtHandler.ShowSubjectObeCloPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.subjectObeCloPvtHandler.StoreSubjectObeCloPvtHandlerFilter;
import tuf.webscaf.springDocImpl.CloDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class SubjectObeClosPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/subject-obe-clos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = SubjectObeCloPvtHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveSubjectObeCloPvtDto.class
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
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/subject-obe-clos/un-mapped/show/{subjectObeUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = SubjectObeCloPvtHandler.class,
                            beanMethod = "showClosAgainstSubjectObe",
                            operation = @Operation(
                                    operationId = "showClosAgainstSubjectObe",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveCloEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Clos That Are Not Mapped With Given Subject Obe",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "subjectObeUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with code or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/subject-obe-clos/mapped/show/{subjectObeUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = SubjectObeCloPvtHandler.class,
                            beanMethod = "showMappedClosAgainstSubjectObe",
                            operation = @Operation(
                                    operationId = "showMappedClosAgainstSubjectObe",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveCloEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Clos That Are Mapped With Given Subject Obe",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "subjectObeUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with code or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/subject-obe-clos/store/{subjectObeUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SubjectObeCloPvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CloEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Clos for a Subject Obe",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = CloDocImpl.class)
                                            )),
                                    description = "Store Clos Against a Given Subject Obe",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "subjectObeUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/subject-obe-clos/delete/{subjectObeUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = SubjectObeCloPvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CloEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Clos Against a Given Subject Obe",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "subjectObeUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "cloUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> subjectObeCloRoutes(SubjectObeCloPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/subject-obe-clos/un-mapped/show/{subjectObeUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showClosAgainstSubjectObe).filter(new ShowSubjectObeCloPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/subject-obe-clos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index).filter(new IndexSubjectObeCloPvtHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/subject-obe-clos/mapped/show/{subjectObeUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedClosAgainstSubjectObe).filter(new ShowSubjectObeCloPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/subject-obe-clos/store/{subjectObeUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreSubjectObeCloPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/subject-obe-clos/delete/{subjectObeUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteSubjectObeCloPvtHandlerFilter()));
    }

}
