# Exploration Into SQL Indexing Method

## 1. Implement B-Trees and Hash structures

* Find in-memory implementations of B-trees and hash structures and fill them each with data that's provided here: http://cs.nyu.edu/cs/faculty/shasha/papers/myindex. Then make your program able to run from standard input a file of commands.

* Command Format:  

    * ```insert(key,value)```
    
    * ```delete(key)```

    * ```search(key))```

* An insert(k,v) should be interpreted as an __”upsert”__ meaning that if there is already a record with key value k, then that record’s value should be changed to v. The program should show timings for all operations together and write the total time and the resulting table to standard out after all operations.

## 2. Quantitize Index Effect On Mysql

* schema: 
    * Employee(ID, Name, Salary, Manager, Department) 
    * Course(EmpID, CourseID, Prof, Grade)

* data: 
    * http://cs.nyu.edu/cs/faculty/shasha/papers/emp 
    * http://cs.nyu.edu/cs/faculty/shasha/papers/course

* load data, to table Employee and table Course. Show the timings and the results of the questions below: 
    * (a) Find the number of employees who earn at least 50 more than their managers.
    * (b) For those departments in which more than one person takes courses, find the average salary by department.
    * (c) Find the average salary earned by people taking the course of prof1038.

* Now show how to add indexes in such a way that each query requires less time. That is, show the create index statements to use and give new timing results.

## 3. Experiment With Clustered Secondary Index With mysql From Python

* (a) Create an empty emp table without indexes. Perform 100,000 inserts (taken in any order, but without duplicates) from the emp file into the employee table. Record the timing. These will be insert statements with explicit values, one row per insert statement. To do that, you will use a programming language like Python or Java and interface with the database for each insert.
* (b) Recreate an empty emp table but this time with a unique index on ID and an index on department. Perform 100,000 inserts from the emp file into the employee table Record the timings. If an insert with the same ID as an existing record comes in, then refuse that new insert.

