package com.zyl11123ok.deRun;

import android.content.SharedPreferences;

public class Settings {
	
	SharedPreferences mSettings;
	public Settings(SharedPreferences settings){
		mSettings=settings;
	}
	  public float getStepLength() {
	        try {
	            return Float.valueOf(mSettings.getString("step_length", "70").trim());
	        }
	        catch (NumberFormatException e) {
	            // TODO: reset value, & notify user somehow
	            return 0f;
	        }
	    }
	 public boolean wakeAggressively() {
	        return mSettings.getString("operation_level", "run_in_background").equals("wake_up");
	    }
	    public boolean keepScreenOn() {
	        return mSettings.getString("operation_level", "run_in_background").equals("keep_screen_on");
	    }
	    
	  public void saveServiceRunningWithTimestamp(boolean running) {
	        SharedPreferences.Editor editor = mSettings.edit();
	        editor.putBoolean("service_running", running);
	        editor.putLong("last_seen", Utils.currentTimeInMillis());
	        editor.commit();
	    }
	    
	    public void saveServiceRunningWithNullTimestamp(boolean running) {
	        SharedPreferences.Editor editor = mSettings.edit();
	        editor.putBoolean("service_running", running);
	        editor.putLong("last_seen", 0);
	        editor.commit();
	    }

	    public void clearServiceRunning() {
	        SharedPreferences.Editor editor = mSettings.edit();
	        editor.putBoolean("service_running", false);
	        editor.putLong("last_seen", 0);
	        editor.commit();
	    }

	    public boolean isServiceRunning() {
	        return mSettings.getBoolean("service_running", false);
	    }
	    
	    public boolean isNewStart() {
	        // activity last paused more than 10 minutes ago
	        return mSettings.getLong("last_seen", 0) < Utils.currentTimeInMillis() - 1000*60*10;
	    }
}
