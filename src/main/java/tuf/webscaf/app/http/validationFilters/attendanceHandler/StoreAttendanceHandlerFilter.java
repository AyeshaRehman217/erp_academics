package tuf.webscaf.app.http.validationFilters.attendanceHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoreAttendanceHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();


//                    if (value.containsKey("studentUUID")) {
//                        if (value.getFirst("studentUUID").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Student Field Required"
//                                    )
//                            );
//                        } else {
//                            if (!value.getFirst("studentUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                                messages.add(
//                                        new AppResponseMessage(
//                                                AppResponse.Response.ERROR,
//                                                "Invalid Student."
//                                        )
//                                );
//                            }
//                        }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Student Field Required"
//                                )
//                        );
//                    }
//
//                    if (value.containsKey("academicSessionUUID")) {
//                        if (value.getFirst("academicSessionUUID").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Academic Session Field Required"
//                                    )
//                            );
//                        } else {
//                            if (!value.getFirst("academicSessionUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                                messages.add(
//                                        new AppResponseMessage(
//                                                AppResponse.Response.ERROR,
//                                                "Invalid Academic Session."
//                                        )
//                                );
//                            }
//                        }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Academic Session Field Required"
//                                )
//                        );
//                    }
//
//                    if (value.containsKey("subjectUUID")) {
//                        if (value.getFirst("subjectUUID").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Subject Field Required"
//                                    )
//                            );
//                        } else {
//                            if (!value.getFirst("subjectUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                                messages.add(
//                                        new AppResponseMessage(
//                                                AppResponse.Response.ERROR,
//                                                "Invalid Subject."
//                                        )
//                                );
//                            }
//                        }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Subject Field Required"
//                                )
//                        );
//                    }



                    if (value.containsKey("commencementOfClassesUUID")) {
                        if (value.getFirst("commencementOfClassesUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Commencement of Classes Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("commencementOfClassesUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Commencement of Classes"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Commencement of Classes Field Required"
                                )
                        );
                    }

                    if (value.containsKey("attendanceTypeUUID")) {
                        if (value.getFirst("attendanceTypeUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Attendance Type Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("attendanceTypeUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Attendance Type"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Attendance Type Field Required"
                                )
                        );
                    }


//                    if (value.containsKey("startTime")) {
//                        if (value.getFirst("startTime").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Start Time Field Required"
//                                    )
//                            );
//                        } else {
//                            if (!value.getFirst("startTime").matches("^(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
//                                messages.add(
//                                        new AppResponseMessage(
//                                                AppResponse.Response.ERROR,
//                                                "Start Time must be in HH:mm:ss format"
//                                        )
//                                );
//                            }
//                        }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Start Time Field Required"
//                                )
//                        );
//                    }
//
//                    if (value.containsKey("endTime")) {
//                        if (value.getFirst("endTime").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "End Time Field Required"
//                                    )
//                            );
//                        } else {
//                            if (!value.getFirst("endTime").matches("^(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
//                                messages.add(
//                                        new AppResponseMessage(
//                                                AppResponse.Response.ERROR,
//                                                "End Time must be in HH:mm:ss format"
//                                        )
//                                );
//                            }
//                        }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "End Time Field Required"
//                                )
//                        );
//                    }

                    if ((value.containsKey("markedBy") && (!Objects.equals(value.getFirst("markedBy"), "")))) {
                        if (!value.getFirst("markedBy").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Marked By UUID"
                                    )
                            );
                        }
                    }



                    if (!messages.isEmpty()) {
                        CustomResponse appresponse = new CustomResponse();
                        return appresponse.set(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.name(),
                                null,
                                "eng",
                                "token",
                                0L,
                                0L,
                                messages,
                                Mono.empty()
                        );
                    }

                    return next.handle(request);
                });
    }
}
