package com.box;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi") public class Help extends Activity implements OnClickListener {
	//声明按钮
	private Button btnBack;
	private GameView view=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		//实例按钮
		btnBack = (Button) findViewById(R.id.back);
		//给每个按钮添加监听
		btnBack.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == btnBack) {
			//创建一个意图，并且设置需打开的Activity
			//Intent intent = new Intent(Help.this, GameMenu.class);
			//发送数据 
			//intent.putExtra("Main", "我是发送的数据~娃哈哈");
			//启动另外一个Activity
			//this.startActivity(intent);
			super.onBackPressed();
		}
	}
	}

