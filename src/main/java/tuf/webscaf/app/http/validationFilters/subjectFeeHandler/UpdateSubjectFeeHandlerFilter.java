package tuf.webscaf.app.http.validationFilters.subjectFeeHandler;

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

public class UpdateSubjectFeeHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (!request.pathVariable("uuid").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Invalid UUID"
                                )
                        );
                    }

                    if (value.containsKey("subjectOfferedUUID")) {
                        if (value.getFirst("subjectOfferedUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Subject Offered UUID Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("subjectOfferedUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Subject Offered UUID"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Subject Offered UUID Field Required"
                                )
                        );
                    }


                    if (value.containsKey("creditHoursRate")) {
                        String creditHoursRate = value.getFirst("creditHoursRate");
                        if (!creditHoursRate.isEmpty()) {

                            if (creditHoursRate.matches("^[0-9]+$")) {
                                creditHoursRate = String.valueOf(Double.parseDouble(value.getFirst("creditHoursRate")));
                            }

                            if (!creditHoursRate.matches("^[0-9]+.[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Credit Hours Rate"
                                        )
                                );
                            }
                        }
                    }

                    if (value.containsKey("amount")) {
                        String amount = value.getFirst("amount");
                        if (!amount.isEmpty()) {

                            if (amount.matches("^[0-9]+$")) {
                                amount = String.valueOf(Double.parseDouble(value.getFirst("amount")));
                            }

                            if (!amount.matches("^[0-9]+.[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Amount"
                                        )
                                );
                            }
                        }
                    }

                    if (value.containsKey("subjectEnrollmentFee")) {
                        if (value.getFirst("subjectEnrollmentFee").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Subject Enrollment Fee Field Required"
                                    )
                            );
                        } else {
                            String subjectEnrollmentFee = value.getFirst("subjectEnrollmentFee");

                            if (!subjectEnrollmentFee.isEmpty()) {

                                if (subjectEnrollmentFee.matches("^[0-9]+$")) {
                                    subjectEnrollmentFee = String.valueOf(Double.parseDouble(value.getFirst("subjectEnrollmentFee")));
                                }

                                if (!subjectEnrollmentFee.matches("^[0-9]+.[0-9]+$")) {
                                    messages.add(
                                            new AppResponseMessage(
                                                    AppResponse.Response.ERROR,
                                                    "Invalid Subject Enrollment Fees"
                                            )
                                    );
                                }
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Subject Enrollment Fee Required"
                                )
                        );
                    }

                    if (value.containsKey("currencyUUID")) {
                        if (value.getFirst("currencyUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Currency UUID Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("currencyUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Currency UUID"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Currency UUID Field Required"
                                )
                        );
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
