package com.box;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.box.MapFactory;

public class GameMain extends Activity {
    /** Called when the activity is first created. */
	//GameView界面，该界面功能为对本案例中的场景进行渲染
	private GameView view=null;
	public GameMenu menu=null;
	private Button bt_menu;
	private Button bt_menu_run;
	private Button bt_init;
	private Button bt_run;
	private static String TAGC="Camera";
	//指令
	private List<TopCode> TopCodesList=new ArrayList();	
	private TopCode Instruction=null;
	private List<TopCode> tempList=new ArrayList();	
    
    private Timer rTimer=null;
    private static int rcount=0;
    private TimerTask rTimerTask=null;
    private static int rdelay=1000;//1s
    private static int rperiod=1000;//1s
    protected static final int UPDATE_RUNTEXT = 0;
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
    private Handler rHandler=null;//用于依次输出指令
    
    private static final int UPDATE_SCROLLTEXT=0;
    private static final int UPDATE_STEPTEXT=0;
    //对应
    public Map<Integer,int[]> topCodeMap=new HashMap<Integer,int[]>();//{109-->[19,1],110-->[19,2]}
    public Map<Integer,Integer> loopMap=new HashMap<Integer,Integer>();//loop
    //loop
    private int end=179;
    public String strFlag;
    public int screenwidth,screenheight;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strFlag = getIntent().getExtras().getString("Main"); 
        setContentView(R.layout.main);
        //获得mainactivity中的视图
        //Toast.makeText(GameMain.this,strFlag, Toast.LENGTH_SHORT).show();
        view=(GameView)findViewById(R.id.gameView);
        //对应Map
        topCodeMap.put(47, new int[] {19,1});
        topCodeMap.put(55, new int[] {20,1});
        topCodeMap.put(103, new int[] {21,1});
        topCodeMap.put(107, new int[] {22,1});
        topCodeMap.put(59, new int[] {19,2});
        topCodeMap.put(61, new int[] {20,2});
        topCodeMap.put(79, new int[] {21,2});
        topCodeMap.put(87, new int[] {22,2});
        topCodeMap.put(91, new int[] {19,3});
        topCodeMap.put(93, new int[] {20,3});
        topCodeMap.put(115, new int[] {21,3});
        topCodeMap.put(117, new int[] {22,3});
        topCodeMap.put(121, new int[] {19,4});
        topCodeMap.put(143, new int[] {20,4});
        topCodeMap.put(151, new int[] {21,4});
        topCodeMap.put(155, new int[] {22,4});
        loopMap.put(157, 2);
        loopMap.put(167, 3);
        loopMap.put(171, 4);
        loopMap.put(173, 5);
       // topCodeMap.put(155, new int[] {22,4});
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
	    		    Log.i(TAGS,"UPDATE_SCROLLTEXT"+msg);
	    		    break;
	    		default:
	    			break;
	    		}
	    	}
	    }; 
	    rHandler=new Handler(){
        	public void handleMessage(Message msg){
        		Log.i(TAG,"handleMessage");
        		switch (msg.what){
        		case UPDATE_RUNTEXT:
        			getInstructions();
        			rcount++;
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
	    		    Log.i(TAGS,"UPDATE_STEPTEXT"+msg);
	    		    break;
	    		default:
	    			break;
	    		}
	    	}
	    }; 
	    
	    //获得重新开始的按钮
	    bt_init=(Button)findViewById(R.id.bt_menu_init);
	    bt_init.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			//	view.undo();
				view.thisGrade();
			}
        });
	    //动作运行
        bt_menu_run=(Button)findViewById(R.id.bt_menu_run);
        bt_menu_run.setOnClickListener(new OnClickListener(){
			@Override
          	public void onClick(View v){
			     if(TopCodesList==null||TopCodesList.size()<=2){
		      		 stopInstructions();
		      		 stopList();
		      		 Toast.makeText(getApplicationContext(), "编程块不正确哦，检查一下再运行吧", Toast.LENGTH_SHORT).show();
		      	 }
		      	 else if(TopCodesList.get(0).getCode()==31&&TopCodesList.get(TopCodesList.size()-1).getCode()==109){
		      		//TopCodesList.remove(0);
		      		//TopCodesList.remove(TopCodesList.size()-1);
		      		tempList = produceLoopArray(TopCodesList);
		      		Toast.makeText(getApplicationContext(), "开始运行", Toast.LENGTH_SHORT).show();
					//开启timer
				     startList();
		      	 }else{
		      		 //stopInstructions();
		      		 //stopList();
		      		Toast.makeText(getApplicationContext(), "头尾编码块不正确", Toast.LENGTH_SHORT).show();
		      	 }
			}
    	});
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
    private void stopInstructions() {
		// TODO Auto-generated method stub
		TopCodesList=null;
	}
	private void stopList(){
		Log.i(TAG, "stopList");
		if (rTimer != null) {  
            rTimer.cancel();  
            rTimer = null;  
        }  
  
        if (rTimerTask != null) {  
            rTimerTask.cancel();  
            rTimerTask = null;  
        }     
		rcount = 0;
	}
	//************************************
    public int[] getKeyCode(int code){
    	return topCodeMap.get(code);
    }
    
    public void convertKeyCodeToAction(int KeyCode){
    	switch(KeyCode){
    	case 19:
    		view.moveUp();
    		view.StepCount++;
    		startStep();
    		view.repaint();
    		break;
    	case 20:
    		view.moveDown();
    		view.StepCount++;
    		startStep();
    		view.repaint();
    		break;
    	case 21:
    		view.moveLeft();
    		view.StepCount++;
    		startStep();
    		view.repaint();
    		break;
    	case 22:
    		view.moveRight();
    		view.StepCount++;
    		startStep();
    		view.repaint();
    		break;
    	}
    }
    /**
     * @return
     */
    private TopCode getInstructions(){
    	if(tempList==null){
    		Log.i(TAG,"tempList==null");
    	}else if(rcount<tempList.size()){
    		Instruction=tempList.get(rcount);//rcount是在updateTEXT的时候更新的
    		//Toast.makeText(getApplicationContext(), "编程块"+rcount, 200).show();
    		int[] KeyCode=getKeyCode(Instruction.getCode());//长度为2
    		for(int i=0;i<KeyCode[1];i++){
    			convertKeyCodeToAction(KeyCode[0]);
    		}
    		Log.i(TAG,"getInstructions"+String.valueOf(Instruction));
    	//log.e(TAG,"getKeyCode"+String.valueOf(KeyCode));
    	}else{ //当rcount大于tempList的长度时，停止
    		//Instruction=tempList.get(tempList.size()-1);
    		//rcount=0;
    		stopList();
    		
    	}
    	return Instruction;
    }

   public void sendRunMessage(int id){ //调用的是Handler中的sendMessage(Message msg) 
       Log.i(TAG,"sendRunMessage-->");
	   if (rHandler != null) {  
           Message message = Message.obtain(rHandler, id); 
           rHandler.sendMessage(message);   
       }   
   }  
   public void startList() {
	   Log.i(TAG,"startlist");
       if (rTimer == null) {  
           rTimer = new Timer();  
       }   
 
       if (rTimerTask == null) {  
           rTimerTask = new TimerTask() {  
               @Override  
               public void run() {   
            	   Log.i(TAG,"startsendrun()");
                   sendRunMessage(UPDATE_RUNTEXT);
               }   
           };  
       }  
 
       if(rTimer != null && rTimerTask != null )  
           rTimer.schedule(rTimerTask, rdelay, rperiod); 
   } 
    
    //设置一个intent，调用相机
    private void open() {
    	    //拍完照startActivityForResult() 结果返回onActivityResult()函数
			// TODO Auto-generated method stub
			Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent,0);
    }
    //对循环块进行处理
	public List<TopCode> produceLoopArray(List<TopCode> codes){
		if (codes == null || codes.size() <= 2){
			return new ArrayList<TopCode>();
		}
		List<TopCode> rst = new ArrayList<TopCode>(),
					  tmp = null;
		int n = codes.size(),
			times = 0;
		boolean isStart = false;
		
		for (int i=0;i<n;i++){
			if (i == 0 || i == n - 1){
				continue;
			}
			else if (loopMap.containsKey(codes.get(i).getCode())){
				isStart = true;
				times = loopMap.get(codes.get(i).getCode());
				tmp = new ArrayList<TopCode>();
			}
			else if (codes.get(i).getCode() == end){
				// push tmp to rst with multiple times
				if (tmp == null){
					return rst;
				}
				
				while (times > 0){
					rst.addAll(tmp);
					times--;
				}
				tmp = null;
				isStart = false;
			}
			else{
				if (isStart){
					tmp.add(codes.get(i));
				}
				else{
					rst.add(codes.get(i));
				}
			}
		}
		return rst;
	}
	public void checkBlocks(){
		if(TopCodesList==null||TopCodesList.size()<=2){
      		 stopInstructions();
      		 stopList();
      		Toast.makeText(this.getApplicationContext(), "编码块数量过少，只识别了"+(TopCodesList == null ? 0 : TopCodesList.size())+"个编程块", Toast.LENGTH_SHORT).show();
         	}
      	 else if(TopCodesList.get(0).getCode()==31&&TopCodesList.get(TopCodesList.size()-1).getCode()==109){
      		//TopCodesList.remove(0);
      		//TopCodesList.remove(TopCodesList.size()-1);
      		tempList = produceLoopArray(TopCodesList);
      		Toast.makeText(this.getApplicationContext(), "识别"+TopCodesList.size()+"个编程块，点击运行开始", Toast.LENGTH_SHORT).show();
         	 }else{
      		 //stopInstructions();
      		 //stopList();
      		Toast.makeText(this.getApplicationContext(), "头尾编码块不正确", Toast.LENGTH_SHORT).show();
      	 }
       }
    //在新的activity中获得图片数据
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { 
        	//将获得的图像存在Bitmap的bp中
       	 Bitmap bp=(Bitmap)data.getExtras().get("data");
       	 Scanner scanner=new Scanner();
       	 TopCodesList=scanner.scan(bp);//返回spots列表
       	 checkBlocks();
       	 /*if(TopCodesList==null||TopCodesList.size()<=2){
       		 stopInstructions();
       		 stopList();
       		 Toast.makeText(this.getApplicationContext(), "编码块数量过少，再检查一下吧", Toast.LENGTH_SHORT).show();
       	 }
       	 else if(TopCodesList.get(0).getCode()==31&&TopCodesList.get(TopCodesList.size()-1).getCode()==109){
       		//TopCodesList.remove(0);
       		//TopCodesList.remove(TopCodesList.size()-1);
       		tempList = produceLoopArray(TopCodesList);
       		Toast.makeText(this.getApplicationContext(), "识别"+TopCodesList.size()+"个编程块，点击运行开始", Toast.LENGTH_SHORT).show();
       	 }else{
       		 stopInstructions();
       		 stopList();
       		Toast.makeText(this.getApplicationContext(), "头尾编码块不正确", Toast.LENGTH_SHORT).show();
       	 } */
        }else if(resultCode == RESULT_CANCELED) { 
    	   // 用户取消了图像捕获
        	Toast.makeText(this.getApplicationContext(), "取消照相", Toast.LENGTH_SHORT).show();
      		 stopInstructions();
       		 stopList();
        }
   	 Log.i(TAGC, "spots-->"+TopCodesList);
 }
    
    
  //获得卷轴计数**************************
    public int getScrollCount(){
    	ScrollCount=view.ScrollCountAll-view.curScrollCount();
    	Log.i(TAGS, "ScrollCount-->"+ScrollCount);
    	return ScrollCount;
    }
    protected void updateScrollText() {
	     ScrollText.setText(String.valueOf(getScrollCount()));
	     Log.i(TAGS,"getscrollcount-->"+ScrollText);
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
	     Log.i(TAGS,"getstepcount--"+StepText);
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
    	menu.add(0,3,0,"返回游戏");
    	menu.add(0,4,0,"返回主菜单");
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
    		//save();
    		break;
    	case 4:
    		//System.exit(0);
    		save();
			stopTimer();
    		Intent intent = new Intent(GameMain.this, GameMenu.class);
			this.startActivity(intent);
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
		Log.e("GameMain","onStop");
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
	 public String getstrFlag(){
	    	return strFlag;
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
	protected void onDestroy(){
		super.onDestroy();
		Log.e("GameMain","onDestroy");
	}
	protected void onPause(){
		super.onPause();
		Log.e("GameMain","onPause");
	}
	protected void onRestart(){
		super.onRestart();
		Log.e("GameMain","onRestart");
	}
	protected void onResume(){
		super.onResume();
		Log.e("GameMain","onResume");
	}
	protected void onStart(){
		super.onStart();
		Log.e("GameMain","onStart");
	}
}
