SELECT TEMP.STATE_NAME, TEMP.MAX_COUNT
FROM (
    SELECT C.STATE_NAME, COUNT(*) AS MAX_COUNT
    FROM project2.Public_Cities C
    JOIN project2.Public_User_Events E ON C.CITY_ID = E.EVENT_CITY_ID
    GROUP BY C.STATE_NAME
)TEMP
WHERE TEMP.MAX_COUNT = (
    SELECT MAX(MAX_COUNT)
    FROM (
        SELECT C.STATE_NAME, COUNT(*) AS MAX_COUNT
        FROM project2.Public_Cities C
        JOIN project2.Public_User_Events E ON C.CITY_ID = E.EVENT_CITY_ID
        GROUP BY C.STATE_NAME
    ));


"SELECT TEMP.STATE_NAME, TEMP.MAX_COUNT " + 
"FROM ( " +
    "SELECT C.STATE_NAME, COUNT(*) AS MAX_COUNT " +
    "FROM " + CitiesTable + " C " +
    "JOIN " + EventsTable + " E ON C.CITY_ID = E.EVENT_CITY_ID " +
    "GROUP BY C.STATE_NAME)TEMP " +
"WHERE TEMP.MAX_COUNT = ( " +
    "SELECT MAX(MAX_COUNT) " +
    "FROM (" + 
        "SELECT C.STATE_NAME, COUNT(*) AS MAX_COUNT " +
        "FROM " + CitiesTable + " C " +
        "JOIN " + EventsTable + "E ON C.CITY_ID = E.EVENT_CITY_ID " +
        "GROUP BY C.STATE_NAME))"
