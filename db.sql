drop table lastTraining;
drop table event;
drop table npcTrainer;
drop table notification;
drop table report;
drop table stadium;
drop table injury;
drop table npcPlayer;
drop table npc;
drop table message;
drop table chat;
drop table game;
drop table team_league;
drop table league;
drop table stadiumTraining;
drop table team;
drop table player;

create table player
(
	idPlayer integer primary key,
	login varchar(30) not null unique,
	password varchar(64) not null,
	email varchar(30) not null unique,
	type varchar(1) check( type in ('a', 'm', 'p') ) not null,
	active number(1,0) check (active in (1,0)) not null,
	banned number(1,0) check (banned in (1,0)) not null
);

create table team
(
	idTeam integer primary key,
	idPlayer integer not null,
	name varchar(100) not null,
	prestige integer not null,
	budget integer not null,
	constraint fk_team_idplayer foreign key (idPlayer) references player(idPlayer)
);

create table stadiumTraining
(
  	idStadium integer primary key,
  	idTeam integer not null,
  	xpMultiplier integer not null,
    cost integer not null,
    constraint fk_st_idteam foreign key (idTeam) references team(idTeam)
);

create table league
(
	idLeague integer primary key,
	name varchar(100) not null,
	requiredPrestige integer not null,
 	requiredNumberOfTeams integer not null,
	actualNumberOfTeams integer not null
);

create table team_league
(
	idTeam integer not null,
	idLeague integer not null,
	constraint pk_pt primary key (idTeam, idLeague),
	constraint fk_tl_idteam foreign key (idTeam) references team(idTeam),
	constraint fk_tl_idleague foreign key (idLeague) references league(idLeague)
);

create table game
(
	idGame integer primary key,
	idLeague integer not null,
  	queueNumber integer not null,
	idTeam1 integer,
	idTeam2 integer,
	score varchar(5),
	datePlayed date not null,
	ticketPrice integer not null,
	constraint fk_game_idLeague foreign key (idLeague) references league(idLeague),
	constraint fk_game_idTeam1 foreign key (idTeam1) references team(idTeam),
	constraint fk_game_idTeam2 foreign key (idTeam2) references team(idTeam)
);

create table chat
(
	idChat integer primary key,
	idLeague integer not null,
	constraint fk_chat_idleague foreign key (idLeague) references league(idLeague)
);

create table message
(
	idMessage integer primary key,
	idChat integer not null,
	idPlayer integer not null,
	text varchar(120) not null,
	datePosted date not null,
	removed varchar(1) check( removed in ('0', '1') ) not null,
	constraint fk_msg_idchat foreign key (idChat) references chat(idChat),
	constraint fk_msg_idplayer foreign key (idPlayer) references player(idPlayer)
);

create table npc
(
	idNpc integer primary key,
	idTeam integer,
	name varchar(100) not null,
	cost integer not null,
	type varchar(1) check( type in ('t', 'p') ) not null,
	constraint fk_npc_idteam foreign key (idTeam) references team(idTeam)
);

create table npcPlayer
(
	idNpc integer primary key,
	position varchar(1) check( position in ('g', 'd', 'm', 'a') ) not null,
	stamina integer not null,
	strength integer not null,
	agility integer not null,
	speed integer not null,
	endurance integer not null,
	health integer not null,
	fatigue integer not null,
	inSquad number(1,0) check (inSquad in (1,0)) not null,
	transfer number(1,0) check (transfer in (1,0)) not null,	
	constraint fk_npcplayer_idNpc foreign key (idNpc) references npc(idNpc)
);

create table injury
(
	idInjury integer primary key,
	idNpc integer not null,
	type varchar(1) not null,
	startDate date not null,
	endDate date not null,
	intensity integer not null,
	constraint fk_injury_idNpc foreign key (idNpc) references npcplayer(idNpc)
);

create table stadium
(
	idStadium integer not null,
	idTeam integer,
	country varchar(20) not null,
	city varchar(20) not null,
	capacity integer not null,
	constraint fk_stadium_idTeam foreign key (idTeam) references team(idTeam)
);

create table report
(
	idReport integer primary key,
	idPlayerSource integer not null,
	idPlayerTarget integer not null,
	reason varchar(200) not null,
	dateReported date not null,
	constraint fk_report_source foreign key (idPlayerSource) references player(idPlayer),
	constraint fk_report_target foreign key (idPlayerTarget) references player(idPlayer)
);

create table notification
(
	idNotification integer primary key,
	idPlayer integer,
	text varchar(500) not null,
	startDate date not null,
	finishDate date not null,
	type varchar(1) check( type in ('a', 'm', 'p', 'n', 'e', 's') ) not null, -- admins, mods, players, non-admins, everyone, single (fill idPlayer)
	constraint fk_notif_player foreign key (idPlayer) references player(idPlayer)
);

create table event
(
	idEvent integer primary key,
	title varchar(40) not null,
	type varchar(10) not null,
	startDate date not null,
	finishDate date not null
);

create table npcTrainer
(
	idNpc integer primary key,
	trainerType varchar(1) check(trainerType in('c','m','p','n','r')) not null,
	influence integer not null,
	constraint fk_npctrainer_idNpc foreign key (idNpc) references npc(idNpc)
);

create table lastTraining
(
	idTeam integer primary key,
	trainingDate date not null,
	constraint fk_lastTraining_idTeam foreign key (idTeam) references team(idTeam)
);

insert into player values(1, 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'none', 'a', 1, 0);
insert into league values(0, 'test league please ignore', 999999, 16, 0);
insert into chat values(0, 0);