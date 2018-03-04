
CREATE TABLE event_descriptions (
	event_id int NOT NULL,
	title varchar(1000) NOT NULL,
	subtitle varchar(1000),
	speaker varchar(1000) NOT NULL,
	description text NOT NULL,
	venue varchar(1000) NOT NULL,
	event_date date NOT NULL,
	event_time time NOT NULL,
	max_seats int NOT NULL,
	taken_seats int NOT NULL,
	media_link varchar(1000)
);

CREATE TABLE whitelisted_users (
	user_id int NOT NULL,
	username varchar(100)
	email varchar(40) NOT NULL,
	PRIMARY KEY (user_id)
);

CREATE TABLE newsletter_post (
    post_id int NOT NULL,
    post_date date NOT NULL,
    post_user_id int NOT NULL,
    title varchar(1000) NOT NULL,
    subtitle varchar(1000) NOT NULL,
);



