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
import tuf.webscaf.app.http.handler.AcademicCalendarDetailHolidayPvtHandler;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailHolidayPvtHandler.DeleteAcademicCalendarDetailHolidayPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailHolidayPvtHandler.ShowAcademicCalendarDetailHolidayPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.academicCalendarDetailHolidayPvtHandler.StoreAcademicCalendarDetailHolidayPvtHandlerFilter;
import tuf.webscaf.springDocImpl.HolidayDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class AcademicCalendarDetailHolidaysPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/academic-calendar-detail-holidays/un-mapped/show/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = AcademicCalendarDetailHolidayPvtHandler.class,
                            beanMethod = "showUnMappedHolidaysAgainstAcademicCalendarDetail",
                            operation = @Operation(
                                    operationId = "showUnMappedHolidaysAgainstAcademicCalendarDetail",
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
                                    description = "Show Holidays That Are Not Mapped With Given Academic Calendar Detail",
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
                            path = "/academic/api/v1/academic-calendar-detail-holidays/mapped/show/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = AcademicCalendarDetailHolidayPvtHandler.class,
                            beanMethod = "showMappedHolidaysAgainstAcademicCalendarDetail",
                            operation = @Operation(
                                    operationId = "showMappedHolidaysAgainstAcademicCalendarDetail",
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
                                    description = "Show Holidays That Are Mapped With Given Academic Calendar Detail",
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
                            path = "/academic/api/v1/academic-calendar-detail-holidays/store/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = AcademicCalendarDetailHolidayPvtHandler.class,
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
                                            description = "Create Holidays for a Academic Calendar Detail",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = HolidayDocImpl.class)
                                            )),
                                    description = "Store Holidays Against a Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/academic-calendar-detail-holidays/delete/{academicCalendarDetailUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = AcademicCalendarDetailHolidayPvtHandler.class,
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
                                    description = "Delete Holidays Against a Given Academic Calendar Detail",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "academicCalendarDetailUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "holidayUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> academicCalendarDetailHolidaysPvtRoutes(AcademicCalendarDetailHolidayPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/academic-calendar-detail-holidays/un-mapped/show/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showUnMappedHolidaysAgainstAcademicCalendarDetail).filter(new ShowAcademicCalendarDetailHolidayPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/academic-calendar-detail-holidays/mapped/show/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedHolidaysAgainstAcademicCalendarDetail).filter(new ShowAcademicCalendarDetailHolidayPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/academic-calendar-detail-holidays/store/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreAcademicCalendarDetailHolidayPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/academic-calendar-detail-holidays/delete/{academicCalendarDetailUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteAcademicCalendarDetailHolidayPvtHandlerFilter()));
    }

}
