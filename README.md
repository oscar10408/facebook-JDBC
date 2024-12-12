# facebook-JDBC

## Introduction
In this project, I developed a Java application that executes SQL queries against a relational database and processes the query results into specified data structures. While much of the application's structural foundation was provided, I implemented the SQL query logic and result handling to meet the project's requirements. This hands-on experience enhanced my skills in SQL query design and database application programming, providing practical insights into real-world database systems.

## Public Data Set
The Fakebook dataset used in this project is structured similarly to the dataset created in Project 1, with notable differences, such as the absence of "Messages" and "Participants" tables. The following tables are included:

- **Public_Users**
- **Public_Friends**
- **Public_Cities**
- **Public_User_Current_Cities**
- **Public_User_Hometown_Cities**
- **Public_Programs**
- **Public_Education**
- **Public_User_Events**
- **Public_Albums**
- **Public_Photos**
- **Public_Tags**

### Key Constraints:
- Every row in `Public_Friends` follows the invariant `user1_id < user2_id`, ensuring users cannot be friends with themselves and preventing duplicate friendship entries.
- Tables are stored under the `project2` schema. When using SQL*Plus, tables should be accessed using the format `project2.<tableName>`. Note: This convention differs when implementing queries in Java.
