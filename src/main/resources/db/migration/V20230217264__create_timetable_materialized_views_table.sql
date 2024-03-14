CREATE MATERIALIZED VIEW timetableView AS
select *
from (
         select timetable_creations.*,students.uuid as studentUUID,
                5 as priority
         from timetable_creations
                  join enrollments on timetable_creations.enrollment_uuid=enrollments.uuid
                  join students on enrollments.student_uuid=students.uuid
         where enrollments.deleted_at is null
           and students.deleted_at is null
           and timetable_creations.deleted_at is null
           and enrollments.uuid is not null
           and timetable_creations.is_rescheduled = false

         UNION

         select timetable_creations.*,students.uuid as studentUUID,
                1 as priority
         from timetable_creations
                  join sections on timetable_creations.section_uuid=sections.uuid
                  join section_student_pvt on sections.uuid=section_student_pvt.section_uuid
                  join students on section_student_pvt.student_uuid=students.uuid
         where students.deleted_at is null
           and section_student_pvt.deleted_at is null
           and sections.deleted_at is null
           and timetable_creations.deleted_at is null
           and timetable_creations.section_uuid is not null
           and timetable_creations.is_rescheduled = false

         UNION

         select timetable_creations.*,students.uuid as studentUUID,3 as priority
         from timetable_creations
                  join student_groups on timetable_creations.student_group_uuid=student_groups.uuid
                  join student_group_students_pvt on student_group_students_pvt.student_group_uuid=student_groups.uuid
                  join students on student_group_students_pvt.student_uuid=students.uuid
         where student_group_students_pvt.deleted_at is null
           and student_groups.deleted_at is null
           and students.deleted_at is null
           and timetable_creations.deleted_at is null
           and timetable_creations.student_group_uuid is not null
           and timetable_creations.is_rescheduled = false

         UNION

         select timetable_creations.*,students.uuid as studentUUID,
                6 as priority
         from timetable_creations
                  join enrollments on timetable_creations.enrollment_uuid=enrollments.uuid
                  join students on enrollments.student_uuid=students.uuid
         where enrollments.deleted_at is null
           and students.deleted_at is null
           and timetable_creations.deleted_at is null
           and enrollments.uuid is not null
           and timetable_creations.is_rescheduled = true


         UNION

         select timetable_creations.*,students.uuid as studentUUID,
                2 as priority
         from timetable_creations
                  join sections on timetable_creations.section_uuid=sections.uuid
                  join section_student_pvt on sections.uuid=section_student_pvt.section_uuid
                  join students on section_student_pvt.student_uuid=students.uuid
         where students.deleted_at is null
           and section_student_pvt.deleted_at is null
           and sections.deleted_at is null
           and timetable_creations.deleted_at is null
           and timetable_creations.section_uuid is not null
           and timetable_creations.is_rescheduled = true

         UNION

         select timetable_creations.*,students.uuid as studentUUID,4 as priority
         from timetable_creations
                  join student_groups on timetable_creations.student_group_uuid=student_groups.uuid
                  join student_group_students_pvt on student_group_students_pvt.student_group_uuid=student_groups.uuid
                  join students on student_group_students_pvt.student_uuid=students.uuid
         where student_group_students_pvt.deleted_at is null
           and student_groups.deleted_at is null
           and students.deleted_at is null
           and timetable_creations.deleted_at is null
           and timetable_creations.student_group_uuid is not null
           and timetable_creations.is_rescheduled = true

     ) as timetable_creations
order by priority

WITH DATA;