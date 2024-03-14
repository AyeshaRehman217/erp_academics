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
import tuf.webscaf.app.dbContext.master.entity.AilmentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.http.handler.TeacherChildAilmentPvtHandler;
import tuf.webscaf.app.http.validationFilters.teacherChildAilmentPvtHandler.DeleteTeacherChildAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherChildAilmentPvtHandler.ShowTeacherChildAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherChildAilmentPvtHandler.StoreTeacherChildAilmentPvtHandlerFilter;
import tuf.webscaf.springDocImpl.AilmentDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class TeacherChildAilmentsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-ailments/existing/show/{teacherChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAilmentPvtHandler.class,
                            beanMethod = "showAilmentsAgainstTeacherChild",
                            operation = @Operation(
                                    operationId = "showAilmentsAgainstTeacherChild",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveAilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Ailments That Are Not Mapped With Given Teacher Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with name or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-ailments/mapped/show/{teacherChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherChildAilmentPvtHandler.class,
                            beanMethod = "showMappedAilmentsAgainstTeacherChild",
                            operation = @Operation(
                                    operationId = "showMappedAilmentsAgainstTeacherChild",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveAilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Ailments That Are Mapped With Given Teacher Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with name or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-ailments/store/{teacherChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherChildAilmentPvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Ailments for a Teacher Child",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = AilmentDocImpl.class)
                                            )),
                                    description = "Store Ailments Against a Given Teacher Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherChildUUID"),
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/teacher-child-ailments/delete/{teacherChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = TeacherChildAilmentPvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Ailments Against a Given Teacher Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "ailmentUUID"),
                                            //       @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> teacherChildAilmentsPvtRoutes(TeacherChildAilmentPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/teacher-child-ailments/existing/show/{teacherChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showAilmentsAgainstTeacherChild).filter(new ShowTeacherChildAilmentPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-child-ailments/mapped/show/{teacherChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedAilmentsAgainstTeacherChild).filter(new ShowTeacherChildAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/teacher-child-ailments/store/{teacherChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherChildAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/teacher-child-ailments/delete/{teacherChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteTeacherChildAilmentPvtHandlerFilter()));
    }

}
