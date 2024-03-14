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
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.http.handler.StudentGuardianHobbyPvtHandler;
import tuf.webscaf.app.http.validationFilters.studentGuardianHobbyPvtHandler.DeleteStudentGuardianHobbyPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGuardianHobbyPvtHandler.ShowStudentGuardianHobbyPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGuardianHobbyPvtHandler.StoreStudentGuardianHobbyPvtHandlerFilter;
import tuf.webscaf.springDocImpl.HobbyDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentGuardianHobbiesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-guardian-hobbies/existing/show/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGuardianHobbyPvtHandler.class,
                            beanMethod = "showHobbiesAgainstStudentGuardian",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "showHobbiesAgainstStudentGuardian",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Hobbies Against That Are Not Mapped With Given Student Guardian",
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
                            path = "/academic/api/v1/student-guardian-hobbies/mapped/show/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGuardianHobbyPvtHandler.class,
                            beanMethod = "showMappedHobbiesAgainstStudentGuardian",
                            operation = @Operation(
                                    operationId = "showMappedHobbiesAgainstStudentGuardian",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Hobbies That Are Mapped With Given Student Guardian",
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
                            path = "/academic/api/v1/student-guardian-hobbies/store/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentGuardianHobbyPvtHandler.class,
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
                                                            implementation = HobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Hobbies for a Student Guardian",
                                            required = true,
                                            content = @Content(
//                                                    mediaType = "multipart/form-data",
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = HobbyDocImpl.class)
                                            )),
                                    description = "Store Hobbies Against a Given Student Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-guardian-hobbies/delete/{studentGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentGuardianHobbyPvtHandler.class,
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
                                                            implementation = HobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Hobbies Against a Given Student Guardian",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGuardianUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "hobbyUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentGuardianHobbyPvtRoutes(StudentGuardianHobbyPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-guardian-hobbies/existing/show/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showHobbiesAgainstStudentGuardian).filter(new ShowStudentGuardianHobbyPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-guardian-hobbies/mapped/show/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedHobbiesAgainstStudentGuardian).filter(new ShowStudentGuardianHobbyPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-guardian-hobbies/store/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentGuardianHobbyPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-guardian-hobbies/delete/{studentGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentGuardianHobbyPvtHandlerFilter()));
    }

}
