{\rtf1\ansi\ansicpg1252\cocoartf1404\cocoasubrtf460
{\fonttbl\f0\fswiss\fcharset0 Helvetica;\f1\fnil\fcharset0 Monaco;}
{\colortbl;\red255\green255\blue255;}
\margl1440\margr1440\vieww25400\viewh16000\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 \
\pard\pardeftab720\partightenfactor0

\f1\fs22 \cf0 **********************************************************************************************************************************************************************************************
\f0\fs24 \
\
please see the DBSqlLite.pdf to understand the requirements of the project\
\

\f1\fs22 **********************************************************************************************************************************************************************************************\
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 \
To run the DBSqlLite.jave file, apache commons library jar file is required.\
The required jar file is available in the Lib folder with the name \'93commons-lang3-3.4.jar\'94\
This jar file is required to be configured and then imported(importing already done in the code using the statement  \'93
\f1\fs22 import org.apache.commons.lang3.StringUtils;\'94
\f0\fs24 \
\
\pard\pardeftab720\partightenfactor0

\f1\fs22 \cf0 **********************************************************************************************************************************************************************************************
\f0\fs24 \
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0
\cf0 \
The three .tbl files do not exist initially but can be created by uncommenting the \'93makeInformationSchema()\'94 line in the main method of the DBSqlLite.java code.\
The \'93makeInformationSchema()\'94 method should be run only once i.e comment the line once the three .tbl files are created.\
\
\pard\pardeftab720\partightenfactor0

\f1\fs22 \cf0 **********************************************************************************************************************************************************************************************
\f0\fs24 \
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0
\cf0 \
The supported commands can be seen by typing \'93help;\'94.\
Below is the detailed list of commands that can be given as user commands/input.\
\
Note: In the below commands, <return> means to press the key \'93Enter\'94 or \'93Return\'94.Similar is the case with  <space>.\
\
\pard\pardeftab720\partightenfactor0

\f1\fs22 \cf0 **********************************************************************************************************************************************************************************************\
\
show Schemas;\
Displays all the schemas including the Information_Schema\
\
**********************************************************************************************************************************************************************************************\
\
create Schema;<return><SchemaName>;\
Creates a new Schema with the name \'93SchemaName\'94\
\
Eg: Create Schema;<return>library;\
\
**********************************************************************************************************************************************************************************************\
\
use;<return><SchemaName>;\
Selects the schema with the name \'93SchemaName\'94\
\
Eg: Use;<return>library;\
\
**********************************************************************************************************************************************************************************************\
\
show tables;\
Shows all the tables in the selected schema.\
\
**********************************************************************************************************************************************************************************************\
\
create table;<return><TableName>\{<return><ColumnName1><space><ColumnName1 Data type><space><ColumnName1 Primary key>ColumnName1 NOT NULL> , <ColumnName12><space><ColumnName2 Data type><space><ColumnName2 Primary key>ColumnName2 NOT NULL>\};\
\
Creates a table with the name \'93TableName\'94 with two columns ColumnName1 and ColumnName2.\
\
Note: In the above command, Primary Key/NOT NULL are optional and only one of these two should be given.If none of these two are given, the engine considers the column as non Primary and nullable.\
\
Eg: Create table;<return>Book\{<return>Book_id int PRIMARY KEY,Title varchar(20) NOT NULL,Branch_name varchar(20)\};\
\
**********************************************************************************************************************************************************************************************\
\
insert into table;<return><TableName><space>VALUES<space><return>(value1,value2);\
\
inserts a row into the table with name \'93TableName\'94 with the column values as value1 and value2.\
\
Eg: insert into table;<return>Book VALUES <return>(5,Harry Potter,Addison);\
\
**********************************************************************************************************************************************************************************************\
\
select * from;<return><TableName><space>WHERE<space><return><ColumnName><space><operator><space><ColumnValue>;\
\
selects all the rows that satisfy the condition on ColumnName , from the table \'93TableName\'94.\
\
Eg: select * from;<return>Book WHERE <return>Book_id = 5;\
\
**********************************************************************************************************************************************************************************************\
\
\
\
\
\
\

\f0\fs24 \
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0
\cf0 \
}