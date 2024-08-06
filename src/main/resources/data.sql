
insert into "user" (email, password, name, nickname, is_social) values ('admin@naver.com', '$2a$12$4EbNBPJzszu/DQ11QEgdTuBoxPjZFV0EhOPWusw7FzSYt3.WMjMvK', 'admin', 'admin', false);
insert into "user" (email, password, name, nickname, is_social) values ('geonHui@naver.com', '$2a$12$kwmjJetpHfDH9IV2843.suBCzpiL.AIyfdKCAG/syMsP.P3KFx/2m', 'geonHui', 'geonHui', false);

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values (1, 'ROLE_USER');
insert into user_authority (user_id, authority_name) values (1, 'ROLE_ADMIN');
insert into user_authority (user_id, authority_name) values (2, 'ROLE_USER');


INSERT INTO tag (category, name) VALUES ('WEATHER', 'SUNNY');
INSERT INTO tag (category, name) VALUES ('WEATHER', 'CLOUDY');
INSERT INTO tag (category, name) VALUES ('WEATHER', 'RAINY');
INSERT INTO tag (category, name) VALUES ('WEATHER', 'SNOWY');
INSERT INTO tag (category, name) VALUES ('WEATHER', 'WINDY');
INSERT INTO tag (category, name) VALUES ('TEMPERATURE', 'HOT');
INSERT INTO tag (category, name) VALUES ('TEMPERATURE', 'COLD');
INSERT INTO tag (category, name) VALUES ('TEMPERATURE', 'WARM');
INSERT INTO tag (category, name) VALUES ('TEMPERATURE', 'COOL');
INSERT INTO tag (category, name) VALUES ('TEMPERATURE', 'MILD');
INSERT INTO tag (category, name) VALUES ('SEASON', 'SPRING');
INSERT INTO tag (category, name) VALUES ('SEASON', 'SUMMER');
INSERT INTO tag (category, name) VALUES ('SEASON', 'FALL');
INSERT INTO tag (category, name) VALUES ('SEASON', 'WINTER');
