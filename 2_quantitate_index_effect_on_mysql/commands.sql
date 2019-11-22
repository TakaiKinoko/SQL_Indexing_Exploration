-- @author: Fang Cabrera

CREATE DATABASE hw3;
USE hw3

-- create tables
create table Employee(ID int, Name varchar(20), Salary int, Manager int, Department varchar(20));
create table Course(EmpID int, CourseID int, Prof varchar(20), Grade int);

-- load data
LOAD DATA LOCAL INFILE 'data/emp.txt'
INTO TABLE Employee FIELDS TERMINATED BY '|'
LINES TERMINATED BY '\n'
(ID, Name, Salary, Manager, Department);

LOAD DATA LOCAL INFILE 'data/course.txt'
INTO TABLE Course FIELDS TERMINATED BY '|'
LINES TERMINATED BY '\n' 
(EmpID, CourseID, Prof, Grade);

-- a
SELECT COUNT(emp1.ID)
FROM Employee emp1, Employee emp2
WHERE emp1.Salary >= 50 + emp2.Salary and emp1.Manager = emp2.ID;

-- b 
SELECT emp.Department, AVG(emp.Salary)
FROM Employee emp
WHERE emp.Department = ANY 
(
-- get all the departments that has more than one employee enrolled in courses
SELECT emp.Department
FROM Employee emp, Course stu
WHERE stu.EmpID = emp.ID
GROUP BY emp.Department
HAVING COUNT(emp.Department) > 1
)
GROUP BY emp.Department; -- AVG has to happen after GROUP BY


-- c
SELECT AVG(e.Salary)
FROM Employee e, Course c
WHERE e.ID = c.EmpID and c.Prof = 'prof1038';


-- create index
CREATE INDEX course_index ON Course (EmpID) USING BTREE;
CREATE INDEX emp_index ON Employee (ID) USING BTREE;

-- drop index
DROP INDEX course_index ON Course;
DROP INDEX emp_index ON Employee;