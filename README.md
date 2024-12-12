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

# Facebook Queries

## Query 1: First Names
Identify information about Fakebook users' first names. You need to find:
- The longest and shortest first names by length. In case of ties, report all tied names in alphabetical order.
- The most common first names and how many users have that first name. In case of ties, report all tied names in alphabetical order.

## Query 2: Lonely Users
Identify all Fakebook users who have no Fakebook friends. For each user without any friends, report:
- Their ID
- First name
- Last name
The users should be reported in ascending order by ID.

## Query 3: Users Who Live Away From Home
Identify all Fakebook users who no longer live in their hometown. For each such user, report:
- Their ID
- First name
- Last name
The results should be sorted in ascending order by the users' ID.

## Query 4: Highly-Tagged Photos
Identify the most highly-tagged photos. You will be given a `num` argument to return the top `num` photos with the most tagged users, sorted in descending order by the number of tagged users. If there are ties, list the photo with the smaller ID first. For each photo, report:
- The photo’s ID
- The ID of the album containing the photo
- The photo’s Fakebook link
- The name of the album containing the photo
Also, list the ID, first name, and last name of the users tagged in that photo, sorted by user ID.

## Query 5: Matchmaker
Suggest possible unrealized Fakebook friendships. Given two arguments, `num` and `yearDiff`, return the top `num` pairs of users who meet the following conditions:
- They are the same gender.
- They are tagged in at least one common photo.
- They are not friends.
- The difference in their birth years is less than or equal to `yearDiff`.
For each pair, report:
- The IDs, first names, and last names of the two users (list the smaller ID first).
- The photos in which they were tagged together, with each photo’s ID, Fakebook link, album ID, and album name.

## Query 6: Suggest Friends
Suggest possible unrealized Fakebook friendships based on mutual friends. Given a `num` argument, return the top `num` pairs of users who have the most mutual friends but are not friends themselves. A mutual friend is one who is a friend of both users. For each pair, report:
- The IDs, first names, and last names of the two users (list the smaller ID first).
- The IDs, first names, and last names of all their mutual friends, sorted by ID.

## Query 7: Event-Heavy States
Identify the states in which the most Fakebook events are held. Return the states with the most events, listed in ascending order by state name, along with the number of events held in those states.

## Query 8: Oldest and Youngest Friends
Identify the oldest and youngest friend of a particular Fakebook user. Given a `userID`, report:
- The ID, first name, and last name of the oldest and youngest friend of the user.
If two friends have the same birth date, report the one with the larger user ID.

## Query 9: Potential Siblings
Identify pairs of Fakebook users that might be siblings. Two users might be siblings if:
- They have the same last name.
- They have the same hometown.
- They are friends.
- The difference in their birth years is strictly less than 10 years.
Report each pair with the smaller user ID first and the larger user ID second.

# JDBC Statements and ResultSets

This project makes use of JDBC tools, specifically `Statement` and `ResultSet`, to execute SQL queries and process their results. The necessary Java libraries have already been imported, allowing you to focus on implementing and using these tools to interact with your database.

## Using Statements

In JDBC, a `Statement` object is used to execute queries and updates against the database. Each query function provided in this project has already created a `Statement` object named `stmt` that you can use directly. If you need to create a new `Statement` object, you can copy the try-with-resources block and change the variable name as needed.

To execute a query using a `Statement`, you should use the following method:

```java
Statement stmt = new Statement ( ... ) ;
 ResultSet rst = stmt.executeQuery ( ... ) ;
 while (rst.next ()) {
 ResultSet rst2 = stmt.executeQuery ( ... ) ;
 long val = rst.getLong (1);
 }
```

Reusing stmt for generating results in rst2 causes rst to close, leading to an exception when accessing its data. To handle multiple ResultSets, create a second Statement for the inner query and ensure it's declared outside the loop to avoid unnecessary reinitialization. Additionally, always close ResultSets before closing the Statement to ensure proper resource management. Refer to the implementation of Query 0 for resource closure examples.
