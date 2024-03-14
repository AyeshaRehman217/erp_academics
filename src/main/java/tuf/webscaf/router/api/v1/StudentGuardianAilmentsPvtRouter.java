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
import tuf.webscaf.app.http.handler.StudentGuardianAilmentPvtHandler;
import tuf.webscaf.app.http.validationFilters.studentGuardianAilmentPvtHandler.DeleteStudentGuardianAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGuardianAilmentPvtHandler.ShowStudentGuardianAilmentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGuardianAilmentPvtHandler.StoreStudentGuardianAilmentPvtHandlerFilter;
import tuf.webscaf.springDocImpl.AilmentDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentGuardianAilmentsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-guardian-ailments/existing/show/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGuardianAilmentPvtHandler.class,
                            beanMethod = "showAilmentsAgainstStudentGuardian",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "showAilmentsAgainstStudentGuardian",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveAilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Ailments That Are Not Mapped With Given Student Guardian UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
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
                            path = "/academic/api/v1/student-guardian-ailments/mapped/show/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGuardianAilmentPvtHandler.class,
                            beanMethod = "showMappedAilmentsAgainstStudentGuardian",
                            operation = @Operation(
                                    operationId = "showMappedAilmentsAgainstStudentGuardian",
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
                                    description = "Show Ailments That Are Mapped With Given Student Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
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
                            path = "/academic/api/v1/student-guardian-ailments/store/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentGuardianAilmentPvtHandler.class,
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
                                                            implementation = AilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Ailments for a Student Guardian",
                                            required = true,
                                            content = @Content(
//                                                    mediaType = "multipart/form-data",
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = AilmentDocImpl.class)
                                            )),
                                    description = "Store Ailments Against a Given Student Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-guardian-ailments/delete/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentGuardianAilmentPvtHandler.class,
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
                                                            implementation = AilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Ailments Against a Given Student Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "ailmentUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentGuardianAilmentsPvtRoutes(StudentGuardianAilmentPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-guardian-ailments/existing/show/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showAilmentsAgainstStudentGuardian).filter(new ShowStudentGuardianAilmentPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-guardian-ailments/mapped/show/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedAilmentsAgainstStudentGuardian).filter(new ShowStudentGuardianAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-guardian-ailments/store/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentGuardianAilmentPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-guardian-ailments/delete/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentGuardianAilmentPvtHandlerFilter()));
    }

}
