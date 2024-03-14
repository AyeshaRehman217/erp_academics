package tuf.webscaf.router.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import tuf.webscaf.app.dbContext.master.entity.*;
import tuf.webscaf.seeder.handler.SeederHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;


@Configuration
public class SeederRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/address-type/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAddressType",
                            operation = @Operation(
                                    operationId = "storeAddressType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AddressTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/ailments/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAilments",
                            operation = @Operation(
                                    operationId = "storeAilments",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AilmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/castes/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCastes",
                            operation = @Operation(
                                    operationId = "storeCastes",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CasteEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/contact-types/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeContactType",
                            operation = @Operation(
                                    operationId = "storeContactType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = ContactTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/gender/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeGender",
                            operation = @Operation(
                                    operationId = "storeGender",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = GenderEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/hobby/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeHobby",
                            operation = @Operation(
                                    operationId = "storeHobby",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = HobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/marital-status/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeMaritalStatus",
                            operation = @Operation(
                                    operationId = "storeMaritalStatus",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = MaritalStatusEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/religion/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeReligion",
                            operation = @Operation(
                                    operationId = "storeReligion",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = ReligionEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/sessions/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSession",
                            operation = @Operation(
                                    operationId = "storeSession",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SessionTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-sessions/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicSession",
                            operation = @Operation(
                                    operationId = "storeAcademicSession",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AcademicSessionEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/attendance-type/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAttendanceType",
                            operation = @Operation(
                                    operationId = "storeAttendanceType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = AttendanceTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/guardian-type/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeGuardianType",
                            operation = @Operation(
                                    operationId = "storeGuardianType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = GuardianTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/campuses/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCampuses",
                            operation = @Operation(
                                    operationId = "storeCampuses",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CampusEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/classrooms/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeClassrooms",
                            operation = @Operation(
                                    operationId = "storeClassrooms",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = ClassroomEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/course-levels/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCourseLevel",
                            operation = @Operation(
                                    operationId = "storeCourseLevel",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CourseLevelEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/degrees/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeDegree",
                            operation = @Operation(
                                    operationId = "storeDegree",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = DegreeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/faculties/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeFaculty",
                            operation = @Operation(
                                    operationId = "storeFaculty",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = FacultyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/departments/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeDepartment",
                            operation = @Operation(
                                    operationId = "storeDepartment",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = DepartmentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/courses/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCourse",
                            operation = @Operation(
                                    operationId = "storeCourse",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CourseEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/holiday-type/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeHolidayType",
                            operation = @Operation(
                                    operationId = "storeHolidayType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = HolidayTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/holidays/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeHoliday",
                            operation = @Operation(
                                    operationId = "storeHoliday",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = HolidayEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/lecture-delivery-modes/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeLectureDeliveryMode",
                            operation = @Operation(
                                    operationId = "storeLectureDeliveryMode",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = LectureDeliveryModeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/lecture-types/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeLectureType",
                            operation = @Operation(
                                    operationId = "storeLectureType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = LectureTypeEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/semesters/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSemester",
                            operation = @Operation(
                                    operationId = "storeSemester",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SemesterEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/subjects/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSubject",
                            operation = @Operation(
                                    operationId = "storeSubject",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/campus-courses/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCampusCourseMapper",
                            operation = @Operation(
                                    operationId = "storeCampusCourseMapper",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CampusCourseEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/course-subjects/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeCourseSubject",
                            operation = @Operation(
                                    operationId = "storeCourseSubject",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = CourseSubjectEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/subject-offered/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSubjectOffered",
                            operation = @Operation(
                                    operationId = "storeSubjectOffered",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/students/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeStudent",
                            operation = @Operation(
                                    operationId = "storeStudent",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/students/profile/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeStudentProfile",
                            operation = @Operation(
                                    operationId = "storeStudentProfile",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/students/enrollment/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeEnrollment",
                            operation = @Operation(
                                    operationId = "storeEnrollment",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-calendars/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicCalendar",
                            operation = @Operation(
                                    operationId = "storeAcademicCalendar",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-calendars-plans/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicCalendarPlan",
                            operation = @Operation(
                                    operationId = "storeAcademicCalendarPlan",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-calendar-event-types/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicCalendarEventType",
                            operation = @Operation(
                                    operationId = "storeAcademicCalendarEventType",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-calendar-details/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicCalendarDetail",
                            operation = @Operation(
                                    operationId = "storeAcademicCalendarDetail",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/academic-calendar-events/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeAcademicCalendarEvent",
                            operation = @Operation(
                                    operationId = "storeAcademicCalendarEvent",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/department-rank-catalogues/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeDepartmentRankCatalogue",
                            operation = @Operation(
                                    operationId = "storeDepartmentRankCatalogue",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/department-ranks/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeDepartmentRank",
                            operation = @Operation(
                                    operationId = "storeDepartmentRank",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/subject-outlines/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSubjectOutline",
                            operation = @Operation(
                                    operationId = "storeSubjectOutline",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/subject-obes/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSubjectOBEs",
                            operation = @Operation(
                                    operationId = "storeSubjectOBEs",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/subject-outline-offered/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSubjectOutlineOffered",
                            operation = @Operation(
                                    operationId = "storeSubjectOutlineOffered",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/registrations/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeRegistration",
                            operation = @Operation(
                                    operationId = "storeRegistration",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/teachers/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeTeacher",
                            operation = @Operation(
                                    operationId = "storeTeacher",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/teacher-profile/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeTeacherProfile",
                            operation = @Operation(
                                    operationId = "storeTeacherProfile",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/seeder/sections/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SeederHandler.class,
                            beanMethod = "storeSection",
                            operation = @Operation(
                                    operationId = "storeSection",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SubjectOfferedEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Unsuccessful",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    }
                            )
                    )
            }
    )
    public RouterFunction<ServerResponse> seederRoutes(SeederHandler handle) {
        return RouterFunctions.route(POST("academic/api/v1/seeder/address-type/store"), handle::storeAddressType)
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/ailments/store"), handle::storeAilments))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/castes/store"), handle::storeCastes))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/contact-types/store"), handle::storeContactType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/gender/store"), handle::storeGender))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/hobby/store"), handle::storeHobby))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/marital-status/store"), handle::storeMaritalStatus))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/religion/store"), handle::storeReligion))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/sessions/store"), handle::storeSession))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-sessions/store"), handle::storeAcademicSession))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/attendance-type/store"), handle::storeAttendanceType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/guardian-type/store"), handle::storeGuardianType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/campuses/store"), handle::storeCampuses))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/classrooms/store"), handle::storeClassrooms))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/course-levels/store"), handle::storeCourseLevel))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/degrees/store"), handle::storeDegree))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/faculties/store"), handle::storeFaculty))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/departments/store"), handle::storeDepartment))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/courses/store"), handle::storeCourse))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/holiday-type/store"), handle::storeHolidayType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/holidays/store"), handle::storeHoliday))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/lecture-delivery-modes/store"), handle::storeLectureDeliveryMode))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/lecture-types/store"), handle::storeLectureType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/semesters/store"), handle::storeSemester))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/subjects/store"), handle::storeSubject))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/campus-courses/store"), handle::storeCampusCourseMapper))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/course-subjects/store"), handle::storeCourseSubject))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/subject-offered/store"), handle::storeSubjectOffered))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/students/store"), handle::storeStudent))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/students/profile/store"), handle::storeStudentProfile))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/students/enrollment/store"), handle::storeEnrollment))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-calendars/store"), handle::storeAcademicCalendar))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-calendars-plans/store"), handle::storeAcademicCalendarPlan))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-calendar-event-types/store"), handle::storeAcademicCalendarEventType))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-calendar-details/store"), handle::storeAcademicCalendarDetail))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/academic-calendar-events/store"), handle::storeAcademicCalendarEvent))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/department-rank-catalogues/store"), handle::storeDepartmentRankCatalogue))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/department-ranks/store"), handle::storeDepartmentRank))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/subject-outlines/store"), handle::storeSubjectOutline))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/subject-obes/store"), handle::storeSubjectOBEs))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/subject-outline-offered/store"), handle::storeSubjectOutlineOffered))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/registrations/store"), handle::storeRegistration))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/teachers/store"), handle::storeTeacher))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/teacher-profile/store"), handle::storeTeacherProfile))
                .and(RouterFunctions.route(POST("academic/api/v1/seeder/sections/store"), handle::storeSection));
    }
}