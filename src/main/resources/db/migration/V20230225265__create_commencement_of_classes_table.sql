CREATE TABLE public.commencement_of_classes
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    version bigint,
    uuid uuid DEFAULT gen_random_uuid(),
    status boolean DEFAULT FALSE,
    is_rescheduled boolean DEFAULT FALSE,
    priority integer,
    description character varying,
    rescheduled_date timestamp without time zone,
    start_time time without time zone,
    end_time time without time zone,
    subject_uuid uuid,
    student_uuid uuid,
    enrollment_uuid uuid,
    section_uuid uuid,
    student_group_uuid uuid,
    teacher_uuid uuid,
    classroom_uuid uuid,
    academic_session_uuid uuid,
    lecture_type_uuid uuid,
    lecture_delivery_mode_uuid uuid,
    day uuid,
    created_by uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_by uuid,
    updated_at timestamp without time zone,
    deleted_by uuid,
    deleted_at timestamp without time zone,
    req_company_uuid uuid,
    req_branch_uuid uuid,
    req_created_browser character varying,
    req_created_ip character varying,
    req_created_port character varying,
    req_created_os character varying,
    req_created_device character varying,
    req_created_referer character varying,
    req_updated_browser character varying,
    req_updated_ip character varying,
    req_updated_port character varying,
    req_updated_os character varying,
    req_updated_device character varying,
    req_updated_referer character varying,
    req_deleted_browser character varying,
    req_deleted_ip character varying,
    req_deleted_port character varying,
    req_deleted_os character varying,
    req_deleted_device character varying,
    req_deleted_referer character varying,
    editable boolean DEFAULT true,
    deletable boolean DEFAULT false,
    archived boolean DEFAULT false,
    PRIMARY KEY (id)
);