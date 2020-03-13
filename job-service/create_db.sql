create table jobs
(
    id                uuid        not null
        constraint jobs_pk
            primary key,
    job_type          varchar(64),
    status            varchar(64) not null,
    created_at        timestamptz   not null,
    finished_at       timestamptz,
    last_used         timestamptz   not null,
    error_type        varchar(64),
    exception_message text,
    profile varchar (64)
);

create table files
(
    id             uuid         not null
        constraint files_pk
            primary key,
    file_path      varchar(255) not null,
    checksum       varchar(32)  not null,
    file_type      varchar(128) not null,
    file_size      bigint       not null,
    file_orig_name text,
    created_at     timestamptz    not null,
    source_for_job uuid
        constraint files_jobs_id_fk_source_file
            references jobs
            on update cascade on delete cascade,
    result_for_job uuid
        constraint files_jobs_id_fk_result_file
            references jobs
            on update cascade on delete cascade
);

create index jobs_last_used_index
    on jobs (last_used);

create unique index files_file_path_uindex
    on files (file_path);

create index files_source_for_job_index
    on files (source_for_job);

create unique index files_result_result_for_job_uindex
    on files (result_for_job);

create index files_created_at_index
    on files (created_at);

