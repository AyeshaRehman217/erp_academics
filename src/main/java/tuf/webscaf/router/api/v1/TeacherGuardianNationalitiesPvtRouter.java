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
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.http.handler.TeacherGuardianNationalityPvtHandler;
import tuf.webscaf.app.http.validationFilters.teacherGuardianNationalityPvtHandler.DeleteTeacherGuardianNationalityPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherGuardianNationalityPvtHandler.ShowTeacherGuardianNationalityPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherGuardianNationalityPvtHandler.StoreTeacherGuardianNationalityPvtHandlerFilter;
import tuf.webscaf.springDocImpl.NationalityDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class TeacherGuardianNationalitiesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-guardian-nationalities/existing/show/{teacherGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherGuardianNationalityPvtHandler.class,
                            beanMethod = "showNationalitiesAgainstTeacherGuardian",
                            operation = @Operation(
                                    operationId = "showNationalitiesAgainstTeacherGuardian",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveNationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Nationalities That Are Not Mapped With Given Teacher Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID"),
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
                            path = "/academic/api/v1/teacher-guardian-nationalities/mapped/show/{teacherGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherGuardianNationalityPvtHandler.class,
                            beanMethod = "showMappedNationalitiesAgainstTeacherGuardian",
                            operation = @Operation(
                                    operationId = "showMappedNationalitiesAgainstTeacherGuardian",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveNationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Nationalities That Are Mapped With Given Teacher Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID"),
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
                            path = "/academic/api/v1/teacher-guardian-nationalities/store/{teacherGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherGuardianNationalityPvtHandler.class,
                            beanMethod = "store",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = NationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Nationalities for a Teacher Guardian",
                                            required = true,
                                            content = @Content(
//                                                    mediaType = "multipart/form-data",
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = NationalityDocImpl.class)
                                            )),
                                    description = "Store Nationalities Against a Given Teacher Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/teacher-guardian-nationalities/delete/{teacherGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = TeacherGuardianNationalityPvtHandler.class,
                            beanMethod = "delete",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = NationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Nationalities Against a Given Teacher Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "nationalityUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> teacherGuardianNationalitiesPvtRoutes(TeacherGuardianNationalityPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/teacher-guardian-nationalities/existing/show/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showNationalitiesAgainstTeacherGuardian).filter(new ShowTeacherGuardianNationalityPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/teacher-guardian-nationalities/mapped/show/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedNationalitiesAgainstTeacherGuardian).filter(new ShowTeacherGuardianNationalityPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/teacher-guardian-nationalities/store/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherGuardianNationalityPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/teacher-guardian-nationalities/delete/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteTeacherGuardianNationalityPvtHandlerFilter()));
    }

}
