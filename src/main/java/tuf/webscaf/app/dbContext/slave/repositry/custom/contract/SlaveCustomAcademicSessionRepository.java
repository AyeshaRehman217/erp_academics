package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicSessionEntity;

import java.util.UUID;

// This interface wil extends in Slave Academic Session Repository
public interface SlaveCustomAcademicSessionRepository {

    Flux<SlaveAcademicSessionEntity> showAcademicSessionOfCalendarWithoutStatus(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicSessionEntity> showAcademicSessionOfCalendarWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicSessionEntity> showAcademicSessionOfTeacherWithoutStatus(UUID teacherUUID, String name, String dp, String d, Integer size, Long page);

    Flux<SlaveAcademicSessionEntity> showAcademicSessionOfTeacherWithStatus(UUID teacherUUID, String name, Boolean status, String dp, String d, Integer size, Long page);


}
