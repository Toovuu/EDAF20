USE lab2;

DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS MovieShow;
DROP TABLE IF EXISTS Movie;
DROP TABLE IF EXISTS Theater;
DROP TABLE IF EXISTS Users;


CREATE TABLE Users(
userName varchar(50) not null,
realName varchar(50) not null,
phoneNbr char(10) not null,
primary key(userName)
);

CREATE TABLE Theater(
theaterName varchar(50) not null,
nbrSeats Integer not null,
primary key(theaterName)
);

CREATE TABLE Movie(
movieName varchar(50) not null,
movieID varchar(50) not null,
primary key(movieID)
);

CREATE TABLE MovieShow(
movieShowID varchar(50) not null,
theaterName varchar(50) not null,
movieID varchar(50) not null,
showDate Date,
primary key(movieShowID),
foreign key (theaterName) references Theater(theaterName),
foreign key (movieID) references Movie(movieID)
);

CREATE TABLE Ticket(
resNbr Integer AUTO_INCREMENT,
userName varchar(50) not null,
movieShowID varchar(50) not null,
primary key(resNbr),
foreign key (userName) references Users(userName),
foreign key (movieShowID) references  MovieShow(movieShowID)
);

INSERT INTO Users(userName, realNAme, phoneNbr) VALUES
('Nick', 'Nicklas Borg', '0731234566'),
('Kalle', 'Karl Larsson', '0761234567'),
('Dicky', 'Richard McBalle', '0701234567'),
('Tom', 'Tomas Borg', '0731231231');
-- 8

INSERT INTO Theater(theaterName, nbrSeats) VALUES
('SF Helsingborg', 100),
('SF Lund', 150),
('SF Malmö', 125);

INSERT INTO Movie(movieName, movieID) VALUES
('KING KONG', 'kingkong123'),
('KONG KING', 'kongking123'),
('Film namn', 'filmid');

INSERT INTO MovieShow(movieShowID, theaterName, movieID, showDate) VALUES
('111', 'SF Helsingborg', 'filmid', '2020-10-10'),
('123', 'SF Lund', 'filmid', '2020-10-11'),
('113', 'SF Malmö', 'kingkong123', '2020-05-05');

INSERT INTO Ticket(userName, movieShowID) VALUES
('Dicky', '111');
INSERT INTO Ticket(userName, movieShowID) VALUES
('Nick', 123);
-- 8
INSERT INTO MovieShow(movieShowID, theaterName, movieID, showDate) VALUES
('121', 'SF NOT EXIST', 'filmid', '2020-10-03');
--
SELECT movieName FROM MovieShow
JOIN Movie ON MovieShow.movieID = Movie.movieID;
--
SELECT showDate FROM MovieShow 
WHERE movieID = 'filmid';
-- 
SELECT * FROM MovieShow
WHERE movieID = 'filmid';

select nbrseats, count(ticket.movieshowid), movieshow.theatername from movieshow
left join ticket on movieshow.movieshowid = ticket.movieshowid 
join theater on theater.theatername = movieshow.theatername 
join movie on movie.movieid = movieshow.movieid
group by movieshow.movieshowid;


select resnbr, theatername, moviename, showdate from ticket
join movieshow on ticket.movieshowid = movieshow.movieshowid
join movie on movie.movieid = movieshow.movieid;








