package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseOfferedSubjectRepository;

public class SlaveCustomCourseOfferedSubjectRepositoryImpl implements SlaveCustomCourseOfferedSubjectRepository {
    private DatabaseClient client;
    private SlaveSubjectEntity slaveSubjectEntity;

    @Autowired
    public SlaveCustomCourseOfferedSubjectRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }
    
//    @Override
//    public Flux<SlaveSubjectEntity> existingSubjectsList(UUID courseUUID, String name, String description, String slug, String subjectCode, String dp, String d, Integer size, Long page) {
//        String query = "SELECT subjects.* FROM subjects\n" +
//                "WHERE subjects.uuid NOT IN(\n" +
//                "SELECT subjects.uuid FROM subjects\n" +
//                "LEFT JOIN subject_offered\n" +
//                "ON subject_offered.subject_uuid = subjects.uuid\n" +
//                "WHERE subject_offered.course_offered_uuid = '" + courseUUID +
//                "' AND subject_offered.deleted_at IS NULL\n" +
//                "AND subjects.deleted_at IS NULL)\n" +
//                "AND (subjects.name ILIKE '%" + name + "%'" +
//                "OR subjects.description ILIKE '%" + description + "%'" +
//                "OR subjects.slug ILIKE '%" + slug + "%'" +
//                "OR subjects.subject_code ILIKE '%" + subjectCode + "%')" +
//                "AND subjects.deleted_at IS NULL " +
//                "ORDER BY subjects." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();
//
//        Flux<SlaveSubjectEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubjectEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveSubjectEntity> existingSubjectsListWithStatus(UUID courseUUID, Boolean status, String name, String description, String slug, String subjectCode, String dp, String d, Integer size, Long page) {
//        String query = "SELECT subjects.* FROM subjects\n" +
//                "WHERE subjects.uuid NOT IN(\n" +
//                "SELECT subjects.uuid FROM subjects\n" +
//                "LEFT JOIN subject_offered\n" +
//                "ON subject_offered.subject_uuid = subjects.uuid\n" +
//                "WHERE subject_offered.course_offered_uuid = '" + courseUUID +
//                "' AND subject_offered.deleted_at IS NULL\n" +
//                "AND subjects.deleted_at IS NULL)\n" +
//                "AND (subjects.name ILIKE '%" + name + "%'" +
//                "OR subjects.description ILIKE '%" + description + "%'" +
//                "OR subjects.slug ILIKE '%" + slug + "%'" +
//                "OR subjects.subject_code ILIKE '%" + subjectCode + "%')" +
//                "AND subjects.deleted_at IS NULL " +
//                "AND subjects.status = " + status +
//                " ORDER BY subjects." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubjectMapper mapper = new SlaveCustomSubjectMapper();
//
//        Flux<SlaveSubjectEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubjectEntity))
//                .all();
//
//        return result;
//    }

}
