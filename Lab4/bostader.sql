use viktorbj; # Byt till eget användarnamn

drop table bostader; # Om det finns en tidigare databas

create table bostader (
lan varchar(64),
objekttyp varchar(64),
adress varchar(64),
area float,
rum int,
pris float,
avgift float
);


insert into bostader values ('Stockholm','Bostadsrätt','Polhemsgatan 1',30,1,1000000,1234);

insert into bostader values ('Stockholm','Bostadsrätt','Polhemsgatan 2',60,2,2000000,2345);

insert into bostader values ('Stockholm','Villa','Storgatan 1',130,5,1000000,3456);

insert into bostader values ('Stockholm','Villa','Storgatan 2',160,6,1000000,3456);

insert into bostader values ('Uppsala','Bostadsrätt','Gröna gatan 1',30,1,500000,1234);

insert into bostader values ('Uppsala','Bostadsrätt','Gröna gatan 2',60,2,1000000,2345);

insert into bostader values ('Uppsala','Villa','Kungsängsvägen 1',130,5,1000000,3456);

insert into bostader values ('Uppsala','Villa','Kungsängsvägen 2',160,6,1000000,3456);

insert into bostader values ('Stockholm','Bostadsrätt','Sveavägen 2',50,2,4000000,6456);

insert into bostader values ('Stockholm','Bostadsrätt','Bäckbornas v. 33',120,5,1400000,7456);

insert into bostader values ('Stockholm','Bostadsrätt','Rondellen 14', 60,2,1300000,3500);

insert into bostader values ('Uppsala','Bostadsrätt','Universitetsvägen 2',70,3,1250000,3476);

insert into bostader values ('Uppsala','Hyresrätt','Sankt olofsgatan 70',75,3,800000,2456);

insert into bostader values ('Gävle','Hyresrätt','Stockholmsgatan 60',100,4,150000,5000);

insert into bostader values ('Gävle','Bostadsrätt','Knutgatan 65',30,4,800000,2500);

insert into bostader values ('Gävle','Bostadsrätt','Knutgatan 69',35,4,805000,2700);

insert into bostader values ('Gävle','Villa','Båtgatan 632',130,8,1800000,5000);

insert into bostader values ('Gävle','Villa','Båtgatan 652',160,7,2000000,7000);


SELECT * FROM bostader
