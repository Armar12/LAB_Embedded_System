package com.zyl11123ok.deRun;

import java.util.ArrayList;

public class StepNotifier implements StepListener {

	private int mCount = 0;
    Settings mSettings;
    Utils mUtils;
    public StepNotifier(Settings settings, Utils utils) {
    	mUtils = utils;
        mSettings = settings;
        notifyListener();
    }
    public void setUtils(Utils utils) {
        mUtils = utils;
       
    }

    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }
	@Override
	public void onStep() {
		mCount ++;
		 notifyListener();
	}
	public void reloadSettings() {
	        notifyListener();
	}
	@Override
	public void passValue() {
		
	}
	  public interface Listener {
	        public void stepsChanged(int value);
	        public void passValue();
	    }
	    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

	    public void addListener(Listener l) {
	        mListeners.add(l);
	    }
	    public void notifyListener() {
	        for (Listener listener : mListeners) {
	            listener.stepsChanged((int)mCount);
	        }
	    }

}
