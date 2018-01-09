

CREATE TABLE whitelisted_users (
	user_id int,
	username varchar(255)
);

CREATE TABLE events (
	event_id bigserial NOT NULL,
	event_date date NOT NULL,
	event_time time NOT NULL,
	PRIMARY KEY (event_id)
);

CREATE TABLE event_descriptions (
	event_id int NOT NULL,
	title varchar(255) NOT NULL,
	subtitle varchar(255),
	speaker varchar(255) NOT NULL,
	description varchar(255) NOT NULL,
	venue varchar(255) NOT NULL,
	event_date date NOT NULL,
	event_time time NOT NULL,
	max_seats int NOT NULL,
	taken_seats int NOT NULL,
	media_link varchar(255)
);

