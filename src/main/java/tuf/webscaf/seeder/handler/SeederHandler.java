package tuf.webscaf.seeder.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.seeder.service.*;

@Component
public class SeederHandler {

    @Autowired
    SeederAddressTypeService seederAddressTypeService;

    @Autowired
    SeederAilmentService seederAilmentService;

    @Autowired
    SeederCasteService seederCasteService;

    @Autowired
    SeederContactTypeService seederContactTypeService;

    @Autowired
    SeederContactService seederContactService;

    @Autowired
    SeederEmailTypeService seederEmailTypeService;

    @Autowired
    SeederGenderService seederGenderService;

    @Autowired
    SeederHobbyService seederHobbyService;

    @Autowired
    SeederCoursesService seederCoursesService;

    @Autowired
    SeederMaritalStatusService seederMaritalStatusService;

    @Autowired
    SeederReligionService seederReligionService;

    @Autowired
    SeederSessionService seederSessionService;

    @Autowired
    SeederAcademicSessionService seederAcademicSessionService;

    @Autowired
    SeederAttendanceTypeService seederAttendanceTypeService;

    @Autowired
    SeederGuardianTypeService seederGuardianTypeService;

    @Autowired
    SeederCampusesService seederCampusesService;

    @Autowired
    SeederClassRoomService seederClassRoomService;

    @Autowired
    SeederCourseLevelService seederCourseLevelService;

    @Autowired
    SeederCourseTypeService seederCourseTypeService;

    @Autowired
    SeederDegreeService seederDegreeService;

    @Autowired
    SeederFacultyService seederFacultyService;

    @Autowired
    SeederDepartmentService seederDepartmentService;

    @Autowired
    SeederHolidayTypeService seederHolidayTypeService;

    @Autowired
    SeederHolidayService seederHolidayService;

    @Autowired
    SeederLectureDeliveryModeService seederLectureDeliveryModeService;

    @Autowired
    SeederLectureTypeService seederLectureTypeService;

    @Autowired
    SeederSemesterService seederSemesterService;

    @Autowired
    SeederSubjectsService seederSubjectsService;

    @Autowired
    SeederCampusCourseMapperService seederCampusCourseMapperService;

    @Autowired
    SeederCourseSubjectService seederCourseSubjectService;

    @Autowired
    SeederSubjectOfferedService seederSubjectOfferedService;

    @Autowired
    SeederStudentService seederStudentService;

    @Autowired
    SeederStudentProfileService seederStudentProfileService;

    @Autowired
    CustomResponse customResponse;

    @Autowired
    SeederEnrollmentService seederEnrollmentService;

    @Autowired
    SeederAcademicCalendarService seederAcademicCalendarService;

    @Autowired
    SeederAcademicCalendarPlanService seederAcademicCalendarPlanService;

    @Autowired
    SeederAcademicCalendarEventTypeService seederAcademicCalendarEventTypeService;

    @Autowired
    SeederAcademicCalendarDetailsService seederAcademicCalendarDetailsService;

    @Autowired
    SeederAcademicCalendarEventService seederAcademicCalendarEventService;

    @Autowired
    SeederDepartmentRankCatalogueService seederDepartmentRankCatalogueService;

    @Autowired
    SeederDepartmentRankService seederDepartmentRankService;

    @Autowired
    SeederSubjectOutlineService seederSubjectOutlineService;


    @Autowired
    SeederSubjectOBEsService seederSubjectOBEsService;

    @Autowired
    SeederSubjectOutlineOfferedService seederSubjectOutlineOfferedService;

    @Autowired
    SeederRegistrationService seederRegistrationService;

    @Autowired
    SeederTeacherService seederTeacherService;

    @Autowired
    SeederTeacherProfileService seederTeacherProfileService;

    @Autowired
    SeederSectionService seederSectionService;

    public Mono<ServerResponse> storeAddressType(ServerRequest serverRequest) {
        return seederAddressTypeService
                .seedAddressType();
    }

    public Mono<ServerResponse> storeAilments(ServerRequest serverRequest) {
        return seederAilmentService
                .seedAilments();
    }

    public Mono<ServerResponse> storeCastes(ServerRequest serverRequest) {
        return seederCasteService
                .seedCaste();
    }

    public Mono<ServerResponse> storeContactType(ServerRequest serverRequest) {
        return seederContactTypeService
                .seedContactType();
    }

    public Mono<ServerResponse> storeContactNo(ServerRequest serverRequest) {
        return seederContactService
                .seedContact();
    }

    public Mono<ServerResponse> storeEnrollment(ServerRequest serverRequest) {
        return seederEnrollmentService
                .seedStudent();
    }

    public Mono<ServerResponse> storeGender(ServerRequest serverRequest) {
        return seederGenderService
                .seedGender();
    }

    public Mono<ServerResponse> storeHobby(ServerRequest serverRequest) {
        return seederHobbyService
                .seedHobby();
    }

    public Mono<ServerResponse> storeMaritalStatus(ServerRequest serverRequest) {
        return seederMaritalStatusService
                .seedMaritalStatus();
    }

    public Mono<ServerResponse> storeReligion(ServerRequest serverRequest) {
        return seederReligionService
                .seedReligion();
    }

    public Mono<ServerResponse> storeSession(ServerRequest serverRequest) {
        return seederSessionService
                .seedSession();
    }

    public Mono<ServerResponse> storeAcademicSession(ServerRequest serverRequest) {
        return seederAcademicSessionService
                .seedAcademicSession();
    }

