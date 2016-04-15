###########################################################################################
Please see the DBSqlLite.pdf to understand the requirements of the project
###########################################################################################

To run the DBSqlLite.jave file, apache commons library jar file is required.
The required jar file is available in the Lib folder with the name “commons-lang3-3.4.jar”
This jar file is required to be configured and then imported(importing already done in the code using the statement  “import org.apache.commons.lang3.StringUtils;”
###########################################################################################

The three .tbl files do not exist initially but can be created by uncommenting the “makeInformationSchema()” line in the main method of the DBSqlLite.java code.
The “makeInformationSchema()” method should be run only once i.e comment the line once the three .tbl files are created.
###########################################################################################

The supported commands can be seen by typing “help;”.
Below is the detailed list of commands that can be given as user commands/input.

Note: In the below commands, <return> means to press the key “Enter” or “Return”.Similar is the case with  <space>.
###########################################################################################

show Schemas;
Displays all the schemas including the Information_Schema
###########################################################################################

create Schema;<return><SchemaName>;
Creates a new Schema with the name “SchemaName”

Eg: Create Schema;<return>library;
###########################################################################################

use;<return><SchemaName>;
Selects the schema with the name “SchemaName”

Eg: Use;<return>library;
###########################################################################################

show tables;
Shows all the tables in the selected schema.
###########################################################################################

create table;<return><TableName>{<return><ColumnName1><space><ColumnName1 Data type><space><ColumnName1 Primary key>ColumnName1 NOT NULL> , <ColumnName12><space><ColumnName2 Data type><space><ColumnName2 Primary key>ColumnName2 NOT NULL>};

Creates a table with the name “TableName” with two columns ColumnName1 and ColumnName2.

Note: In the above command, Primary Key/NOT NULL are optional and only one of these two should be given.If none of these two are given, the engine considers the column as non Primary and nullable.

Eg: Create table;<return>Book{<return>Book_id int PRIMARY KEY,Title varchar(20) NOT NULL,Branch_name varchar(20)};
###########################################################################################

insert into table;<return><TableName><space>VALUES<space><return>(value1,value2);

inserts a row into the table with name “TableName” with the column values as value1 and value2.

Eg: insert into table;<return>Book VALUES <return>(5,Harry Potter,Addison);
###########################################################################################

select * from;<return><TableName><space>WHERE<space><return><ColumnName><space><operator><space><ColumnValue>;

selects all the rows that satisfy the condition on ColumnName , from the table “TableName”.

Eg: select * from;<return>Book WHERE <return>Book_id = 5;




