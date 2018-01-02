

CREATE TABLE whitelisted_users (user_id int, username varchar(255));
CREATE TABLE events ( event_id bigserial NOT NULL, event_date date NOT NULL, event_time time NOT NULL, PRIMARY KEY (event_id));
