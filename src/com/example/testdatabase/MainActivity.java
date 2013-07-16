package com.example.testdatabase;

 
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	public static final String KEY_ID = "_id";
	public static final String KEY_GOLD_HOARD_NAME_COLUMN = "GOLD_HOARD_NAME_CLOUMN";
	public static final String KEY_GOLD_HOARD_ACCESSIBLE_COLUMN = "GOLD_HOARD_ACCESSIBLE_COLUMN";
	public static final String KEY_GOLD_HOARDED_COLUMN = "GOLD_HOARDED_CLOUMN";
	public Context context;
	public HoardDBOpenHelper hoardDBOpenHelper;
	public Button insert,query,update,delete,updata;
	public TextView tv;
	public EditText edit1,edit2,edit3,edit_id;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			 if(msg.what==1){
				 String sb=(String)msg.obj;
				 tv.setText(sb);
			 }
			 handler.removeCallbacks(r);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		insert=(Button)findViewById(R.id.insert);
		insert.setOnClickListener(this);
		query=(Button)findViewById(R.id.query);
		query.setOnClickListener(this);
		updata=(Button)findViewById(R.id.updata);
		updata.setOnClickListener(this);
		
		update=(Button)findViewById(R.id.update);
		update.setOnClickListener(this);
		delete=(Button)findViewById(R.id.delete);
		delete.setOnClickListener(this);
		tv=(TextView)findViewById(R.id.tv);
		edit1=(EditText)findViewById(R.id.name);
		edit2=(EditText)findViewById(R.id.clum);
		edit3=(EditText)findViewById(R.id.bloo);
		edit_id=(EditText)findViewById(R.id.edit_id);
		
		context = this;
		// 可以创建 一个数据库
		// SQLiteDatabase db=context.openOrCreateDatabase("demo.db",
		// context.MODE_PRIVATE, null);

		hoardDBOpenHelper = new HoardDBOpenHelper(context,
				HoardDBOpenHelper.DATABASE_NAME, null,
				HoardDBOpenHelper.DATABASE_VERSION);
		hoardDBOpenHelper.close();
		handler.post(r);
	}	
	
	
	Runnable r=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Cursor cursor=getAccessibleHoard();
			//找出列的索引 在列出的列不存在的时候返回 -1
			//int index=cursor.getColumnIndex(KEY_GOLD_HOARD_NAME_COLUMN);
			 //找出列的索引在列出的列不存在时抛出一个异常\
			int id_index=cursor.getColumnIndexOrThrow(KEY_ID);
		    int name_index= cursor.getColumnIndexOrThrow(KEY_GOLD_HOARD_NAME_COLUMN);
		    int clum_index= cursor.getColumnIndexOrThrow(KEY_GOLD_HOARD_ACCESSIBLE_COLUMN);
		    int bloo_index= cursor.getColumnIndexOrThrow(KEY_GOLD_HOARDED_COLUMN);
		    StringBuilder sb=new StringBuilder();
		   
			while(cursor.moveToNext()){
				  
					String name=cursor.getString(name_index);
					float clum=cursor.getFloat(clum_index);
					int bloo=cursor.getInt(bloo_index);
					int id=cursor.getInt(id_index);
					sb.append(id+"  "+name+"   "+clum+"     "+bloo+"\n");
					//System.out.println("ppppp"+ "     "+id+"  "+name+"   "+clum+"     "+bloo);
			} 
			 cursor.close();
			 Message msg = handler.obtainMessage();
			 msg.obj = sb.toString();
			 msg.what = 1;
			 handler.sendMessage(msg);
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==insert){
			 new MyThread(0).run();
			 handler.post(r);
		}else if(v==query){
			 handler.post(r);
		}else if(v==update){
			 new MyThread(1).run();
			 handler.post(r);
		}else if(v==delete){
			 new MyThread(2).run();
			 handler.post(r);
		}else if(v==updata){
			  hoardDBOpenHelper.onUpgrade(hoardDBOpenHelper.getWritableDatabase(), 1, 2);	
			  handler.post(r);
		}
	}
	
	
	public int getAllData(){
		return 0;
	}


	public class MyThread extends Thread {
		 int flag=0;
		
		public MyThread(int f) {
			// TODO Auto-generated constructor stub
			flag=f;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			if (TextUtils.isEmpty(edit1.getText())||TextUtils.isEmpty(edit2.getText())||TextUtils.isEmpty(edit3.getText())) {
			
				Toast.makeText(MainActivity.this, "插入信息不能为空", Toast.LENGTH_LONG).show();
			}else {
				String name=String.valueOf(edit1.getText().toString());
				float clum=Float.parseFloat(edit2.getText().toString());
				int bloo=Integer.parseInt(edit3.getText().toString());
				
				switch (flag) {
				case 0:
					
					addNewHoard(name,clum,bloo);
					break;
				case 1:
					
					if (TextUtils.isEmpty(edit_id.getText())){
						Toast.makeText(MainActivity.this, "插入信息不能为空", Toast.LENGTH_LONG).show();
	
					}else {
						int id = Integer.parseInt(edit_id.getText().toString());
						updateHoardValue(id,name,clum,bloo);
					}
				
					break;
				case 2:
					if (TextUtils.isEmpty(edit_id.getText())){
						Toast.makeText(MainActivity.this, "插入信息不能为空", Toast.LENGTH_LONG).show();
	
					}else {
						int id = Integer.parseInt(edit_id.getText().toString());
						deleteEmptyHoards(id);
						
					}
					break;
				 
				default:
					break;
				}
			}
			
		}
	}
	/**
	 * 
	 * Listing 8-3: Querying a database
	 * @return
	 */
	private Cursor getAccessibleHoard() {
			 
		// Specify the result column projection. Return the minimum set
		// of columns required to satisfy your requirements.
		String[] result_columns = new String[] { KEY_ID,KEY_GOLD_HOARD_NAME_COLUMN,
				KEY_GOLD_HOARD_ACCESSIBLE_COLUMN, KEY_GOLD_HOARDED_COLUMN };

		// Specify the where clause that will limit our results.
		//String where = KEY_GOLD_HOARD_ACCESSIBLE_COLUMN + "=" + 1;
		String where=null;
		// Replace these with valid SQL statements as necessary.
		String whereArgs[] = null;
		String groupBy = null;
		String having = null;
		String order = null;

		SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(HoardDBOpenHelper.DATABASE_TABLE,
				result_columns, where, whereArgs, groupBy, having, order);
		return cursor;
	}
	
	/** 
	 * 
	 * Listing 8-5: Inserting new rows into a database
	 * @param hoardName 插入的值
	 * @param hoardValue
	 * @param hoardAccessible
	 */
	public void addNewHoard(String hoardName, float hoardValue,
			int hoardAccessible) {
		 
		// Create a new row of values to insert.
		ContentValues newValues = new ContentValues();

		// Assign values for each row.
		newValues.put(KEY_GOLD_HOARD_NAME_COLUMN, hoardName);
		newValues.put(KEY_GOLD_HOARDED_COLUMN, hoardValue);
		newValues.put(KEY_GOLD_HOARD_ACCESSIBLE_COLUMN, hoardAccessible);
		// [ ... Repeat for each column / value pair ... ]

		// Insert the row into your table
		SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
		db.insert(HoardDBOpenHelper.DATABASE_TABLE, null, newValues);
		Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
		db.close();
	}
	
	/**
	 * Listing 8-6: Updating a database row
	 * @param hoardId 要更新的行号
	 * @param newHoardValue 要更新的值
	 */
	public void updateHoardValue(int hoardId, String name,float newHoardValue,int cloo) {
	 
		// Create the updated row Content Values.
		ContentValues updatedValues = new ContentValues();

		// Assign values for each row.
		updatedValues.put(KEY_GOLD_HOARD_NAME_COLUMN, name);
		updatedValues.put(KEY_GOLD_HOARDED_COLUMN, newHoardValue);
		updatedValues.put(KEY_GOLD_HOARD_ACCESSIBLE_COLUMN, cloo);
		// [ ... Repeat for each column to update ... ]

		// Specify a where clause the defines which rows should be
		// updated. Specify where arguments as necessary.
		String where = KEY_ID + "=" + hoardId;
		String whereArgs[] = null;

		// Update the row with the specified index with the new values.
		SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
		db.update(HoardDBOpenHelper.DATABASE_TABLE, updatedValues, where,whereArgs);
		db.close();
	}
	
	/**
	 * Listing 8-7: Deleting a database row
	 */
	public void deleteEmptyHoards(int num) {
		 
		// Specify a where clause that determines which row(s) to delete.
		// Specify where arguments as necessary.
		String where = KEY_ID + "=" + num;
		String whereArgs[] = null;

		// Delete the rows that match the where clause.
		SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
		db.delete(HoardDBOpenHelper.DATABASE_TABLE, where, whereArgs);
		db.close();
		System.out.println("delete");
	}
	

	/**
	 * 数据类 生成数据库和管理数据库的升级
	 * */
	public static class HoardDBOpenHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "myDatabase.db";
		private static final String DATABASE_TABLE = "GoldHoards";
		private static final int DATABASE_VERSION = 1;
		 
		// 创建数据库的语句

		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, "
				+ KEY_GOLD_HOARD_NAME_COLUMN + " text not null , "
				+ KEY_GOLD_HOARDED_COLUMN + "  float, "
				+ KEY_GOLD_HOARD_ACCESSIBLE_COLUMN + " integer); ";

		public HoardDBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			System.out.println("onCreate");
			db.execSQL(DATABASE_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			// 记录版本升级
			Log.w("TaskDBAdapter", "	Upgrading from version " + oldVersion
					+ " to " + newVersion + ", which will destory all old data");
			// 将数据库升级到现有的版本 通常比较oldversion和newversion的值
			// 可以处理多个旧版本的情况
			// 最简单的办法是删除旧表 创建新表
			db.execSQL("DROP TABLE  " + DATABASE_TABLE);
			onCreate(db);
		}

	}



}
