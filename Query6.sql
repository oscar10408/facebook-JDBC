CREATE VIEW bidirectional_friendship AS
SELECT USER1_ID, USER2_ID FROM project2.Public_Friends
UNION
SELECT USER2_ID, USER1_ID FROM project2.Public_Friends;

"CREATE VIEW bidirectional_friendship AS " + 
"SELECT USER1_ID, USER2_ID FROM " + FriendsTable +
" UNION " +
"SELECT USER2_ID, USER1_ID FROM " + FriendsTable

CREATE VIEW common_friends AS
SELECT F1.USER1_ID as U1_ID, F2.USER1_ID AS U2_ID, F1.USER2_ID AS COMMON_ID
FROM bidirectional_friendship F1 JOIN bidirectional_friendship F2 ON F1.USER2_ID = F2.USER2_ID
WHERE F1.USER1_ID < F2.USER1_ID 
AND NOT EXISTS 
(SELECT 1 FROM bidirectional_friendship BF
WHERE BF.USER1_ID = F1.USER1_ID AND BF.USER2_ID = F2.USER1_ID);

"CREATE VIEW common_friends AS " +
"SELECT F1.USER1_ID as U1_ID, F2.USER1_ID AS U2_ID, F1.USER2_ID AS COMMON_ID " + 
"FROM bidirectional_friendship F1 JOIN bidirectional_friendship F2 ON F1.USER2_ID = F2.USER2_ID " +
"WHERE F1.USER1_ID < F2.USER1_ID " + 
"AND NOT EXISTS " + 
"(SELECT 1 FROM bidirectional_friendship BF " + 
"WHERE BF.USER1_ID = F1.USER1_ID AND BF.USER2_ID = F2.USER1_ID)"

CREATE VIEW Top_Npairs AS
SELECT * FROM (SELECT u1_ID, u2_id, COUNT(*)
from common_friends group by u1_id, u2_id
order by count(*) desc, u1_id) WHERE ROWNUM <= 5;

"CREATE VIEW Top_Npairs AS " +
"SELECT * FROM (SELECT u1_ID, u2_id, COUNT(*) " +
"from common_friends group by u1_id, u2_id " +
"order by count(*) desc, u1_id) WHERE ROWNUM <= " + num

SELECT CF.u1_ID, U1.FIRST_NAME, U1.LAST_NAME,
CF.u2_id, U2.FIRST_NAME, U2.LAST_NAME,
CF.COMMON_ID, U3.FIRST_NAME, U3.LAST_NAME
FROM common_friends CF JOIN Top_Npairs TN ON CF.u1_id = TN.u1_id AND CF.u2_id = TN.u2_id
JOIN project2.Public_Users U1 ON U1.USER_ID = CF.u1_ID
JOIN project2.Public_Users U2 ON U2.USER_ID = CF.u2_ID
JOIN project2.Public_Users U3 ON U3.USER_ID = CF.COMMON_ID
ORDER BY CF.u1_ID, CF.u2_ID;

"SELECT CF.u1_ID, U1.FIRST_NAME, U1.LAST_NAME, " + 
"CF.u2_id, U2.FIRST_NAME, U2.LAST_NAME, " + 
"CF.COMMON_ID, U3.FIRST_NAME, U3.LAST_NAME " +
"FROM common_friends CF JOIN Top_Npairs TN ON CF.u1_id = TN.u1_id AND CF.u2_id = TN.u2_id " +
"JOIN " + UsersTable + " U1 ON U1.USER_ID = CF.u1_ID " +
"JOIN " + UsersTable + " U2 ON U2.USER_ID = CF.u2_ID " +
"JOIN " + UsersTable + " U3 ON U3.USER_ID = CF.COMMON_ID " +
"ORDER BY CF.u1_ID, CF.u2_ID, CF.COMMON_ID"