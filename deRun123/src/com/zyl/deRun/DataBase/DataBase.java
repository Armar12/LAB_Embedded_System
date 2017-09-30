package com.zyl.deRun.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBase {
	private static final String TAG = "DeRun_BilldbHelper";
	private static final String DATABASE_NAME = "deRun.db";
	 SQLiteDatabase db;
	 Context context;
	 
	 public DataBase(Context _context){
		 context=_context;
	     db=context.openOrCreateDatabase(DATABASE_NAME, 0, null); 
	     Log.v(TAG,"db path="+db.getPath());
	 }
	 
	 public void CreatTable_amount(){
		 try{
			 db.execSQL("CREATE TABLE amount("+ "_id INTEGER primary key autoincrement,"
					 +"date TEXT,"+"Distance TEXT"+");");
			 
			 Log.v("TAG","creat Table amount ok");
		     }
		 catch(Exception e){
			 Log.v("TAG","creat Table amount err,table exists.");
		 }
		 
	 }
	 public boolean Amount_save(String date,String distance){
		 String sql=" ";
		 try{
			 sql="insert into amount values(null,'"+date+"','"+distance+"')";
			 db.execSQL(sql);
			 
			 Log.v("TAG","insert Table amount ok");
			 return true;
		 }
		 catch(Exception e){
	        	Log.v("TAG","insert Table amount err="+sql);
	        	return false;
	        }
	 }
	 public Cursor getAmount(String sdate){
		 Log.v("TAG","run get amount cursor date=");
		 String table="amount";
		 String[] columns = new  String[]{"_id","date","Distance"};
		 String  selection="date like '%"+sdate+"%'";
		 String[] selectionArgs =null;
	     String groupBy = null;
	     String having = null ; 
	     String orderBy = "date" ;  
	     
	     return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	 }
	 public void delAmount(int id){
		 db.execSQL("delete from amount where _id="+id);
	 }
}
