package com.databases.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.databases.example.Schedule.PlanRecord;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PlanReceiver extends BroadcastReceiver{

	final String dbFinance = "dbFinance";
	final String tblPlanTrans = "tblPlanTrans";
	SQLiteDatabase myDB = null;
	private SliderMenu menu;
	
	//Date Format to use for date (03-26-2013)
	final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");		

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d("PlanReceiver", "Notified of boot");
			Toast.makeText(context, "PlanReceiver received from boot", Toast.LENGTH_LONG).show();
			reschedulePlans(context);
		}
		else{
			String name = bundle.getString("plan_name");
			Toast.makeText(context, "PlanReceiver received " + name, Toast.LENGTH_LONG).show();

			//reschedulePlans(context);

			try {
				notify(context, bundle);
			} catch (Exception e) {
				Toast.makeText(context, "There was an error somewhere \n e = " + e, Toast.LENGTH_SHORT).show();
				Log.e("onReceive", "ERROR: " + e);
				e.printStackTrace();
			}

		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void notify(Context context, Bundle bundle) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence from = "Welsh Finances";
		CharSequence message = "Sample Notification text here...";

		String plan_id = bundle.getString("plan_id");
		String plan_acct_id = bundle.getString("plan_acct_id");
		String plan_name = bundle.getString("plan_name");
		String plan_value = bundle.getString("plan_value");
		String plan_type = bundle.getString("plan_type");
		String plan_category = bundle.getString("plan_category");
		String plan_memo = bundle.getString("plan_memo");
		String plan_offset = bundle.getString("plan_offset");
		String plan_rate = bundle.getString("plan_rate");
		String plan_cleared = bundle.getString("plan_cleared");

		//Intent fired when notification is clicked on
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context,Checkbook.class), 0);

		Calendar cal = Calendar.getInstance();

		RemoteViews customNotifView = new RemoteViews("com.databases.example", 
				R.layout.notification_big);
		customNotifView.setTextViewText(R.id.TextNotification, plan_id + " " + plan_name + " " + plan_value + " " + plan_offset + " " + plan_rate + "\n Fired on " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) +":" + cal.get(Calendar.SECOND));

		Notification notification = new NotificationCompat.Builder(context).
				setContentTitle(from+ ": " + plan_name)
				.setContentText(plan_id + " " + plan_name + " " + plan_value + " " + plan_offset + " " + plan_rate)
				.setSmallIcon(R.drawable.calculator)
				.setContentIntent(contentIntent)
				.build();

		if (Build.VERSION.SDK_INT > 15){
			notification.bigContentView = customNotifView;
		}

		//notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(Integer.parseInt(plan_id), notification);

	}

	//Method that remakes the planned transaction
	public void reschedulePlans(Context context){

		// Cursor is used to navigate the query results
		myDB = context.openOrCreateDatabase(dbFinance, context.MODE_PRIVATE, null);

		Cursor cursorPlans = myDB.query(tblPlanTrans, new String[] { "PlanID as _id", "ToAcctID", "PlanName", "PlanValue", "PlanType", "PlanCategory", "PlanMemo", "PlanOffset", "PlanRate", "PlanCleared"}, null,
				null, null, null, null);

		//startManagingCursor(cursorPlans);

		int IDColumn = cursorPlans.getColumnIndex("PlanID");
		int ToIDColumn = cursorPlans.getColumnIndex("ToAcctID");
		int NameColumn = cursorPlans.getColumnIndex("PlanName");
		int ValueColumn = cursorPlans.getColumnIndex("PlanValue");
		int TypeColumn = cursorPlans.getColumnIndex("PlanType");
		int CategoryColumn = cursorPlans.getColumnIndex("PlanCategory");
		int MemoColumn = cursorPlans.getColumnIndex("PlanMemo");
		int OffsetColumn = cursorPlans.getColumnIndex("PlanOffset");
		int RateColumn = cursorPlans.getColumnIndex("PlanRate");
		int ClearedColumn = cursorPlans.getColumnIndex("PlanCleared");

		cursorPlans.moveToFirst();
		if (cursorPlans != null) {
			if (cursorPlans.isFirst()) {
				do {

					String id = cursorPlans.getString(0);
					String to_id = cursorPlans.getString(ToIDColumn);
					String name = cursorPlans.getString(NameColumn);
					String value = cursorPlans.getString(ValueColumn);
					String type = cursorPlans.getString(TypeColumn);
					String category = cursorPlans.getString(CategoryColumn);
					String memo = cursorPlans.getString(MemoColumn);
					String offset = cursorPlans.getString(OffsetColumn);
					String rate = cursorPlans.getString(RateColumn);
					String cleared = cursorPlans.getString(ClearedColumn);

					/****RESET ALARMS HERE****/
					Log.e("reschedulePlans", "rescheduling " + id + to_id + name + value + type + category + memo + offset + rate + cleared);
					PlanRecord record = new PlanRecord(id,to_id,name,value,type,category,memo,offset,rate,cleared);
					schedule(record,context);
					
					
				} while (cursorPlans.moveToNext());
			}

			else {
				//No Results Found
				Log.d("Schedule", "No Plans to reschedule");
			}
		} 

		//Close Database if Open
		if (myDB != null){
			myDB.close();
		}

	}

	//Re-Hash of the schedule method Schedule.java
	private void schedule(PlanRecord plan, Context context) {
		PlanRecord record = plan;
		Date d = null;

		try {
			d = dateFormat.parse(record.offset);
		}catch (java.text.ParseException e) {
			Log.e("schedule", "Couldn't schedule " + record.name + "\n e:"+e);
			e.printStackTrace();
		}
		
		Log.e("Schedule", "d.year=" + (d.getYear()+1900) + " d.date=" + d.getDate() + " d.month=" + d.getMonth());
		
		Calendar firstRun = new GregorianCalendar(d.getYear()+1900,d.getMonth(),d.getDate());
		Log.e("Schedule", "FirstRun:" + firstRun);
		
		Intent intent = new Intent(context, PlanReceiver.class);
		intent.putExtra("plan_id", record.id);
		intent.putExtra("plan_acct_id",record.acctId);
		intent.putExtra("plan_name",record.name);
		intent.putExtra("plan_value",record.value);
		intent.putExtra("plan_type",record.type);
		intent.putExtra("plan_category",record.category);
		intent.putExtra("plan_memo",record.memo);
		intent.putExtra("plan_offset",record.offset);
		intent.putExtra("plan_rate",record.rate);
		intent.putExtra("plan_cleared",record.cleared);

		//Parse Rate (token 0 is amount, token 1 is type)
		String phrase = record.rate;
		String delims = "[ ]+";
		String[] tokens = phrase.split(delims);

		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(context, Integer.parseInt(record.id), intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

		if(tokens[1].contains("Days")){
			Log.e("schedule", "Days");

			//am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), (Integer.parseInt(tokens[0])*AlarmManager.INTERVAL_DAY), sender);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstRun.getTimeInMillis(), (Integer.parseInt(tokens[0])*AlarmManager.INTERVAL_DAY), sender);
		}
		else if(tokens[1].contains("Weeks")){
			Log.e("schedule", "Weeks");
			//am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), (Integer.parseInt(tokens[0])*AlarmManager.INTERVAL_DAY)*7, sender);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstRun.getTimeInMillis(), (Integer.parseInt(tokens[0])*AlarmManager.INTERVAL_DAY)*7, sender);
		}
		else if(tokens[1].contains("Months")){
			Log.e("schedule", "Months");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(cal.getTimeInMillis());
			cal.add(Calendar.MONTH, Integer.parseInt(tokens[0]));
			
			//am.setRepeating(AlarmManager.RTC_WAKEUP, firstRun.getTimeInMillis(), cal.getTimeInMillis(), sender);
			//am.setRepeating(AlarmManager.RTC_WAKEUP, firstRun.getTimeInMillis(), (Integer.parseInt(tokens[0])*AlarmManager.INTERVAL_FIFTEEN_MINUTES), sender);
			am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000*30, sender);
		}
		else{
			Log.e("Schedule", "Could not set alarm; Something wrong with the rate");
		}

		//am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		//am.setRepeating(AlarmManager.RTC_WAKEUP, firstRun.getTimeInMillis(), 1000*20, sender);
		//am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000*6, sender);
	}	
	
	public class PlanRecord {
		protected String id;
		protected String acctId;
		protected String name;
		protected String value;
		protected String type;
		protected String category;
		protected String memo;
		protected String offset;
		protected String rate;
		protected String cleared;

		public PlanRecord(String id, String acctId, String name, String value, String type, String category, String memo, String offset, String rate, String cleared) {
			this.id = id;
			this.acctId = acctId;
			this.name = name;
			this.value = value;
			this.type = type;
			this.category = category;
			this.memo = memo;
			this.offset = offset;
			this.rate = rate;
			this.cleared = cleared;
		}
	}

}//end of PlanReceiver