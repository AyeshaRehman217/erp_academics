package tuf.webscaf.app.http.validationFilters.teacherMotherProfileHandler;

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

public class StoreTeacherMotherProfileHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
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
                                        "Name Required"
                                )
                        );
                    }

                    if (value.containsKey("age")) {
                        if (value.getFirst("age").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Age Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("age").matches("^[0-9]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Age"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Age Required"
                                )
                        );
                    }

                    if (value.containsKey("noOfDependents")) {
                        if (value.getFirst("noOfDependents").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "No of Dependants Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("noOfDependents").matches("^[0-9]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid No of Dependants"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "No of Dependants Required"
                                )
                        );
                    }


                    if (value.containsKey("nic")) {
                        if (value.getFirst("nic").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "NIC Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("nic").matches("^[0-9]{5}-[0-9]{7}-[0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid NIC"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "NIC Required"
                                )
                        );
                    }

                    if (value.containsKey("officialTel")) {
                        if (!value.getFirst("officialTel").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Official Telephone"
                                    )
                            );
                        }
                    }


                    if (value.containsKey("teacherMotherUUID")) {
                        if (value.getFirst("teacherMotherUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Teacher Mother Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("teacherMotherUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Teacher Mother"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Teacher Mother Field Required"
                                )
                        );
                    }

                    if (value.containsKey("image")) {
                        if (!value.getFirst("image").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Image"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("email")) {
                        if (!value.getFirst("email").matches("^(.+)@(.+)$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Email!"
                                    )
                            );
                        }
                    }

//                    if (value.containsKey("contactNoUUID")) {
//                        if (!value.getFirst("contactNoUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Invalid Contact"
//                                    )
//                            );
//                        }
//                    }

                    if (value.containsKey("cityUUID")) {
                        if (!value.getFirst("cityUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid City"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("stateUUID")) {
                        if (!value.getFirst("stateUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid State"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("countryUUID")) {
                        if (!value.getFirst("countryUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Country"
                                    )
                            );
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
