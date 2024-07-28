
insert into "user" (email, password, name, nickname, is_social) values ('admin@naver.com', '$2a$12$4EbNBPJzszu/DQ11QEgdTuBoxPjZFV0EhOPWusw7FzSYt3.WMjMvK', 'admin', 'admin', false);
insert into "user" (email, password, name, nickname, is_social) values ('geonHui@naver.com', '$2a$12$kwmjJetpHfDH9IV2843.suBCzpiL.AIyfdKCAG/syMsP.P3KFx/2m', 'geonHui', 'geonHui', false);

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values (1, 'ROLE_USER');
insert into user_authority (user_id, authority_name) values (1, 'ROLE_ADMIN');
insert into user_authority (user_id, authority_name) values (2, 'ROLE_USER');