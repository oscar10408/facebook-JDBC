package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of uses with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */
            FirstNameInfo info = new FirstNameInfo();

            // Goal A
            ResultSet rst = stmt.executeQuery(
                "SELECT DISTINCT FIRST_NAME " +
                "FROM " + UsersTable + " " +
                "WHERE LENGTH(FIRST_NAME) = (SELECT DISTINCT MAX(LENGTH(FIRST_NAME)) AS LEN " +
                "FROM " + UsersTable + ") " +
                "ORDER BY FIRST_NAME");

            while (rst.next()) { 
                String max_firstname = rst.getString(1);
                info.addLongName(max_firstname);
            }

            // Goal B
            rst = stmt.executeQuery(
                "SELECT DISTINCT FIRST_NAME " +
                "FROM " + UsersTable + " " +
                "WHERE LENGTH(FIRST_NAME) = (SELECT DISTINCT MIN(LENGTH(FIRST_NAME)) AS LEN " +
                "FROM " + UsersTable + ") " +
                "ORDER BY FIRST_NAME");

            while (rst.next()) { 
                String min_firstname = rst.getString(1);
                info.addShortName(min_firstname);
            }

            // Goal C and D
            rst = stmt.executeQuery(
                "SELECT DISTINCT FIRST_NAME, COUNT(*) " +
                "FROM " + UsersTable + " " +
                "GROUP BY FIRST_NAME HAVING COUNT(*) = (SELECT MAX(COUNT(*)) " +
                "FROM " + UsersTable + " " +
                "GROUP BY FIRST_NAME)");

            while (rst.next()) { 
                String most_common_name = rst.getString(1);
                info.addCommonName(most_common_name);
                int commonCount = rst.getInt(2);
                info.setCommonNameCount(commonCount);
            }

            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }

    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT USER_ID, FIRST_NAME, LAST_NAME FROM " + UsersTable +
                " WHERE USER_ID NOT IN " +
                "(SELECT USER1_ID FROM " + FriendsTable +
                " UNION SELECT USER2_ID FROM " + FriendsTable + ") ORDER BY USER_ID"
            );

            while (rst.next()) { 
                Long uid = rst.getLong(1);
                String firstName = rst.getString(2);
                String lastName = rst.getString(3);
                results.add(new UserInfo(uid, firstName, lastName));
            }

            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
            "SELECT DISTINCT U.USER_ID, U.FIRST_NAME, U.LAST_NAME FROM " + 
            HometownCitiesTable + " UH, " + CurrentCitiesTable + " UC, " + UsersTable + " U" +
            " WHERE UH.USER_ID = UC.USER_ID AND U.USER_ID = UC.USER_ID" +
            " AND UH.HOMETOWN_CITY_ID != UC.CURRENT_CITY_ID" +
            " ORDER BY U.USER_ID"
            );

            while (rst.next()) {
                Long uid = rst.getLong(1);
                String firstName = rst.getString(2);
                String lastName = rst.getString(3);
                results.add(new UserInfo(uid, firstName, lastName));
            }
            
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
            
            ResultSet rst = stmt.executeQuery(
                "SELECT P.PHOTO_ID, A.ALBUM_ID, A.ALBUM_NAME, P.PHOTO_LINK, U.USER_ID, U.FIRST_NAME, U.LAST_NAME " +
                "FROM " + PhotosTable + " P, " + TagsTable + " T, " + UsersTable + " U, " + AlbumsTable + " A " +
                "WHERE P.PHOTO_ID = T.tag_photo_id AND T.Tag_subject_id = U.USER_ID AND A.album_id = P.album_id " + 
                "AND P.PHOTO_ID IN " +
                "( " +
                "SELECT tag_photo_id FROM " + 
                "(SELECT tag_photo_id, COUNT(*) " + 
                "FROM " + TagsTable + 
                " GROUP  BY tag_photo_id ORDER BY COUNT(*) DESC, tag_photo_id) " +
                "WHERE ROWNUM <= " + num + ") " +
                "ORDER BY P.PHOTO_ID, U.USER_ID");
            
            Long currentPhotoId = null;
            TaggedPhotoInfo tp = null;

            while (rst.next()) {
                Long photoId = rst.getLong(1);

                if (!photoId.equals(currentPhotoId)){
                    if (tp != null){
                        results.add(tp);
                    }

                    currentPhotoId = photoId;
                    Long albumId = rst.getLong(2);
                    String albumName = rst.getString(3);
                    String albumLink = rst.getString(4);
                    PhotoInfo p = new PhotoInfo(photoId, albumId, albumLink, albumName);
                    tp  = new TaggedPhotoInfo(p);
                }

                Long userId = rst.getLong(5);
                String firstName = rst.getString(6);
                String lastName = rst.getString(7);
                tp.addTaggedUser(new UserInfo(userId, firstName, lastName));

            }
            
            if (tp != null){
                results.add(tp);
            }
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */
            stmt.executeUpdate(
                "CREATE VIEW TEMP AS " + 
                "SELECT U1.USER_ID AS U1_ID, U2.USER_ID AS U2_ID ,count(*) AS CNT " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + TagsTable + " T1, " + TagsTable + " T2 " +
                "WHERE U1.USER_ID = T1.TAG_SUBJECT_ID AND U2.USER_ID = T2.TAG_SUBJECT_ID AND T1.TAG_PHOTO_ID = T2.TAG_PHOTO_ID " +  
                "AND U1.USER_ID < U2.USER_ID AND U1.GENDER = U2.GENDER " + 
                "AND U1.YEAR_OF_BIRTH IS NOT NULL AND U2.YEAR_OF_BIRTH IS NOT NULL AND abs(U1.YEAR_OF_BIRTH - U2.YEAR_OF_BIRTH) <= " + yearDiff +
                " GROUP BY U1.USER_ID, U2.USER_ID ORDER BY CNT DESC, U1_ID, U2_ID");

            // Step 2: Find the user info for the two users in each pair
            ResultSet rst = stmt.executeQuery(
                "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U1.YEAR_OF_BIRTH, " + 
                "U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME, U2.YEAR_OF_BIRTH, " +
                "P.PHOTO_ID, P.ALBUM_ID, A.ALBUM_NAME, P.PHOTO_LINK " +
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, TEMP S, " +
                PhotosTable + " P, " + AlbumsTable + " A," + TagsTable + " T1, " + TagsTable + " T2 " +  
                "WHERE (U1.USER_ID, U2.USER_ID) IN (SELECT S.U1_ID, s.U2_ID FROM TEMP S) " + 
                "AND S.U1_ID = U1.USER_ID AND S.U2_ID = U2.USER_ID " +
                "AND U1.USER_ID = T1.TAG_SUBJECT_ID AND U2.USER_ID = T2.TAG_SUBJECT_ID AND T1.tag_PHOTO_ID = T2.tag_PHOTO_ID " +
                "AND NOT EXISTS (SELECT F.USER1_ID , F.USER2_ID FROM " + FriendsTable + " F WHERE U1.USER_ID = F.USER1_ID AND U2.USER_ID = F.USER2_ID) " +
                "AND T1.tag_PHOTO_ID = P.PHOTO_ID AND P.ALBUM_ID = A.ALBUM_ID AND rownum <= " + num +
                " ORDER BY S.CNT DESC ,U1.USER_ID ASC,  U2.USER_ID ASC");
            
            Long cur1ID = null;
            Long cur2ID = null;
            MatchPair mp = null;
            
            while(rst.next()){
                Long u1UserId = rst.getLong(1);
                Long u2UserId = rst.getLong(5);

                if (!u1UserId.equals(cur1ID) || !u2UserId.equals(cur2ID)){
                    if (mp != null){
                        results.add(mp);
                    }

                    String firstName1 = rst.getString(2);
                    String lastName1 = rst.getString(3); 
                    Long user1Year = rst.getLong(4);
                    String firstName2 = rst.getString(6);
                    String lastName2 = rst.getString(7); 
                    Long user2Year = rst.getLong(8);

                    UserInfo u1 = new UserInfo(u1UserId, firstName1, lastName1);
                    UserInfo u2 = new UserInfo(u2UserId, firstName2, lastName2);
                    mp = new MatchPair(u1, user1Year, u2, user2Year);

                    cur1ID = u1UserId;
                    cur2ID = u2UserId;
                }

                Long photoID = rst.getLong(9);
                Long albumID = rst.getLong(10);
                String albumName = rst.getString(11);
                String link = rst.getString(12);

                PhotoInfo p = new PhotoInfo(photoID, albumID, link, albumName);
                mp.addSharedPhoto(p); 

            }
            
            results.add(mp);
            stmt.executeUpdate("DROP VIEW TEMP");

            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */

            stmt.executeUpdate(
                "CREATE VIEW bidirectional_friendship AS " + 
                "SELECT USER1_ID, USER2_ID FROM " + FriendsTable +
                " UNION " +
                "SELECT USER2_ID, USER1_ID FROM " + FriendsTable);

            stmt.executeUpdate(
                "CREATE VIEW common_friends AS " +
                "SELECT F1.USER1_ID as U1_ID, F2.USER1_ID AS U2_ID, F1.USER2_ID AS COMMON_ID " + 
                "FROM bidirectional_friendship F1 JOIN bidirectional_friendship F2 ON F1.USER2_ID = F2.USER2_ID " +
                "WHERE F1.USER1_ID < F2.USER1_ID " + 
                "AND NOT EXISTS " + 
                "(SELECT 1 FROM bidirectional_friendship BF " + 
                "WHERE BF.USER1_ID = F1.USER1_ID AND BF.USER2_ID = F2.USER1_ID)"
            );   
            
            stmt.executeUpdate(
                "CREATE VIEW Top_Npairs AS " +
                "SELECT * FROM (SELECT u1_ID, u2_id, COUNT(*) " +
                "from common_friends group by u1_id, u2_id " +
                "order by count(*) desc, u1_id) WHERE ROWNUM <= " + num
            );

            ResultSet rst = stmt.executeQuery(
                "SELECT CF.u1_ID, U1.FIRST_NAME, U1.LAST_NAME, " + 
                "CF.u2_id, U2.FIRST_NAME, U2.LAST_NAME, " + 
                "CF.COMMON_ID, U3.FIRST_NAME, U3.LAST_NAME " +
                "FROM common_friends CF JOIN Top_Npairs TN ON CF.u1_id = TN.u1_id AND CF.u2_id = TN.u2_id " +
                "JOIN " + UsersTable + " U1 ON U1.USER_ID = CF.u1_ID " +
                "JOIN " + UsersTable + " U2 ON U2.USER_ID = CF.u2_ID " +
                "JOIN " + UsersTable + " U3 ON U3.USER_ID = CF.COMMON_ID " +
                "ORDER BY CF.u1_ID, CF.u2_ID, CF.COMMON_ID"
            );

            Long u1Id = null;
            Long u2Id = null;
            UsersPair p = null;

            while (rst.next()){
                if (u1Id == null || (!u1Id.equals(rst.getLong(1))) && !u2Id.equals(rst.getLong(4))){
                    if (p != null){
                        results.add(p);
                    }
                    u1Id = rst.getLong(1);
                    String FirstName1 = rst.getString(2);
                    String LastName1 = rst.getString(3);
                    u2Id = rst.getLong(4);
                    String FirstName2 = rst.getString(5);
                    String LastName2 = rst.getString(6);

                    UserInfo u1 = new UserInfo(u1Id, FirstName1, LastName1);
                    UserInfo u2 = new UserInfo(u2Id, FirstName2, LastName2);                    
                    p = new UsersPair(u1, u2);
                }
                Long u3Id = rst.getLong(7);
                String FirstName3 = rst.getString(8);
                String LastName3 = rst.getString(9);
                UserInfo u3 = new UserInfo(u3Id, FirstName3, LastName3);
                p.addSharedFriend(u3);
            }

            if (p != null){
                results.add(p);
            }
           
            stmt.executeUpdate("DROP VIEW bidirectional_friendship");
    		stmt.executeUpdate("DROP VIEW common_friends");
    		stmt.executeUpdate("DROP VIEW Top_Npairs");
            
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            ResultSet rst = stmt.executeQuery(
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
                        "JOIN " + EventsTable + " E ON C.CITY_ID = E.EVENT_CITY_ID " +
                        "GROUP BY C.STATE_NAME))");
                
                Long cnt = null;
                String state = null;

                if (rst.next()){
                    cnt = rst.getLong(2);
                    state = rst.getString(1);
                }

                EventStateInfo info = new EventStateInfo(cnt);
                info.addState(state);

                while (rst.next()){
                    cnt = rst.getLong(2);
                    state = rst.getString(1);
                    info.addState(state);
                }
                
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info; // placeholder for compilation

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT USER_ID, FIRST_NAME, LAST_NAME FROM( " +
                "SELECT * FROM( " + 
                    "SELECT F1.USER2_ID USER_ID, U.FIRST_NAME, U.LAST_NAME, U.YEAR_OF_BIRTH, U.MONTH_OF_BIRTH, U.DAY_OF_BIRTH " + 
                    "FROM " + UsersTable + " U JOIN " + FriendsTable + " F1 ON U.USER_ID = F1.USER2_ID " +
                    "WHERE F1.USER1_ID = " + userID +
                    " UNION " + 
                    "SELECT F2.USER1_ID USER_ID, U.FIRST_NAME, U.LAST_NAME, U.YEAR_OF_BIRTH, U.MONTH_OF_BIRTH, U.DAY_OF_BIRTH " +
                    "FROM " + UsersTable + " U JOIN " + FriendsTable + " F2 ON U.USER_ID = F2.USER1_ID " +
                    "WHERE F2.USER2_ID = " + userID +
                ") " + 
                "ORDER BY YEAR_OF_BIRTH, MONTH_OF_BIRTH, DAY_OF_BIRTH, USER_ID DESC) WHERE ROWNUM <= 1");
            
            Long userIdOld = null;
            String firstNameOld = null;
            String lastNameOld = null;
            Long userIdYoung = null;
            String firstNameYoung = null;
            String lastNameYoung = null;

            while (rst.next()){
                userIdOld = rst.getLong(1);
                firstNameOld = rst.getString(2);
                lastNameOld = rst.getString(3);
            }
            
            UserInfo old = new UserInfo(userIdOld, firstNameOld, lastNameOld);

            rst = stmt.executeQuery(
                "SELECT USER_ID, FIRST_NAME, LAST_NAME FROM( " +
                "SELECT * FROM( " + 
                    "SELECT F1.USER2_ID USER_ID, U.FIRST_NAME, U.LAST_NAME, U.YEAR_OF_BIRTH, U.MONTH_OF_BIRTH, U.DAY_OF_BIRTH " + 
                    "FROM " + UsersTable + " U JOIN " + FriendsTable + " F1 ON U.USER_ID = F1.USER2_ID " +
                    "WHERE F1.USER1_ID = " + userID +
                    " UNION " +
                    "SELECT F2.USER1_ID USER_ID, U.FIRST_NAME, U.LAST_NAME, U.YEAR_OF_BIRTH, U.MONTH_OF_BIRTH, U.DAY_OF_BIRTH " +
                    "FROM " + UsersTable + " U JOIN " + FriendsTable + " F2 ON U.USER_ID = F2.USER1_ID " +
                    "WHERE F2.USER2_ID = " + userID +
                ") " + 
                "ORDER BY YEAR_OF_BIRTH DESC, MONTH_OF_BIRTH DESC, DAY_OF_BIRTH DESC, USER_ID DESC) WHERE ROWNUM <= 1");

            while (rst.next()){
                userIdYoung = rst.getLong(1);
                firstNameYoung = rst.getString(2);
                lastNameYoung = rst.getString(3);
            }
            
            UserInfo young = new UserInfo(userIdYoung, firstNameYoung, lastNameYoung);                

            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return new AgeInfo(old, young); // placeholder for compilation

            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT LEAST(U1.USER_ID, U2.USER_ID) AS USER_ID_1, " +
                       "GREATEST(U1.USER_ID, U2.USER_ID) AS USER_ID_2, " +
                       "U1.FIRST_NAME AS FIRST_NAME_1, " +
                       "U1.LAST_NAME AS LAST_NAME_1, " +
                       "U2.FIRST_NAME AS FIRST_NAME_2, " +
                       "U2.LAST_NAME AS LAST_NAME_2 " +
                "FROM " + UsersTable + " U1 " +
                "JOIN " + UsersTable + " U2 ON U1.LAST_NAME = U2.LAST_NAME " +
                    "AND ABS(U1.YEAR_OF_BIRTH - U2.YEAR_OF_BIRTH) < 10 " +
                    "AND U1.USER_ID < U2.USER_ID " +
                "JOIN " + FriendsTable + " F ON (F.USER1_ID = U1.USER_ID AND F.USER2_ID = U2.USER_ID) " +
                "JOIN " + HometownCitiesTable + " H1 ON U1.USER_ID = H1.USER_ID " +
                "JOIN " + HometownCitiesTable + " H2 ON U2.USER_ID = H2.USER_ID " +
                "WHERE H1.HOMETOWN_CITY_ID = H2.HOMETOWN_CITY_ID " +
                "ORDER BY USER_ID_1, USER_ID_2");

            while (rst.next()){
                Long userId1 = rst.getLong(1);
                Long userId2 = rst.getLong(2);
                String firstName1 = rst.getString(3);
                String lastName1 = rst.getString(4);
                String firstName2 = rst.getString(5);
                String lastName2 = rst.getString(6);

                UserInfo u1 = new UserInfo(userId1, firstName1, lastName1);
                UserInfo u2 = new UserInfo(userId2, firstName2, lastName2);
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            }
            
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
