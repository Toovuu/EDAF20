USE lab1;
--
SELECT * FROM Students;
-- a 
SELECT firstName, lastName 
FROM Students;
-- b
SELECT firstName, lastName 
FROM Student 
ORDER BY lastName;

SELECT firstName, lastName 
FROM Student 
ORDER BY firstName;
-- c
SELECT * 
FROM Students 
WHERE pNBR 
LIKE '75%';
-- d
SELECT COUNT(*) 
FROM Students 
WHERE mod(substring(pNBR,10,1),2)=0;
-- e
SELECT COUNT(*) 
FROM Students;
-- f
SELECT * 
FROM Courses 
WHERE courseCode 
LIKE 'FMA%';
-- g
SELECT * 
FROM Courses 
WHERE credits > 5;
-- h
SELECT courseCode 
FROM TakenCourses 
WHERE pNBR = '790101-1234';
-- i
SELECT courseName, credits 
FROM Courses 
JOIN TakenCourses 
ON Courses.courseCode=TakenCourses.courseCode 
WHERE pNBR = '790101-1234';
-- j
SELECT SUM(credits) 
FROM Courses
JOIN TakenCourses
ON Courses.courseCode=TakenCourses.courseCode 
WHERE pNBR = '790101-1234';
-- k
SELECT AVG(grade) 
FROM TakenCourses
JOIN Courses
ON Courses.courseCode=TakenCourses.courseCode 
WHERE pNBR = '790101-1234';
-- lh
SELECT courseCode 
FROM TakenCourses
JOIN Students
ON TakenCourses.pNbr = Students.pNbr
WHERE Students.firstName = 'Eva' AND Students.lastName = 'Alm';
-- li
SELECT courseName, credits
FROM Courses
JOIN TakenCourses 
ON Courses.courseCode = TakenCourses.courseCode
JOIN Students 
ON TakenCourses.pNbr = Students.pNbr
WHERE Students.firstName = 'Eva' AND Students.lastName = 'Alm';
-- lj
SELECT  SUM(credits)
FROM Courses
JOIN TakenCourses 
ON Courses.courseCode = TakenCourses.courseCode
JOIN Students 
ON TakenCourses.pNbr = Students.pNbr
WHERE Students.firstName = 'Eva' AND Students.lastName = 'Alm';
-- lk
SELECT AVG(grade)
FROM TakenCourses
JOIN Students
ON TakenCourses.pNbr = Students.pNbr
WHERE Students.firstName = 'Eva' AND Students.lastName = 'Alm';
-- m
SELECT firstName, lastName
FROM Students 
WHERE pNbr NOT IN(
SELECT pNbr
FROM TakenCourses);
-- n
CREATE VIEW average AS
SELECT pNbr, AVG(grade) as avg
FROM TakenCourses
GROUP BY pNbr;
SELECT *
FROM average
WHERE avg =(
SELECT MAX(avg)
FROM average);
-- o
select Students.pNbr, COALESCE(SUM(credits),0)
FROM students
LEFT JOIN takencourses ON takencourses.pnbr = students.pnbr
LEFT JOIN courses ON courses.courseCode = takencourses.courseCode
GROUP BY students.pnbr;
-- p
select firstName, lastName, COALESCE(SUM(credits),0)
FROM students
LEFT JOIN takencourses ON takencourses.pnbr = students.pnbr
LEFT JOIN courses ON courses.courseCode = takencourses.courseCode
GROUP BY students.pnbr;
-- q
SELECT firstName, lastName, COUNT(*)
FROM Students
GROUP BY firstName, lastName
HAVING COUNT(*)>1;

















 




