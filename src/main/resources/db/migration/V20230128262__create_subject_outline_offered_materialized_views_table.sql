CREATE MATERIALIZED VIEW subjectOutlineOffered AS
select subject_outline_offered.*,
       CASE WHEN subject_outline_offered.subject_outline_uuid is not null AND subject_outline_offered.subject_obe_uuid is null
                THEN (
               select  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_outlines.name)
               from subject_outline_offered
                        join subject_outlines on subject_outlines.uuid=subject_outline_offered.subject_outline_uuid
                        join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid
                        join course_subject on course_subject.uuid=subject_offered.course_subject_uuid
                        join courses on courses.uuid=course_subject.course_uuid
                        join subjects on subjects.uuid=course_subject.subject_uuid
                        join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid
               where subject_outline_offered.deleted_at is null
                 and academic_sessions.deleted_at is null
                 and course_subject.deleted_at is null
                 and courses.deleted_at is null
                 and subject_outlines.deleted_at is null
                 and subjects.deleted_at is null
           )
            WHEN subject_outline_offered.subject_obe_uuid is not null AND subject_outline_offered.subject_outline_uuid is null
                THEN (
                select  concat(academic_sessions.name,'|',courses.name,'|',subjects.name,'|',subject_obes.name)
                from subject_outline_offered
                         join subject_obes on subject_obes.uuid=subject_outline_offered.subject_obe_uuid
                         join subject_offered on subject_offered.uuid=subject_outline_offered.subject_offered_uuid
                         join course_subject on course_subject.uuid=subject_offered.course_subject_uuid
                         join courses on courses.uuid=course_subject.course_uuid
                         join subjects on subjects.uuid=course_subject.subject_uuid
                         join academic_sessions on academic_sessions.uuid=subject_offered.academic_session_uuid
                where subject_outline_offered.deleted_at is null
                  and academic_sessions.deleted_at is null
                  and course_subject.deleted_at is null
                  and courses.deleted_at is null
                  and subject_obes.deleted_at is null
                  and subjects.deleted_at is null
            )
            ELSE ''
           END as name

from subject_outline_offered

WITH DATA;