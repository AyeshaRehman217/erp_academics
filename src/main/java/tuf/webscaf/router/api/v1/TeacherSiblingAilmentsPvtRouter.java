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
import tuf.webscaf.app.http.handler.TeacherSiblingAilmentPvtHandler;
import tuf.webscaf.app.http.validationFilters.teacherSiblingAilmentPvtHandler.DeleteTeacherSiblingAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingAilmentPvtHandler.ShowTeacherSiblingAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingAilmentPvtHandler.StoreTeacherSiblingAilmentPvtHandlerFilter;
import tuf.webscaf.springDocImpl.AilmentDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class TeacherSiblingAilmentsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-sibling-ailments/existing/show/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSiblingAilmentPvtHandler.class,
                            beanMethod = "showAilmentsAgainstTeacherSibling",
                            operation = @Operation(
                                    operationId = "showAilmentsAgainstTeacherSibling",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveAilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Ailments Against That Are Not Mapped With Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
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
                            path = "/academic/api/v1/teacher-sibling-ailments/mapped/show/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSiblingAilmentPvtHandler.class,
                            beanMethod = "showMappedAilmentsAgainstTeacherSibling",
                            operation = @Operation(
                                    operationId = "showMappedAilmentsAgainstTeacherSibling",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveAilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Ailments Against That Are Mapped With Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
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
                            path = "/academic/api/v1/teacher-sibling-ailments/store/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherSiblingAilmentPvtHandler.class,
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
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Ailments for a Teacher Sibling",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = AilmentDocImpl.class)
                                            )),
                                    description = "Store Ailments Against a Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/teacher-sibling-ailments/delete/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = TeacherSiblingAilmentPvtHandler.class,
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
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Ailments Against a Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "ailmentUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> teacherSiblingAilmentsPvtRoutes(TeacherSiblingAilmentPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/teacher-sibling-ailments/existing/show/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showAilmentsAgainstTeacherSibling).filter(new ShowTeacherSiblingAilmentPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-sibling-ailments/mapped/show/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedAilmentsAgainstTeacherSibling).filter(new ShowTeacherSiblingAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/teacher-sibling-ailments/store/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherSiblingAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/teacher-sibling-ailments/delete/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteTeacherSiblingAilmentPvtHandlerFilter()));
    }

}
