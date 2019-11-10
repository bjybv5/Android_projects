package com.example.words_book;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_WORDS = "create table words ("
            + "id integer primary key autoincrement, "
            + "word text, "
            + "translate_word text, "
            + "example text)";

    private Context mCoentent;

    public MyDatabaseHelper(Context context) {
        super(context, "words_book.db", null, 1);
        mCoentent = context;
    }

    public MyDatabaseHelper(MainActivity context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mCoentent = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORDS);
        Toast.makeText(mCoentent, "Create succeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