    public Mono<ServerResponse> storeAttendanceType(ServerRequest serverRequest) {
        return seederAttendanceTypeService
                .seedAttendanceType();
    }

    public Mono<ServerResponse> storeGuardianType(ServerRequest serverRequest) {
        return seederGuardianTypeService
                .seedGuardianType();
    }

    public Mono<ServerResponse> storeCampuses(ServerRequest serverRequest) {
        return seederCampusesService
                .seedCampuses();
    }

    public Mono<ServerResponse> storeClassrooms(ServerRequest serverRequest) {
        return seederClassRoomService
                .seedClassroom();
    }

    public Mono<ServerResponse> storeCourseLevel(ServerRequest serverRequest) {
        return seederCourseLevelService
                .seedCourseLevel();
    }

    public Mono<ServerResponse> storeCourseType(ServerRequest serverRequest) {
        return seederCourseTypeService
                .seedCourseType();
    }

    public Mono<ServerResponse> storeDegree(ServerRequest serverRequest) {
        return seederDegreeService
                .seedDegree();
    }

    public Mono<ServerResponse> storeFaculty(ServerRequest serverRequest) {
        return seederFacultyService
                .seedFaculty();
    }

    public Mono<ServerResponse> storeDepartment(ServerRequest serverRequest) {
        return seederDepartmentService
                .seedDepartment();
    }

    public Mono<ServerResponse> storeCourse(ServerRequest serverRequest) {
        return seederCoursesService
                .seedCourse();
    }

    public Mono<ServerResponse> storeHolidayType(ServerRequest serverRequest) {
        return seederHolidayTypeService
                .seedHolidayType();
    }

    public Mono<ServerResponse> storeHoliday(ServerRequest serverRequest) {
        return seederHolidayService
                .seedHoliday();
    }

    public Mono<ServerResponse> storeLectureDeliveryMode(ServerRequest serverRequest) {
        return seederLectureDeliveryModeService
                .seedLectureDeliveryMode();
    }

    public Mono<ServerResponse> storeLectureType(ServerRequest serverRequest) {
        return seederLectureTypeService
                .seedLectureType();
    }

    public Mono<ServerResponse> storeSemester(ServerRequest serverRequest) {
        return seederSemesterService
                .seedSemester();
    }

    public Mono<ServerResponse> storeSubject(ServerRequest serverRequest) {
        return seederSubjectsService
                .seedSubject();
    }

    public Mono<ServerResponse> storeCampusCourseMapper(ServerRequest serverRequest) {
        return seederCampusCourseMapperService
                .seedCampusCourseMapper();
    }

    public Mono<ServerResponse> storeCourseSubject(ServerRequest serverRequest) {
        return seederCourseSubjectService
                .seedCourseSubject();
    }

    public Mono<ServerResponse> storeSubjectOffered(ServerRequest serverRequest) {
        return seederSubjectOfferedService
                .seedSubjectOffered();
    }

    public Mono<ServerResponse> storeStudent(ServerRequest serverRequest) {
        return seederStudentService
                .seedStudent();
    }

    public Mono<ServerResponse> storeStudentProfile(ServerRequest serverRequest) {
        return seederStudentProfileService
                .seedStudentMother();
    }

    public Mono<ServerResponse> storeAcademicCalendar(ServerRequest serverRequest) {
        return seederAcademicCalendarService
                .seedAcademicCalendar();
    }


    public Mono<ServerResponse> storeAcademicCalendarPlan(ServerRequest serverRequest) {
        return seederAcademicCalendarPlanService
                .seedAcademicCalendarPlan();
    }

    public Mono<ServerResponse> storeAcademicCalendarEventType(ServerRequest serverRequest) {
        return seederAcademicCalendarEventTypeService
                .seedAcademicCalendarEventType();
    }

    public Mono<ServerResponse> storeAcademicCalendarDetail(ServerRequest serverRequest) {
        return seederAcademicCalendarDetailsService
                .seedAcademicCalendarDetail();
    }

    public Mono<ServerResponse> storeAcademicCalendarEvent(ServerRequest serverRequest) {
        return seederAcademicCalendarEventService
                .seedAcademicCalendarEvent();
    }

    public Mono<ServerResponse> storeDepartmentRankCatalogue(ServerRequest serverRequest) {
        return seederDepartmentRankCatalogueService
                .seedDepartmentRankCatalogue();
    }

    public Mono<ServerResponse> storeDepartmentRank(ServerRequest serverRequest) {
        return seederDepartmentRankService
                .seedDepartmentRank();
    }


    public Mono<ServerResponse> storeSubjectOutline(ServerRequest serverRequest) {
        return seederSubjectOutlineService
                .seedSubjectOutline();
    }

    public Mono<ServerResponse> storeSubjectOBEs(ServerRequest serverRequest) {
        return seederSubjectOBEsService
                .seedSubjectOBEs();
    }

    public Mono<ServerResponse> storeSubjectOutlineOffered(ServerRequest serverRequest) {
        return seederSubjectOutlineOfferedService
                .seedSubjectOutlineOffered();
    }

    public Mono<ServerResponse> storeRegistration(ServerRequest serverRequest) {
        return seederRegistrationService
                .seedRegistration();
    }

    public Mono<ServerResponse> storeTeacher(ServerRequest serverRequest) {
        return seederTeacherService
                .seedTeacher();
    }

    public Mono<ServerResponse> storeTeacherProfile (ServerRequest serverRequest) {
        return seederTeacherProfileService
                .seedTeacherProfile();
    }

    public Mono<ServerResponse> storeSection (ServerRequest serverRequest) {
        return seederSectionService
                .seedSection();
    }

}