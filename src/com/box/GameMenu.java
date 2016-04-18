package com.box;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameMenu extends Activity implements OnClickListener {
	//声明按钮
	private Button btnPlayGame, btnNewGame, btnGameHelp, btnExitGame;
	public boolean IntentFlag=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = this.getIntent();
      	//获取数据
		IntentFlag = intent.getBooleanExtra("Main", false);
		//实例按钮
		btnPlayGame = (Button) findViewById(R.id.playgame);
		btnNewGame = (Button) findViewById(R.id.newgame);
		btnGameHelp = (Button) findViewById(R.id.gamehelp);
		btnExitGame = (Button) findViewById(R.id.exitgame);
		//给每个按钮添加监听
		btnPlayGame.setOnClickListener(this);
		btnNewGame.setOnClickListener(this);
		btnGameHelp.setOnClickListener(this);
		btnExitGame.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == btnPlayGame) {
			//创建一个意图，并且设置需打开的Activity
			Intent intent = new Intent(GameMenu.this, GameMain.class);
			//启动另外一个Activity
			this.startActivity(intent);
		} else if (v == btnNewGame) {
			Intent intent = new Intent(GameMenu.this, GameMain.class);
			IntentFlag=true;
			intent.putExtra("Main",IntentFlag);
			this.startActivity(intent);
			//this.finish();//退出Activity
		}else if (v == btnGameHelp) {
			Intent intent = new Intent(GameMenu.this, Help.class);
			this.startActivity(intent);
		}else if (v == btnExitGame) {
			//System.exit(0);//退出程序
			int pid = android.os.Process.myPid();	//获取当前应用程序的PID
			android.os.Process.killProcess(pid);	//杀死当前进程
		}
	}}
