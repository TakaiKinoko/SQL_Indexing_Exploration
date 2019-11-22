import mysql.connector 
import time
import numpy as np 

##################################################
# CREATE DATABASE & INDICES BEFORE EXECUTING PYTHON SCRIPT

"""
CREATE DATABASE PYTHON6;
USE PYTHON6

create table Employee(ID int, Name varchar(20), Salary int, Manager int, Department varchar(20));
create table Emp_btree(ID int, Name varchar(20), Salary int, Manager int, Department varchar(20));
create table Emp_hash(ID int, Name varchar(20), Salary int, Manager int, Department varchar(20));

create table Emp_test(ID int, Name varchar(20), Salary int, Manager int, Department varchar(20));
ALTER TABLE Emp_test ADD UNIQUE (ID);
ALTER TABLE Emp_test ADD INDEX (Department);

CREATE INDEX emp_btree_ind ON Emp_btree (ID) USING BTREE;
CREATE INDEX emp_hash_ind ON Emp_hash (ID) USING HASH;
"""
##################################################
cnx = mysql.connector.connect(user='root', password="<PASSWORD>", 
                            host='localhost',  
                            database='PYTHON6')  
cursor = cnx.cursor() 
timings1 = [] # record all timings for table w/o index 
timings2 = [] # record all timings for table with BTREE index
timings3 = [] # record all timings for table with HASH index
timings4 = [] # record all timings for table with UNIQUE and SECONDARY indes
fout1 = open("timings_no_index.txt","w+")
fout2 = open("timings_with_index_btree.txt","w+")
fout3 = open("timings_with_index_hash.txt","w+")
fout4 = open("timings_with_unique&secondary_ind.txt", "w+")
##################################################
# WITHOUT INDEX
##################################################
add_employee = ("INSERT INTO Employee " 
                "(ID, Name, Salary, Manager, Department) "               
                "VALUES (%s, %s, %s, %s, %s)") 

with open("emp") as fp: 
    cnt = 1 
    for line in fp: 
        data = line.strip().split('|') 
        start = time.time()
        cursor.execute(add_employee, data)
        end = time.time()
        fout1.write("%dth insertion cost %.5fms.\n" % (cnt, 1000*(end-start)))
        timings1.append(1000*(end-start))
        cnt += 1 

fout1.close()
cnx.commit()

##################################################
# WITH UNIQUE + SECONDARY INDEX
##################################################
# new table name: Emp
add_emp = ("INSERT INTO Emp_test " 
            "(ID, Name, Salary, Manager, Department) "               
            "VALUES (%s, %s, %s, %s, %s)") 

with open("emp") as fp: 
    cnt = 1 
    for line in fp: 
        data = line.strip().split('|') 
        start = time.time()
        cursor.execute(add_emp, data)
        end = time.time()
        fout4.write("%dth insertion cost %.5fms.\n" % (cnt, 1000*(end-start)))
        timings4.append(1000*(end-start))
        cnt += 1 

fout4.close()
cnx.commit()


##################################################
# WITH BTREE INDEX
##################################################
# new table name: Emp
add_emp = ("INSERT INTO Emp_btree " 
            "(ID, Name, Salary, Manager, Department) "               
            "VALUES (%s, %s, %s, %s, %s)") 

with open("emp") as fp: 
    cnt = 1 
    for line in fp: 
        data = line.strip().split('|') 
        start = time.time()
        cursor.execute(add_emp, data)
        end = time.time()
        fout2.write("%dth insertion cost %.5fms.\n" % (cnt, 1000*(end-start)))
        timings2.append(1000*(end-start))
        cnt += 1 

fout2.close()
cnx.commit()

##################################################
# WITH HASH INDEX
##################################################
# new table name: Emp_hash
add_emp_hash = ("INSERT INTO Emp_hash " 
            "(ID, Name, Salary, Manager, Department) "               
            "VALUES (%s, %s, %s, %s, %s)") 

with open("emp") as fp: 
    cnt = 1 
    for line in fp: 
        data = line.strip().split('|') 
        start = time.time()
        cursor.execute(add_emp_hash, data)
        end = time.time()
        fout3.write("%dth insertion cost %.5fms.\n" % (cnt, 1000*(end-start)))
        timings3.append(1000*(end-start))
        cnt += 1 

fout3.close()
cnx.commit()

# house keeping
cnx.close()

##################################################
# COMPARISON
##################################################
print(f"Average cost of insertion for table WITHOUT index is: {np.mean(timings1)}")  
print(f"Average cost of insertion for table with BTREE index is: {np.mean(timings2)}")  
print(f"Average cost of insertion for table with HASH index is: {np.mean(timings3)}")  
print(f"Average cost of insertion for table with UNIQUE + SECONDARY index is: {np.mean(timings4)}") 


##################################################
# PLOT
##################################################
plt.scatter(np.arange(cnt-1), timings1)
plt.show()

plt.scatter(np.arange(cnt-1), timings4)
plt.show()