SELECT P.PHOTO_ID, A.ALBUM_ID, A.ALBUM_NAME, P.PHOTO_LINK, U.USER_ID, U.FIRST_NAME, U.LAST_NAME
FROM project2.public_photos P, project2.public_tags T, project2.public_users U, project2.public_albums A
WHERE P.PHOTO_ID = T.tag_photo_id AND T.Tag_subject_id = U.USER_ID AND A.album_id = P.album_id
AND P.PHOTO_ID IN
(
SELECT tag_photo_id FROM 
(SELECT tag_photo_id, COUNT(*) 
FROM project2.public_Tags 
GROUP BY tag_photo_id ORDER BY COUNT(*) DESC, tag_photo_id)
WHERE ROWNUM <= 5
)
ORDER BY P.PHOTO_ID, U.USER_ID;


"SELECT P.PHOTO_ID, A.ALBUM_ID, A.ALBUM_NAME, P.PHOTO_LINK, U.USER_ID, U.FIRST_NAME, U.LAST_NAME " +
"FROM " + PhotosTable + " P, " + TagsTable + " T, " + UsersTable + " U, " + AlbumsTable + " A " +
"WHERE P.PHOTO_ID = T.tag_photo_id AND T.Tag_subject_id = U.USER_ID AND A.album_id = P.album_id " + 
"AND P.PHOTO_ID IN " +
"( " +
"SELECT tag_photo_id FROM " + 
"(SELECT tag_photo_id, COUNT(*) " + 
"FROM " + TagsTable + 
" GROUP  BY tag_photo_id ORDER BY COUNT(*) DESC, tag_photo_id) " +
"WHERE ROWNUM <= 5" + ") " +
"ORDER BY P.PHOTO_ID, U.USER_ID"