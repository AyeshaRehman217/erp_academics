package tuf.webscaf.app.http.validationFilters.studentFatherAcademicHistoryHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class StoreStudentFatherAcademicHistoryHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (value.containsKey("studentFatherUUID")) {
                        if (value.getFirst("studentFatherUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Student Father Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("studentFatherUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Student Father"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Student Father Field Required"
                                )
                        );
                    }

                    if (value.containsKey("degreeUUID")) {
                        if (value.getFirst("degreeUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Degree UUID Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("degreeUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Degree"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Degree Field Required"
                                )
                        );
                    }

                    if (value.containsKey("totalMarks")) {
//                        if (value.getFirst("totalMarks").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Total Marks Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("totalMarks").isEmpty()) {
                            if (!value.getFirst("totalMarks").matches("^[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Total Marks"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Total Marks Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("obtainedMarks")) {
//                        if (value.getFirst("obtainedMarks").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Obtained Marks Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("obtainedMarks").isEmpty()) {
                            if (!value.getFirst("obtainedMarks").matches("^[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Obtained Marks"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Obtained Marks Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("totalCgpa")) {
                        if (!value.getFirst("totalCgpa").isEmpty()) {
                            String totalCgpa = value.getFirst("totalCgpa");

                            if (totalCgpa.matches("^[0-9]+$")) {
                                // get the total cgpa after parsing in double
                                totalCgpa = String.valueOf(Double.parseDouble(value.getFirst("totalCgpa")));
                            }

                            if (!totalCgpa.matches("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Total CGPA"
                                        )
                                );
                            }
                        }
                    }

                    if (value.containsKey("obtainedCgpa")) {
                        if (!value.getFirst("obtainedCgpa").isEmpty()) {
                            String obtainedCgpa = value.getFirst("obtainedCgpa");

                            if (obtainedCgpa.matches("^[0-9]+$")) {
                                // get the obtained cgpa after parsing in double
                                obtainedCgpa = String.valueOf(Double.parseDouble(value.getFirst("obtainedCgpa")));
                            }

                            if (!obtainedCgpa.matches("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Obtained CGPA"
                                        )
                                );
                            }
                        }
                    }

                    if (value.containsKey("percentage")) {
//                        if (value.getFirst("percentage").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Percentage Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("percentage").isEmpty()) {
                            if (!value.getFirst("percentage").matches("^[+]?([0-9]*[.])?[0-9]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Percentage"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Percentage Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("grade")) {
//                        if (value.getFirst("grade").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Grade Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("grade").isEmpty()) {

                            // get encoded value of grade from request
                            String encodedGrade = value.getFirst("grade");

                            String decodedGrade = "";

                            // decode the value of grade
                            try {
                                decodedGrade = URLDecoder.decode(encodedGrade, "UTF-8");
                            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                                e.printStackTrace();
                            }

                            if (!decodedGrade.matches("^[0-9A-Za-z+-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Grade"
                                        )
                                );
                            }
                        }

                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Grade Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("startDate")) {
//                        if (value.getFirst("startDate").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "Start Date Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("startDate").isEmpty()) {
                            if (!value.getFirst("startDate").matches("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}\\s(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Start Date must be in dd-MM-yyyy HH:mm:ss format"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "Start Date Field Required"
//                                )
//                        );
//                    }


                    if (value.containsKey("endDate")) {
//                        if (value.getFirst("endDate").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "End Date Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("endDate").isEmpty()) {
                            if (!value.getFirst("endDate").matches("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}\\s(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "End Date must be in dd-MM-yyyy HH:mm:ss format"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "End Date Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("passOutYear")) {
//                        if (value.getFirst("passOutYear").isEmpty()) {
//                            messages.add(
//                                    new AppResponseMessage(
//                                            AppResponse.Response.ERROR,
//                                            "PassOut Year Field Required"
//                                    )
//                            );
//                        } else {
                        if (!value.getFirst("passOutYear").isEmpty()) {
                            if (!value.getFirst("passOutYear").matches("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}\\s(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "PassOut Year must be in dd-MM-yyyy HH:mm:ss format"
                                        )
                                );
                            }
                        }
                    }
//                    } else {
//                        messages.add(
//                                new AppResponseMessage(
//                                        AppResponse.Response.ERROR,
//                                        "PassOut Year Field Required"
//                                )
//                        );
//                    }

                    if (value.containsKey("countryUUID")) {
                        if (value.getFirst("countryUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Country Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("countryUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Country"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Country Field Required"
                                )
                        );
                    }

                    if (value.containsKey("stateUUID")) {
                        if (value.getFirst("stateUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "State Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("stateUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid State"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "State Field Required"
                                )
                        );
                    }

                    if (value.containsKey("cityUUID")) {
                        if (value.getFirst("cityUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "City Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("cityUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid City"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "City Field Required"
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
