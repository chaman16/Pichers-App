package com.example.pichers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myuri.db";
    public static final String TABLE_NAME = "myuri_data";
    public static final String Col1 = "ID";
    public static final String Col2 = "ITEM1";

    public  Database(Context context){super(context,DATABASE_NAME,null,1);}



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable="CREATE TABLE "+ TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+"ITEM1 TEXT)";
        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String a= "DROP TABLE IF EXISTS " +TABLE_NAME ;
        sqLiteDatabase.execSQL(a);
        onCreate(sqLiteDatabase);

    }
    public boolean addData(Uri item1){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Col2, String.valueOf(item1));
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1){
            return false;
        }else{
            return  true;
        }
    }
    public Cursor getListContent(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return  data;
    }
}
