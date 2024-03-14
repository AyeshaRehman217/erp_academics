package tuf.webscaf.app.http.validationFilters.courseHandler;

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

public class StoreCourseHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (value.containsKey("name")) {
                        if (value.getFirst("name").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Name Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("name").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Name"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Name Field Required"
                                )
                        );
                    }

                    if (value.containsKey("code")) {
                        if (value.getFirst("code").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Code Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("code").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Code"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Code Field Required"
                                )
                        );
                    }

                    if (value.containsKey("description")) {
                        if (!value.getFirst("description").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Description"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("courseLevelUUID")) {
                        if (value.getFirst("courseLevelUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Course Level UUID Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("courseLevelUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Course Level UUID"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Course Level UUID Field Required"
                                )
                        );
                    }

                    if (value.containsKey("departmentUUID")) {
                        if (value.getFirst("departmentUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Department UUID Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("departmentUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Department UUID"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Department UUID Field Required"
                                )
                        );
                    }

                    if (value.containsKey("minimumAgeLimit")) {
                        if (value.getFirst("minimumAgeLimit").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Minimum Age Limit Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("minimumAgeLimit").matches("^[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Minimum Age Limit"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Minimum Age Limit Field Required"
                                )
                        );
                    }

                    if (value.containsKey("maximumAgeLimit")) {
                        if (value.getFirst("maximumAgeLimit").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Maximum Age Limit Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("maximumAgeLimit").matches("^[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Maximum Age Limit"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Maximum Age Limit Field Required"
                                )
                        );
                    }

                    if (value.containsKey("shortName")) {
                        if (value.getFirst("shortName").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Course Short Name Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("shortName").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Course Short Name"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Course Short Name Field Required"
                                )
                        );
                    }

                    if (value.containsKey("eligibilityCriteria")) {
                        if (value.getFirst("eligibilityCriteria").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Eligibility Criteria Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("eligibilityCriteria").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Eligibility Criteria"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Eligibility Criteria Field Required"
                                )
                        );
                    }

                    if (value.containsKey("duration")) {
                        if (!value.getFirst("duration").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Duration"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("isSemester")) {
                        if (Boolean.valueOf(value.getFirst("isSemester"))) {

                            // checks if no of semesters is present
                            if (value.containsKey("noOfSemester")) {
                                if (!value.getFirst("noOfSemester").isEmpty()) {
                                    if (!value.getFirst("noOfSemester").matches("^[0-9]+$")) {
                                        messages.add(
                                                new AppResponseMessage(
                                                        AppResponse.Response.ERROR,
                                                        "No.Of Semesters Will be Numeric"
                                                )
                                        );
                                    }
                                }
                            }

                        }
                    }

                    if (value.containsKey("isAnnual")) {
                        if (Boolean.valueOf(value.getFirst("isAnnual"))) {

                            // checks if no of annuals is present
                            if (value.containsKey("noOfAnnuals")) {
                                if (!value.getFirst("noOfAnnuals").isEmpty()) {
                                    if (!value.getFirst("noOfAnnuals").matches("^[0-9]+$")) {
                                        messages.add(
                                                new AppResponseMessage(
                                                        AppResponse.Response.ERROR,
                                                        "No.Of Annuals Will be Numeric"
                                                )
                                        );
                                    }
                                }
                            }
                        }
                    }

                    if (messages.isEmpty() != true) {
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
