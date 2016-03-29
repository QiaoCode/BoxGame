package com.box;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import TopCodes.Scanner;
import TopCodes.TopCode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.box.MapFactory;

public class GameMain extends Activity {
    /** Called when the activity is first created. */
	//GameView界面，该界面功能为对本案例中的场景进行渲染
	private GameView view=null;
	private Button bt_menu;
	private Button bt_run;
	private static String TAGC="Camera";
	//加入时间**************************
    private static String TAG="Timer";
    
    private TextView ClockText=null;
    
    private Timer mTimer=null;
    private TimerTask mTimerTask=null;
    
    private Handler mHandler=null;
    
    private static int count=0;
    
    private static int delay=1000;//1s
    private static int period=1000;//1s
    
    private static final int UPDATE_CLOCKTEXT=0;
    //加入卷轴数量和步数
    private static String TAGS="ScrollOrStep";
    private TextView ScrollText=null;
    private TextView StepText=null;
	int ScrollCount=0;
    private Handler aHandler=null;//用于操作计数
    private Handler bHandler=null;//用于卷轴计数
    private static final int UPDATE_SCROLLTEXT=0;
    private static final int UPDATE_STEPTEXT=0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //获得mainactivity中的视图
        view=(GameView)findViewById(R.id.gameView);
        //获得计时器数字，设置Handler
        ClockText=(TextView)findViewById(R.id.clocktext);
        ScrollText=(TextView)findViewById(R.id.scrolltext);
        StepText=(TextView)findViewById(R.id.steptext);
		mHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch (msg.what){
	    		case UPDATE_CLOCKTEXT:
	    		    updateClockText();
	    		    break;
	    		default:
	    			break;
	    		}
	    	}
	    }; 
    	aHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch (msg.what){
	    	    case UPDATE_SCROLLTEXT:
	    		    updateScrollText();
	    		    Log.e(TAGS,"UPDATE_SCROLLTEXT"+msg);
	    		    break;
	    		default:
	    			break;
	    		}
	    	}
	    }; 
	    bHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch (msg.what){
	    		case UPDATE_STEPTEXT:
	    		    updateStepText();
	    		    Log.e(TAGS,"UPDATE_STEPTEXT"+msg);
	    		    break;
	    		default:
	    			break;
	    		}
	    	}
	    }; 
	    //获得菜单按钮
        bt_menu=(Button)findViewById(R.id.bt_menu);
        bt_menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			//	view.undo();
				openOptionsMenu();
			}
        });
        //设置相机监听
        bt_run=(Button)findViewById(R.id.bt_run);
        bt_run.setOnClickListener(new OnClickListener(){
			@Override
          	public void onClick(View v){
	            open();
			}
    	});
    }	
    //设置一个intent，调用相机
    private void open() {
    	    //拍完照startActivityForResult() 结果返回onActivityResult()函数
			// TODO Auto-generated method stub
			Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent,0);
    }
    //在新的activity中获得图片数据
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    //将获得的图像存在Bitmap的bp中
	 Bitmap bp=(Bitmap)data.getExtras().get("data");
 }
  //获得卷轴计数**************************
    public int getScrollCount(){
    	ScrollCount=view.ScrollCountAll-view.curScrollCount();
    	Log.i(TAGS, "ScrollCount-->"+ScrollCount);
    	return ScrollCount;
    }
    protected void updateScrollText() {
	     ScrollText.setText(String.valueOf(getScrollCount()));
	     Log.e(TAGS,"getscrollcount-->"+ScrollText);
	     sendScrollMessage(UPDATE_SCROLLTEXT);      
	}
    public void sendScrollMessage(int id){ //调用的是Handler中的sendMessage(Message msg) 
        if (aHandler != null) {  
            Message message = Message.obtain(aHandler, id); 
            aHandler.sendMessage(message);   
        }  
    }  
    public void startScroll() {
    	sendScrollMessage(UPDATE_SCROLLTEXT);
    }
	//获得操作步数计数**************************
    public int getStepCount(){
    	return view.StepCount;
    }
    protected void updateStepText() {
	     StepText.setText(String.valueOf(getStepCount()));
	     Log.e(TAGS,"getstepcount--"+StepText);
	     sendStepMessage(UPDATE_STEPTEXT);      
	}
    public void sendStepMessage(int id){ //调用的是Handler中的sendMessage(Message msg) 
        if (bHandler != null) {  
            Message message = Message.obtain(bHandler, id); 
            bHandler.sendMessage(message);   
        }  
    }  
    public void startStep() {
    	sendStepMessage(UPDATE_STEPTEXT);
    }
   //设置计时器**************************
    public void sendMessage(int id){ //调用的是Handler中的sendMessage(Message msg) 
        if (mHandler != null) {  
            Message message = Message.obtain(mHandler, id);     
            mHandler.sendMessage(message);   
        }  
    }  
	 protected void updateClockText() {
	     ClockText.setText(String.valueOf(count));     
	}
	public void startTimer() {
		 if (mTimer == null) {  
	            mTimer = new Timer();  
	        }  
	  
	     if (mTimerTask == null) {  
	    	 mTimerTask = new TimerTask() {  
	                @Override  
	                public void run() {  
	                    //Log.i(TAG, "count: "+String.valueOf(count));  
	                    sendMessage(UPDATE_CLOCKTEXT);      
	                    count ++;    
	                }  
	            };  
	        }  
	     if(mTimer != null && mTimerTask != null ) { 
	           mTimer.schedule(mTimerTask, delay, period);
	     }
	     }
	
	public void stopTimer() {
		if (mTimer != null) {  
	            mTimer.cancel();  
	            mTimer = null;  
	        }  
	  
	        if (mTimerTask != null) {  
	            mTimerTask.cancel();  
	            mTimerTask = null;  
	        }     
	        count = 0;  	
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	menu.add(0,0,0,"上一关");
    	//menu.add(Menu.NONE, Menu.First+1, 0, "设置").setIcon(R.drawable.setting);
    	menu.add(0,1,0,"下一关");
    	menu.add(0,2,0,"撤销");
    	menu.add(0,3,0,"返回");
    	menu.add(0,4,0,"退出");
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 0:
    		//上一关
    		view.priorGrade();
    		break;
    	case 1:
    		//下一关
    		view.nextGrade();
    		break;
    	case 2:
    		//undo
    		view.undo();
    		break;
    	case 3:
    		break;
    	case 4:
    		this.finish();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    public byte[][] getMap(int grade)
    {
    	/*String mapString=getMapString(index).trim();
    	String []str=mapString.split(",");
    	int row=Integer.parseInt(str[0]);
    	int column=Integer.parseInt(str[1]);
    	byte map[][]=new byte[row][column];
    	int current=2;
    	for(int i=0;i<row;i++)
    		for(int j=0;j<column;j++)
    		{
    			System.out.println(str[current]);
    			map[i][j]=(byte)Integer.parseInt(str[current++]);
    		}
    	return map;*/
    	return MapFactory.getMap(grade);
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	//退出时保存游戏状态
    	save();
    }
    
    public void save()
    {
    	//退出时只保存关卡
    	//地图，关卡数
    	//Map map=new Map(view.getManX(),view.getManY(),view.getMap(),view.getGrade());
    	byte [][]map=view.getMap();
    	int row=map.length;
    	int column=map[0].length;
    	//使用构造方法进行初始化,这样初始化出的StringBuffer对象是一个空的对象。
    	StringBuffer mapString=new StringBuffer();
    	//mapString最终格式
    	//行优先存储，两两之间逗号隔开
    /*	mapString.append(row);
    	mapString.append(",");
    	mapString.append(column);
    	mapString.append(",");*/
    	for(int i=0;i<row;i++){
    		for(int j=0;j<column;j++)
    		{
    			mapString.append(map[i][j]);
    			mapString.append(",");
    		}
    	}
    	//最后多加了一个逗号，解析时注意
    	/*很多时候我们开发的软件需要向用户提供软件参数设置功能，
    	 * Android平台给我们提供了一个SharedPreferences类，
    	 * 它是一个轻量级的存储类，特别适合用于保存软件配置参数。
    	 * 使用SharedPreferences保存数据，其背后是用xml文件存放数据，
    	 * 文件存放在/data/data/<package name>/shared_prefs目录下
    	 */
    	SharedPreferences pre=getSharedPreferences("map", 0);
    	//获取编辑器
    	SharedPreferences.Editor editor=pre.edit();
    	//用于更改位置、关数等状态
    	//SharedPreferences.Editor.putXX:向SharedPreferences里面存入指定的key对应的数值
    	/*editor.putInt("manX", view.getManX());
    	editor.putInt("manY", view.getManY());*/
    	editor.putInt("grade", view.getGrade());
    	/*meeeeee
    	editor.putInt("StepCount", view.getStepCount());
    	editor.putInt("ScrollCount", getScrollCount());
    	editor.putInt("TimerCount", getTimerCount());*/
    	editor.putInt("row", row);
    	editor.putInt("column", column);
    	editor.putString("mapString", mapString.toString());
    	editor.commit();
    }
	public int getTimerCount()
	{
		return count;
	}
   /* public  String getMapString(int index)
    {
    	String map="";
    	InputStream in=getResources().openRawResource(R.raw.map);
    	InputStreamReader isr=new InputStreamReader(in);
    	BufferedReader br=new BufferedReader(isr);
    	try {
    		
    		int i=0;
    		while(i<index && (map=br.readLine())!=null )
    			i++;
    		if(map==null)
    		{
    			in.reset();
    			map=br.readLine();
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(map);
    	return map;
    }*/
    
    /*public Map getMap(int index)
    {
    	Map map=null;
    	index--;
    	index=index<0?0:index;
    	try {
			InputStream in;
			//in=getResources().getAssets().open("map.map");
			in=getResources().openRawResource(R.raw.map);
			ObjectInputStream ois=new ObjectInputStream(in);
			Vector mapVector=(Vector)(ois.readObject());
			index=index>mapVector.size()?mapVector.size():index;
			map=(Map)(mapVector.get(index));
			ois.close();
			in.close();
			mapVector=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
    }*/
}
