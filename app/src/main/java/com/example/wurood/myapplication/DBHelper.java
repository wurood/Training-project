package com.example.wurood.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_ROW_ID = "_id";


    private static final String TABLE_NAME = "manual";
    private static final String DATABASE_NAME = "contactDB";
    private SQLiteDatabase db;
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY, " + KEY_NAME +
            " TEXT NOT NULL, " + KEY_MOBILE + " TEXT NOT NULL, " + KEY_EMAIL + " TEXT, "
            + KEY_PHOTO + " BLOB" + ")";

    ArrayList<String> contacts;
    private static int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void addContacts(String name, String mobile, String email, byte[] photo) {
        db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_MOBILE, email);
        initialValues.put(KEY_EMAIL, mobile);
        initialValues.put(KEY_PHOTO, photo);
        db.insert(TABLE_NAME, null, initialValues);
        db.close();
    }

    public int updateContact(String name, String updateName, String updateEmail, String updateNumber, byte[] updatePhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, updateName);
        values.put(KEY_MOBILE, updateNumber);
        values.put(KEY_EMAIL, updateEmail);
        values.put(KEY_PHOTO, updatePhoto);
        int i = db.update(TABLE_NAME,
                values, // column/value
                "name = ?",
                new String[]{name});
        db.close();
        return i;
    }

    public void deleteContact(String Name) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "name = ?", new String[]{String.valueOf(Name)});
        db.close();
    }


    public ArrayList<Contact> allContacts() {


        ArrayList<Contact> contacts = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Contact contact;

        if (cursor.moveToFirst()) {

            do {
                Bitmap bitPhoto;
                byte[] b = cursor.getBlob(4);
                if (b != null) {
                    bitPhoto = getImage(b);
                } else bitPhoto = null;
                contact = new Contact(cursor.getString(1), cursor.getString(2), bitPhoto, Long.parseLong(cursor.getString(0)), R.mipmap.ic_home, cursor.getString(3));
                contacts.add(contact);

            } while (cursor.moveToNext());

        }
        db.close();
        return contacts;

    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public boolean CheckEvent(String name, String mobile, String email, byte[] photo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_NAME, KEY_MOBILE, KEY_EMAIL, KEY_PHOTO},
                KEY_NAME + " = ? and " + KEY_MOBILE + " = ? and " + KEY_EMAIL + " = ? and " + KEY_PHOTO + " = ?",
                new String[]{name, mobile, email, String.valueOf(photo)},
                null, null, null, null);

        if (cursor.moveToFirst())

            return true;
        else
            return false;

    }
}
