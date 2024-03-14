CREATE TABLE public.courses
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    version bigint,
    uuid uuid DEFAULT gen_random_uuid(),
    status boolean default false,
    name character varying NOT NULL,
    slug character varying,
    description character varying,
    code character varying,
    short_name character varying,
    course_level_uuid uuid,
    eligibility_criteria character varying,
    minimum_age_limit integer,
    maximum_age_limit integer,
    department_uuid uuid,
    duration character varying,
    is_semester boolean,
    no_of_semester integer,
--     is_annual boolean,
    no_of_annuals integer,
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