alter table jarvis_tasks add column search_id varchar(512) default NULL, add column result mediumtext default NULL, add index idx_search_id(search_id, type);