package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import constant.Constant;
import models.Variables;


public class DataBaseHandler extends SQLiteOpenHelper {
    private static DataBaseHandler dataBaseHandler;

    public DataBaseHandler(Context context) {
        super(context, Constant.DATABASENAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_Menu.toString());
        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_LatLong.toString());
        sqLiteDatabase.execSQL(DBTables.CUSTOMER_ADD_SCHEDULE);
        sqLiteDatabase.execSQL(DBTables.EMPLOYEE);
        sqLiteDatabase.execSQL(DBTables.TEMPTABLE);
        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_DeviceInfo);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_DeviceInfo);
        sqLiteDatabase.execSQL(DBTables.ALTER_TABLE_LatLong);
        sqLiteDatabase.execSQL(DBTables.TEMPTABLE_UPDATE);
        sqLiteDatabase.execSQL(DBTables.CUSTOMER_ADDSCHEDULE_UPDATE);
    }

    public String Insert(String TableName, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long i = sqLiteDatabase.insert(TableName, null, contentValues);

        if (i >= 1) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }

    }

    public String Update(String TableName, ContentValues contentValues, String WhereClause) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long i = sqLiteDatabase.update(TableName, contentValues, WhereClause, null);

        if (i >= 1) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }


    public List<Variables.Menulist> Read_Menu() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from gal_mst_tmenu where menu_parent_gid=0 order by " + Constant.menu_displayorder, null);
        List<Variables.Menulist> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {

                Variables.Menulist variables = new Variables.Menulist();
                variables.Menu_gid = cursor.getInt(cursor.getColumnIndex("menu_gid"));
                variables.Menu_Parent_gid = cursor.getInt(cursor.getColumnIndex("menu_parent_gid"));
                variables.Menu_Name = cursor.getString(cursor.getColumnIndex("menu_name"));
                variables.Menu_Link = cursor.getString(cursor.getColumnIndex("menu_link"));
                variables.Menu_Display_Order = cursor.getInt(cursor.getColumnIndex("menu_displayorder"));
                variables.Menu_Level = cursor.getInt(cursor.getColumnIndex("menu_level"));
                list.add(variables);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public String Table_Truncate(String Table_Name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(Table_Name, null, null);
        return "";
    }

    public static List<String> getSubMenus(int menu_name, Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DataBaseHandler(context);
        }
        ArrayList<String> strings = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = dataBaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select menu_name from gal_mst_tmenu where menu_parent_gid=" + menu_name + "", null);

        if (cursor.moveToFirst()) {
            do {
                strings.add(cursor.getString(cursor.getColumnIndex("menu_name")));
            } while (cursor.moveToNext());
        }
        cursor.close();


        return strings;
    }

    public List<Variables.Location> getLatLong(Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DataBaseHandler(context);
        }

        SQLiteDatabase sqLiteDatabase = dataBaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select latlong_gid,latlong_lat,latlong_long,latlong_locationname," +
                "latlong_date,latlong_emp_gid,entity_gid " +
                "from fet_trn_tlatlong where latlong_issync = 'N';", null);
        List<Variables.Location> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Variables.Location location = new Variables.Location();
                location.latlong_gid = cursor.getInt(cursor.getColumnIndex("latlong_gid"));
                location.latlong_lat = cursor.getDouble(cursor.getColumnIndex("latlong_lat"));
                location.latlong_long = cursor.getDouble(cursor.getColumnIndex("latlong_long"));
                location.latlong_locationname = cursor.getString(cursor.getColumnIndex("latlong_locationname"));
                location.latlong_date = cursor.getString(cursor.getColumnIndex("latlong_date"));
                location.emp_gid = cursor.getInt(cursor.getColumnIndex("latlong_emp_gid"));
                location.entity_gid = cursor.getInt(cursor.getColumnIndex("entity_gid"));
                list.add(location);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;

    }

    public List<Variables.DeviceInfo> getDeviceInfo(Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DataBaseHandler(context);
        }

        SQLiteDatabase sqLiteDatabase = dataBaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select deviceinfo_gid,deviceinfo_data,deviceinfo_date,deviceinfo_issync " +
                "from gal_trn_tdeviceinfo where deviceinfo_issync = 'N'; ", null);
        List<Variables.DeviceInfo> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Variables.DeviceInfo deviceInfo = new Variables.DeviceInfo();
                deviceInfo.DeviceInfo_Gid = cursor.getInt(cursor.getColumnIndex("deviceinfo_gid"));
                deviceInfo.DeviceInfo_Date = cursor.getString(cursor.getColumnIndex("deviceinfo_date"));
                deviceInfo.DeviceInfo_Data = cursor.getString(cursor.getColumnIndex("deviceinfo_data"));
                list.add(deviceInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


}
