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
import tuf.webscaf.app.dbContext.master.entity.HolidayEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.http.handler.AcademicCalendarDetailEventPvtHandler;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailEventPvtHandler.DeleteAcademicCalendarDetailEventPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailEventPvtHandler.ShowAcademicCalendarDetailEventPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailEventPvtHandler.StoreAcademicCalendarDetailEventPvtHandlerFilter;
import tuf.webscaf.springDocImpl.AcademicCalendarEventDocImpl;
import tuf.webscaf.springDocImpl.HolidayDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class AcademicCalendarDetailEventsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/academic-calendar-detail-events/un-mapped/show/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = AcademicCalendarDetailEventPvtHandler.class,
                            beanMethod = "showUnMappedEventsAgainstAcademicCalendarDetail",
                            operation = @Operation(
                                    operationId = "showUnMappedEventsAgainstAcademicCalendarDetail",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHolidayEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Academic Calendar Events That Are Not Mapped With Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
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
                            path = "/academic/api/v1/academic-calendar-detail-events/mapped/show/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = AcademicCalendarDetailEventPvtHandler.class,
                            beanMethod = "showMappedEventsAgainstAcademicCalendarDetail",
                            operation = @Operation(
                                    operationId = "showMappedEventsAgainstAcademicCalendarDetail",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHolidayEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Academic Calendar Events That Are Mapped With Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
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
                            path = "/academic/api/v1/academic-calendar-detail-events/store/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = AcademicCalendarDetailEventPvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = HolidayEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Events for a Academic Calendar Detail",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = AcademicCalendarEventDocImpl.class)
                                            )),
                                    description = "Store Academic Calendar Events Against a Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/academic-calendar-detail-events/delete/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = AcademicCalendarDetailEventPvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = HolidayEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Academic Calendar Events Against a Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "academicCalendarEventUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> academicCalendarDetailEventsPvtRoutes(AcademicCalendarDetailEventPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/academic-calendar-detail-events/un-mapped/show/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showUnMappedEventsAgainstAcademicCalendarDetail).filter(new ShowAcademicCalendarDetailEventPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/academic-calendar-detail-events/mapped/show/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedEventsAgainstAcademicCalendarDetail).filter(new ShowAcademicCalendarDetailEventPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/academic-calendar-detail-events/store/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreAcademicCalendarDetailEventPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/academic-calendar-detail-events/delete/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteAcademicCalendarDetailEventPvtHandlerFilter()));
    }

}
