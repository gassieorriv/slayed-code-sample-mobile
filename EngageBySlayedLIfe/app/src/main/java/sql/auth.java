package sql;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import models.Authorization;

public class auth {

    private static final String DatabaseName = "Engage";
     public static String dropLoginDB = "DROP TABLE IF EXISTS Authorization";
    public static String createAuthorization = "CREATE TABLE IF NOT EXISTS Authorization (apiToken BLOB, accessToken BLOB, refreshToken BLOB, uid TEXT, authType INT, engageUserId INT, authCode BLOB)";
    public static String selectAuthorization = "SELECT * FROM Authorization";
    public static String deleteAuthorization = "DELETE FROM Authorization";
    public static void Insert(Authorization Authorization, ContextWrapper context) {
        SQLiteDatabase db = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE,null);
        String InsertAuthorization = "INSERT INTO Authorization (apiToken, accessToken, refreshToken, uid, authType, engageUserId, authCode) VALUES " +
                "('" + Authorization.apiToken + "', '"
                     + Authorization.accessToken + "', '"
                     + Authorization.refreshToken + "','"
                     + Authorization.uid + "','"
                     + Authorization.authType + "','"
                     + Authorization.engageUserId + "','"
                     + Authorization.authCode +"')";

        db.execSQL(deleteAuthorization);
        db.execSQL(InsertAuthorization);
        db.close();
    }
    public static void Create(ContextWrapper context){

        SQLiteDatabase db = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);
        db.execSQL(createAuthorization);
        db.close();
    }

    public static Authorization Get(ContextWrapper context) {
        Authorization authorization = new Authorization();
        SQLiteDatabase db = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery(selectAuthorization, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                authorization.apiToken = cursor.getString(cursor.getColumnIndex("apiToken"));
                authorization.accessToken = cursor.getString(cursor.getColumnIndex("accessToken"));
                authorization.refreshToken = cursor.getString(cursor.getColumnIndex("refreshToken"));
                authorization.uid = cursor.getString(cursor.getColumnIndex("uid"));
                authorization.authType = cursor.getInt(cursor.getColumnIndex("authType"));
                authorization.engageUserId = cursor.getInt(cursor.getColumnIndex("engageUserId"));
                authorization.authCode = cursor.getString(cursor.getColumnIndex("authCode"));
            }
        }
        cursor.close();
        db.close();
        return  authorization;
    }

    public static void Delete(ContextWrapper context){
        SQLiteDatabase db = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE,null);
        db.execSQL(deleteAuthorization);
        db.close();
    }
}
