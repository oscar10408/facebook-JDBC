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

---
## Starter Files
- **PublicFakebookOracleConstants.java**: Defines the schema, table name constants, and other key constants for Oracle database interaction. These constants must be used in query implementation to ensure accuracy and consistency.

- **FakebookOracleUtilities.java**: Provides a custom utility class (`FacebookArrayList`) for storing and printing query results.

- **FakebookOracleDataStructures.java**: Contains pre-defined data structures tailored for storing and reporting query results. Familiarity with these structures is crucial for accurate data handling.

- **FakebookOracle.java**: The abstract parent class that defines the structure of the application, including nine abstract methods for SQL queries. It also provides utility methods for printing query results.

- **StudentFakebookOracle.java**: The file where SQL queries are implemented. It defines the derived query class, with each of the nine required queries having its own function. Comments within the functions provide guidance on using data structures from `FakebookOracleDataStructures.java` and structuring queries. Use the 11 constants defined at the bottom of this class (from `PublicFakebookOracleConstants.java`) for referencing public dataset tables.

- **Makefile**:
* To compile your Java application, navigate to your project root directory (where the Makefile is located) and run make or make compile This will compile silently if there are no errors and will print any compilation problems to the command line.
* To run your queries and view the output, you have two options. If you want to run all your queries to compare your output to the
 provided solution output file, run make query-all . To run a single query to view the output, run make queryN where N is the query
 number. You may redirect output for output diff.
* To run your queries and measure their runtime, you again have two options. If you want to time all your queries, run make time-all .
 To measure the runtime of a single query, run make timeN where N is the query number.

- **PublicSolution.txt**: Contains the expected output for each query against the public dataset. Ensure your outputs match this file exactly when using `make query-all`. Trailing blank lines and headers must not be omitted.

- **PublicTime.txt**: Records the average runtime of the instructor's implementation against the public dataset on the CAEN system. Use `make time-all` to collect your runtime. Runtime instability is expected due to JDBC connection mechanisms, and reasonable buffer times are allowed on the Autograder.

