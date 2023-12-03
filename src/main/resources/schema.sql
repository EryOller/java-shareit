DROP TABLE IF EXISTS comments, bookings, items, requests, users;

CREATE TABLE IF NOT EXISTS users
( id int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name varchar(255) NOT NULL,
email varchar(512) NOT NULL,
CONSTRAINT pk_user PRIMARY KEY (id),
CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

--CREATE TABLE IF NOT EXISTS requests
--( id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
--description varchar(255),
--requestor_id int,
--CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id) );

CREATE TABLE IF NOT EXISTS items
( id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(255),
description varchar(255),
is_available boolean,
owner_id int,
--request_id int,
CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id) );
--CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id) );

CREATE TABLE IF NOT EXISTS bookings
( id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
start_date TIMESTAMP WITHOUT TIME ZONE,
end_date TIMESTAMP WITHOUT TIME ZONE,
item_id int,
booker_id int,
status varchar(255),
CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id) );

CREATE TABLE IF NOT EXISTS comments
( id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
text varchar(255),
item_id int,
author_id int,
CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id) );