package com.zyl11123ok.deRun;

import com.zyl11123ok.deRun.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class Welcome extends ActionBarActivity {

	private Handler mHandler = new Handler();
	ImageView imageview;
	TextView textview;
	int alpha = 150;
	int b = 0;
	@SuppressLint({ "NewApi", "HandlerLeak" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		imageview = (ImageView) this.findViewById(R.id.imageView1);
		imageview.setImageAlpha(alpha);
		
		new Thread(new Runnable() {
			public void run() {
				
				while (b < 2) {
					try {
						if (b == 0) {
							Thread.sleep(1000);
							b = 1;
						} else {
							Thread.sleep(50);
						}

						updateApp();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		}).start();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageview.setAlpha(alpha);
				imageview.invalidate();
			}
		};
	}

	public void updateApp() {
		alpha -= 5;

		if (alpha <= 0) {
			b = 2;
			//渐变效果结束时启动主菜单的Activity
			Intent in = new Intent(this,  com.zyl11123ok.deRun.View.class);
			startActivity(in);
			this.finish();
		}

		mHandler.sendMessage(mHandler.obtainMessage());

	}
}
