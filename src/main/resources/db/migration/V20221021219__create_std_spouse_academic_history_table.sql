CREATE TABLE public.std_spouse_academic_history
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    version bigint,
    uuid uuid DEFAULT gen_random_uuid(),
    status boolean DEFAULT false,
    std_spouse_uuid uuid,
    degree_uuid uuid,
    is_cgpa boolean DEFAULT false,
    total_marks integer,
    obtained_marks integer,
    total_cgpa real,
    obtained_cgpa real,
    percentage real,
    grade character varying,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    passout_year timestamp without time zone,
    country_uuid uuid,
    state_uuid uuid,
    city_uuid uuid,
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