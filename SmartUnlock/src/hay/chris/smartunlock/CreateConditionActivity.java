package hay.chris.smartunlock;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;    
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateConditionActivity extends Activity {

	private boolean timerExpanded;
	private boolean bluetoothExpanded;
	private boolean wifiExpanded;
	private boolean locationExpanded;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_condition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_condition, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/*
	 * Handle xml onClick requests
	 */
	public void expandTimer(View view) {
		if (timerExpanded) return;
		TextView description = (TextView) findViewById(R.id.timer_constructor_description);
		description.setText("Remove lock ");
		RadioGroup radios = (RadioGroup) findViewById(R.id.timer_body);
		radios.setVisibility(View.VISIBLE);
	}
	private View[] getTimerViews(){
		View[] views = {findViewById(R.id.text_for),
		                findViewById(R.id.timer_unlock_body),
		                findViewById(R.id.timer_editor),
		                findViewById(R.id.timer_spinner)};
		return views;
	}
	private void dismissAllTimerViews(){
		View[] views = getTimerViews();    
		for (View v: views) {
			v.setVisibility(View.GONE);
		}
	}
	public void onRadioTimerAfterUnlock(View view){
		dismissAllTimerViews();
		TextView tv = (TextView) findViewById(R.id.text_for);
		EditText et = (EditText) findViewById(R.id.timer_editor);
		RelativeLayout createOrCancel = (RelativeLayout) findViewById(R.id.timer_unlock_body);
		tv.setVisibility(View.VISIBLE);
		et.setVisibility(View.VISIBLE);
		createOrCancel.setVisibility(View.VISIBLE);
		Spinner spinner = (Spinner) findViewById(R.id.timer_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.timer_spinner, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setSelection(1);
		spinner.setVisibility(View.VISIBLE);
	}
	public void onRadioTimerBetweenTimes(View view) {
		dismissAllTimerViews();
		//TODO add views and save/create
	}
	public void onRadioTimerAlways(View view) {
		dismissAllTimerViews();
		RelativeLayout createOrCancel = (RelativeLayout) findViewById(R.id.timer_unlock_body);
		createOrCancel.setVisibility(View.VISIBLE);
		//TODO add saving and creating condition
	}
	public void onRadioTimerNever(View view) {
		dismissAllTimerViews();
		RelativeLayout createOrCancel = (RelativeLayout) findViewById(R.id.timer_unlock_body);
		createOrCancel.setVisibility(View.VISIBLE);
		//TODO add saving data and creating condition
	}
	public void onCreateTimer(View view) {
		int radioId = ((RadioGroup)findViewById(R.id.timer_body)).getCheckedRadioButtonId();
		SharedPreferences prefs = this.getSharedPreferences("hay.chris.smartunlock.condition_storage", 0);
	    int currentConditionSize = prefs.getInt("total_condition_storage_size", 0);
	    int currentSetSize = prefs.getInt("total_set_storage_size", 0);
		String name = "condition" + currentConditionSize;
		String setName = "set" + currentSetSize;

		MainActivity.TimerCondition timer = null;
		switch (radioId) {
			case R.id.radio_after_unlock:
				timer = new MainActivity.TimerCondition(name, setName, 0);
				int time = Integer.parseInt(((EditText) findViewById(R.id.timer_editor)).getText().toString());
				int timeType = ((Spinner) findViewById(R.id.timer_spinner)).getSelectedItemPosition();
				timer.setTime(time, timeType);
				break; 
			case R.id.radio_between_times:
				break;
			case R.id.radio_always:
				timer = new MainActivity.TimerCondition(name, setName, 2);
				break;
			case R.id.radio_never:
				timer = new MainActivity.TimerCondition(name, setName, 3);
				break;
		}
		timer.isActive = true;
		Log.e("test", "attempt save");  
		SharedPreferences.Editor edit = prefs.edit();
		Set<String> activeSets = new HashSet<String>(prefs.getStringSet("all_sets_active", new HashSet<String>()));
		Set<String> activeConditionsForSet = new HashSet<String>();
		Set<String> activeTimers = new HashSet<String>(prefs.getStringSet("all_timers_active", new HashSet<String>()));
		activeSets.add(setName);
		activeConditionsForSet.add(name);
		activeTimers.add(name);
		edit.putString(name, timer.toString());
		edit.putStringSet("all_sets_active", activeSets);
		edit.putStringSet(setName + "_active", activeConditionsForSet);
		edit.putStringSet("all_timers_active", activeTimers);
		edit.putInt("total_condition_storage_size", currentConditionSize + 1);
		edit.putInt("total_set_storage_size", currentSetSize + 1);
		edit.commit();
		onCancelTimer(view);
	}
	public void onCancelTimer(View view){
		dismissAllTimerViews();
		TextView description = (TextView) findViewById(R.id.timer_constructor_description);
		description.setText("Remove lock for the duration of the timer.");
		RadioGroup radios = (RadioGroup) findViewById(R.id.timer_body);
		radios.clearCheck();
		radios.setVisibility(View.GONE);
		RelativeLayout createOrCancel = (RelativeLayout) findViewById(R.id.timer_unlock_body);
		createOrCancel.setVisibility(View.GONE);
	}
	
}
