package com.box;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.box.Map;
import com.box.MapFactory;

//布局使用RelativeLayout或FrameLayout,然后view宽高均为match_parent ，叠在所有view的下层

public class GameView extends SurfaceView implements SurfaceHolder.Callback,OnGestureListener,OnTouchListener{
	
	private static String TAG="GameCount";
	private SurfaceHolder holder;
	private int grade=0;
	//row,column记载人的行号 列号
	//leftX,leftY 记载左上角图片的位置  避免图片从(0,0)坐标开始
	private int row=7,column=7,leftX=0,leftY=0;
	//记载地图的行列数
	private int mapRow=0,mapColumn=0;
	//width,height 记载屏幕的大小
	private int width=0,height=0;
	//acceptKey判断按键事件
	private boolean acceptKey=true;
	//程序所用到的图片
	private Bitmap pic[]=null;
	private Bitmap game_bg;
	//获得卷轴的记数
	public int ScrollCount=0;
	private boolean ScrollFlag=false;//撤销时用的
	//获得步数的记数
	public int StepCount=0;
	//获得游戏结束的时间，保存恢复时使用
	public int TimerCount=0;
	//定义一些常量，对应地图的元素
	final byte WALL=1,BOX=2,BOXONEND=3,END=4,MANDOWN=5,MANLEFT=6,MANRIGHT=7,
			MANUP=8,GRASS=9,MANDOWNONEND=10,MANLEFTONEND=11,MANRIGHTONEND=12,
			MANUPONEND=13,SCROLL=14,WATER=15,WATEREND=16,TARGET=17,TARGETEND=18,
			TARGETDOWN=19,TARGETLEFT=20,TARGETRIGHT=21,TARGETUP=22;
	//water和waterend暂时用不到
	private Paint paint=null;
	private GameMain gameMain=null;
	private byte[][] map=null;
	//用来存储每个步骤后的地图信息（用来撤销）
	private ArrayList list=new ArrayList();
	private GestureDetector mGestureDetector;
	
	public void getManPosition()
	{
		for(int i=0;i<map.length;i++)
			for(int j=0;j<map[0].length;j++)
				if(map[i][j]==MANDOWN || map[i][j]==MANDOWNONEND || map[i][j]==MANUP || map[i][j]==MANUPONEND || map[i][j]==MANLEFT || map[i][j]==MANLEFTONEND || map[i][j]==MANRIGHT || map[i][j]==MANRIGHTONEND)
				{
					row=i;
					column=j;
					break;
				}
	}
	
	public void undo()
	{
		if(acceptKey)
		{
			//撤销
			if(list.size()>0)
			{
				//若要撤销 必须走过
//				Map priorMap=(Map)list.get(list.size()-1);
				Map priorMap=(Map)list.get(list.size()-1);
				map=priorMap.getMap();
				row=priorMap.getManX();
				column=priorMap.getManY();
				repaint();
				StepCount--;
				list.remove(list.size()-1);
				if(ScrollFlag=true){
					ScrollCount--;
				}
			}
			else
				
				Toast.makeText(this.getContext(), "不能再撤销！", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this.getContext(), "此关已完成，不能撤销！", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void nextGrade()
	{
		//grade++;
		if(grade>=MapFactory.getCount()-1)
			{
			Toast.makeText(this.getContext(), "恭喜你完成所有关卡！", Toast.LENGTH_LONG).show();
			acceptKey=false;
			}
		else
		{
			grade++;
			initMap();
			repaint();
			acceptKey=true;
		}
	}
	
	public void priorGrade()
	{
		grade--;
		acceptKey=true;
		if(grade<0)
			grade=0;
		initMap();
		repaint();
	}
	//创造
	public void initMap()
	{
		map=gameMain.getMap(grade);
		StepCount=0;//计数为0
		ScrollCount=0;
		list.clear();
		Log.e(TAG,"timer开始");
		gameMain.startTimer();
		getMapSizeAndPosition();
		getManPosition();
//		Map currMap=new Map(row, column, map);
//		list.add(currMap);
	}
	
	public void resumeGame()
	{//访问SharedPreferences中的数据
		SharedPreferences pre=this.getContext().getSharedPreferences("map", 0);
		//getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String mapString=pre.getString("mapString", "");
		if(mapString.equals("")){
			initMap();
		}
		else
		{//否则换成用户上一局退出的情况
		isFinished();//先检查是不是游戏已经结束
		Log.e(TAG,"timer开始");
		gameMain.startTimer();
		row=pre.getInt("manX", 0);//先从sharePreferences里面找key 为 “Age” 的数据， 如果有，说明你事先保存过， 那就取“Age”对应的值(事先保存过的值) ，如果没找到key为“Age” 的，被赋予默认值0 
		column=pre.getInt("manY", 0);
		int rowCount=pre.getInt("row", 0);
		int columnCount=pre.getInt("column", 0);
		grade=pre.getInt("grade", 0);
		StepCount=pre.getInt("StepCount",0);
		ScrollCount=pre.getInt("ScrollCount", 0);
		map=new byte[rowCount][columnCount];
		Log.e("mapString", mapString);
		String str[]=mapString.split(",");
		Log.e("str", str.toString());
//		String d = "";
//		for(int i=0; i<str.length; i++) {
//			d += str[i];
//		}
//		char[] data = d.toCharArray();
		int index=0;
		//TODO 添加了i<str.length
		for(int i=0;i<rowCount;i++) {
			
			for(int j=0;j<columnCount;j++)
			{
				String point = str[index] + "";
				Log.e("point==>", point);
				index++;
				map[i][j]=(byte)Integer.parseInt(point);
			}
		}
			
		getMapSizeAndPosition();}
		//getManPosition();不用获得人的位置，因为地图初始化可以直接完成人的位置的回归
	}
//构造方法
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameMain=(GameMain)context;
		getPic();
		//实例holder
		holder=this.getHolder();
		//添加监听
		holder.addCallback(this);
		this.setOnTouchListener(this);
		this.setLongClickable(true);
		WindowManager manager=gameMain.getWindowManager();
		width=manager.getDefaultDisplay().getWidth();
		height=manager.getDefaultDisplay().getHeight();
		this.setFocusable(true);
		//手势
		GestureDetector localGestureDetector = new GestureDetector(this);
	    this.mGestureDetector = localGestureDetector;
		//initMap();
	   
	    //构造方法执行时从优先数据中恢复游戏
	    //关卡切换时调用initMap()
	    resumeGame();
	}	
	private void getMapSizeAndPosition() {
		// TODO Auto-generated method stub
		mapRow=map.length;
		mapColumn=map[0].length;
		leftX=(width-map[0].length*30)/2;
		leftY=(height-map.length*30)/2;
		System.out.println(leftX);
		System.out.println(leftY);
		System.out.println(mapRow);
		System.out.println(mapColumn);
	}

	public void getPic()
	{
		pic=new Bitmap[23];
		game_bg=BitmapFactory.decodeResource(getResources(), R.drawable.game_bg);
		//pic[0]=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		pic[1]=BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
		pic[2]=BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
		pic[3]=BitmapFactory.decodeResource(getResources(), R.drawable.pic3);
		pic[4]=BitmapFactory.decodeResource(getResources(), R.drawable.pic4);
		pic[5]=BitmapFactory.decodeResource(getResources(), R.drawable.pic5);
		pic[6]=BitmapFactory.decodeResource(getResources(), R.drawable.pic6);
		pic[7]=BitmapFactory.decodeResource(getResources(), R.drawable.pic7);
		pic[8]=BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
		pic[9]=BitmapFactory.decodeResource(getResources(), R.drawable.pic9);
		pic[10]=BitmapFactory.decodeResource(getResources(), R.drawable.pic10);
		pic[11]=BitmapFactory.decodeResource(getResources(), R.drawable.pic11);
		pic[12]=BitmapFactory.decodeResource(getResources(), R.drawable.pic12);
		pic[13]=BitmapFactory.decodeResource(getResources(), R.drawable.pic13);
		pic[14]=BitmapFactory.decodeResource(getResources(), R.drawable.pic14);
		pic[15]=BitmapFactory.decodeResource(getResources(), R.drawable.pic15);
		pic[16]=BitmapFactory.decodeResource(getResources(), R.drawable.pic16);
		pic[17]=BitmapFactory.decodeResource(getResources(), R.drawable.pic17);
		pic[18]=BitmapFactory.decodeResource(getResources(), R.drawable.pic18);
		pic[19]=BitmapFactory.decodeResource(getResources(), R.drawable.pic19);
		pic[20]=BitmapFactory.decodeResource(getResources(), R.drawable.pic20);
		pic[21]=BitmapFactory.decodeResource(getResources(), R.drawable.pic21);
		pic[22]=BitmapFactory.decodeResource(getResources(), R.drawable.pic22);
		}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		paint=new Paint();
		repaint();
		//repaint();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//父类默认的onKeyDown方法，如果按下按键了父类就会返回true 所以回调方法系统会关闭当前activity 
		if(!acceptKey)//禁用按键？
			return super.onKeyDown(keyCode, event);
		/*KEYCODE_DPAD_UP=19;
		KEYCODE_DPAD_DOWN=20;
		KEYCODE_DPAD_LEFT=21;
		KEYCODE_DPAD_RIGHT=22;*/
		if(keyCode==19)
		{
			//向上
			moveUp();
			StepCount++;
			gameMain.startStep();
			Log.i(TAG,"StepCount"+StepCount);
		}
		if(keyCode==20)
		{
			//向下
			moveDown();
			StepCount++;
			gameMain.startStep();
			Log.i(TAG,"StepCount"+StepCount);
		}
		if(keyCode==21)
		{
			//向左
			moveLeft();
			StepCount++;
			gameMain.startStep();
			Log.i(TAG,"StepCount"+StepCount);
		}
		if(keyCode==22)
		{
			//向右
			moveRight();
			StepCount++;
			gameMain.startStep();
			Log.i(TAG,"StepCount"+StepCount);
		}
		repaint();
		///////////////////
		if(isFinished())
		{
			//禁用按键
			acceptKey=false;
			//计时器停止计时
			TimerCount=gameMain.getTimerCount();
			gameMain.stopTimer();	
			//提示进入下一关
			Builder builder=new AlertDialog.Builder(gameMain);
			builder.setTitle("恭喜过关!");
			builder.setMessage("继续下一关吗?");
			builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//进入下一关
					acceptKey=true;
					nextGrade();
				}
			});
			builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					gameMain.finish();
				}
			});
			builder.create().show();
		}
		///////////////////
		return super.onKeyDown(keyCode, event);
	}

	public byte grassOrEnd(byte man)
	//人离开后修改人的坐标，默认返回是GRASS，如果人离开前该位置时MANEND，则返回END
	//如果人离开前状态时Target，则返回target
	{
		byte result=GRASS;
		if(man==MANDOWNONEND || man==MANLEFTONEND || man==MANRIGHTONEND || man==MANUPONEND){
			result=END;//END放箱子的终点
			}else if(man==TARGETDOWN||man==TARGETLEFT||man==TARGETRIGHT||man==TARGETUP||man==TARGETEND){
			 result=TARGET;
			}
		
		return result;
	}
	
	private void moveUp()
	{
		//上一位为BOX,BOXONEND,WALL
		//row和column是人的行列号
		Log.i(TAG,"moveup");
		if(map[row-1][column]<4)//1，2，3分别为墙，红黄箱子（不可走的）
		{
			//上一位为 BOX,BOXONEND
			if(map[row-1][column]==BOX || map[row-1][column]==BOXONEND)
			{
				//上上一位为 END,GRASS则向上一步,其他不用处理
				if(map[row-2][column]==END || map[row-2][column]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row-2][column]==END?BOXONEND:BOX;
					byte manTemp=map[row-1][column]==BOX?MANUP:MANUPONEND;
					//箱子变成temp,箱子往前一步
					map[row-2][column]=boxTemp;
					//人变成MANUP,往上走一步
					map[row-1][column]=manTemp;
					//人刚才站的地方变成GRASS或者END
					map[row][column]=grassOrEnd(map[row][column]);
					//人离开后修改人的坐标
					row--;
				}
			}ScrollFlag=false;
		}
		else
		{
			//上一位为 GRASS,END,其他情况不用处理
			if(map[row-1][column]==GRASS || map[row-1][column]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row-1][column]==END?MANUPONEND:MANUP;
				//人变成temp,人往上走一步
				map[row-1][column]=temp;
				//人刚才站的地方变成GRASS或者END
				map[row][column]=grassOrEnd(map[row][column]);
				ScrollFlag=false;
				//人离开后修改人的坐标
				row--;
			}else{
				//上一位是卷轴
				if(map[row-1][column]==SCROLL)
				{
					ScrollFlag=true;
					Map currMap=new Map(row,column,map);
					list.add(currMap);
					byte temp=MANUP;
					map[row-1][column]=temp;
					map[row][column]=grassOrEnd(map[row][column]);
					//卷轴记数+1
					ScrollCount++;
					gameMain.startScroll();
					Log.i(TAG,"ScrollCount"+ScrollCount);
					row--;
				}else{
					//上一位是target
					if(map[row-1][column]==TARGET)
					{
						Map currMap=new Map(row,column,map);
						list.add(currMap);
						//判断地图中是否还有卷轴或者未完成的箱子
						byte temp=TARGETEND;
						for(int i=0;i<mapRow;i++){
							for(int j=0;j<mapColumn;j++){
								if(map[i][j]==SCROLL||map[i][j]==END)
						           temp=TARGETUP;
							}
						}
						map[row-1][column]=temp;
						map[row][column]=grassOrEnd(map[row][column]);
						row--;
						}ScrollFlag=false;
			}
		}
	  }
	}
	
	private void moveDown()
	{
		//下一位为BOX,BOXONEND,WALL
		if(map[row+1][column]<4)
		{
			//下一位为 BOX,BOXONEND
			if(map[row+1][column]==BOX || map[row+1][column]==BOXONEND)
			{
				//下下一位为 END,GRASS则向下一步,其他不用处理
				if(map[row+2][column]==END || map[row+2][column]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row+2][column]==END?BOXONEND:BOX;
					byte manTemp=map[row+1][column]==BOX?MANDOWN:MANDOWNONEND;
					//箱子变成boxTemp,箱子往下一步
					map[row+2][column]=boxTemp;
					//人变成manTemp,往下走一步
					map[row+1][column]=manTemp;
					//人刚才站的地方变成 grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					row++;
					
				}
			}ScrollFlag=false;
		}
		else
		{
			//下一位为 GRASS,END,其他情况不用处理
			if(map[row+1][column]==GRASS || map[row+1][column]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row+1][column]==END?MANDOWNONEND:MANDOWN;
				//人变成temp,人往下走一步
				map[row+1][column]=temp;
				//人刚才站的地方变成 grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				row++;
				ScrollFlag=false;
			}else{
				//下一位是卷轴
				if(map[row+1][column]==SCROLL)
				{
					ScrollFlag=true;
					Map currMap=new Map(row,column,map);
					list.add(currMap);
					byte temp=MANDOWN;
					map[row+1][column]=temp;
					map[row][column]=grassOrEnd(map[row][column]);
					//卷轴记数+1
					ScrollCount++;
					gameMain.startScroll();
					Log.i(TAG,"ScrollCount"+ScrollCount);
					row++;
				}else{
					//下一位是target
					if(map[row+1][column]==TARGET)
					{
						Map currMap=new Map(row,column,map);
						list.add(currMap);
						//判断地图中是否还有钻石或者未完成的箱子
						byte temp=TARGETEND;
						for(int i=0;i<mapRow;i++){
							for(int j=0;j<mapColumn;j++){
								if(map[i][j]==SCROLL||map[i][j]==END)
						           temp=TARGETDOWN;
							}
						}
						map[row+1][column]=temp;
						map[row][column]=grassOrEnd(map[row][column]);
						row++;
						}ScrollFlag=false;
			}
			}		
		}
	}
	
	private void moveLeft()
	{
		//左一位为BOX,BOXONEND,WALL
		if(map[row][column-1]<4)
		{
			//左一位为 BOX,BOXONEND
			if(map[row][column-1]==BOX || map[row][column-1]==BOXONEND)
			{
				//左左一位为 END,GRASS则向左一步,其他不用处理
				if(map[row][column-2]==END || map[row][column-2]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row][column-2]==END?BOXONEND:BOX;
					byte manTemp=map[row][column-1]==BOX?MANLEFT:MANLEFTONEND;
					//箱子变成boxTemp,箱子往左一步
					map[row][column-2]=boxTemp;
					//人变成manTemp,往左走一步
					map[row][column-1]=manTemp;
					//人刚才站的地方变成 grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					column--;
					
				}
			}ScrollFlag=false;
		}
		else
		{
			//左一位为 GRASS,END,其他情况不用处理
			if(map[row][column-1]==GRASS || map[row][column-1]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row][column-1]==END?MANLEFTONEND:MANLEFT;
				//人变成temp,人往左走一步
				map[row][column-1]=temp;
				//人刚才站的地方变成 grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				column--;
				ScrollFlag=false;
			}else{
				//左一位是卷轴
				if(map[row][column-1]==SCROLL)
				{
					ScrollFlag=true;
					Map currMap=new Map(row,column,map);
					list.add(currMap);
					byte temp=MANLEFT;
					map[row][column-1]=temp;
					map[row][column]=grassOrEnd(map[row][column]);
					//卷轴记数+1
					ScrollCount++;
					gameMain.startScroll();
					Log.i(TAG,"ScrollCount"+ScrollCount);
					column--;
				}else{
					//左一位是target
					if(map[row][column-1]==TARGET)
					{
						Map currMap=new Map(row,column,map);
						list.add(currMap);
						byte temp=TARGETEND;
						for(int i=0;i<mapRow;i++){
							for(int j=0;j<mapColumn;j++){
								if(map[i][j]==SCROLL||map[i][j]==END)
						           temp=TARGETLEFT;
							}
						}
						map[row][column-1]=temp;
						map[row][column]=grassOrEnd(map[row][column]);
						column--;
						}ScrollFlag=false;
			}
			}		
		}
	}
	
	private void moveRight()
	{
		//右一位为BOX,BOXONEND,WALL
		if(map[row][column+1]<4)
		{
			//右一位为 BOX,BOXONEND
			if(map[row][column+1]==BOX || map[row][column+1]==BOXONEND)
			{
				//右右一位为 END,GRASS则向右一步,其他不用处理
				if(map[row][column+2]==END || map[row][column+2]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row][column+2]==END?BOXONEND:BOX;
					byte manTemp=map[row][column+1]==BOX?MANRIGHT:MANRIGHTONEND;
					//箱子变成boxTemp,箱子往右一步
					map[row][column+2]=boxTemp;
					//人变成manTemp,往右走一步
					map[row][column+1]=manTemp;
					//人刚才站的地方变成 grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					column++;
					
				}
			}ScrollFlag=false;
		}
		else
		{
			//右一位为 GRASS,END,其他情况不用处理
			if(map[row][column+1]==GRASS || map[row][column+1]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row][column+1]==END?MANRIGHTONEND:MANRIGHT;
				//人变成temp,人往右走一步
				map[row][column+1]=temp;
				//人刚才站的地方变成 grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				column++;
				ScrollFlag=false;
			}else{
				//右一位是卷轴
				if(map[row][column+1]==SCROLL)
				{
					ScrollFlag=true;
					Map currMap=new Map(row,column,map);
					list.add(currMap);
					byte temp=MANRIGHT;
					map[row][column+1]=temp;
					map[row][column]=grassOrEnd(map[row][column]);
					//卷轴记数+1
					ScrollCount++;
					gameMain.startScroll();
					Log.i(TAG,"ScrollCount"+ScrollCount);
					column++;
				}else{
					//右一位是target
					if(map[row][column+1]==TARGET)
					{
						Map currMap=new Map(row,column,map);
						list.add(currMap);
						//判断地图中是否还有卷轴或者未完成的箱子
						byte temp=TARGETEND;
						for(int i=0;i<mapRow;i++){
							for(int j=0;j<mapColumn;j++){
								if(map[i][j]==SCROLL||map[i][j]==END)
						           temp=TARGETRIGHT;
							}
						}
						map[row][column+1]=temp;
						map[row][column]=grassOrEnd(map[row][column]);
						column++;
						}
			    }ScrollFlag=false;
			}		
		}
	}
	
	public boolean isFinished()
	{  Log.i(TAG,"isfinished");
		for(int i=0;i<mapRow;i++){
			for(int j=0;j<mapColumn;j++){
				//if(map[i][j]==END || map[i][j]==MANDOWNONEND || map[i][j]==MANUPONEND || map[i][j]==MANLEFTONEND || map[i][j]==MANRIGHTONEND||map[i][j]==TAGET)
				if(map[i][j]==TARGET||map[i][j]==SCROLL||map[i][j]==END){
					return false;//游戏不结束
				}
			}
		}
		return true;
	}
	
	protected void paint(Canvas canvas)
	{
		//canvas.drawARGB(125, 0x94,0xDA, 0x3C);
		//canvas.drawRect(leftX, leftY,mapColumn*30,mapRow*30, paint);
		//canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(game_bg, 0, 0, paint);
		for(int i=0;i<mapRow;i++)
			for(int j=0;j<mapColumn;j++)
			{
				//画出地图 i代表行数,j代表列数
				if(map[i][j]!=0)
				canvas.drawBitmap(pic[map[i][j]], leftX+j*30,leftY+i*30, paint);
			//1.位图实例2，3位图的X,Y坐标，4画笔实例
			}
	}
	
	public void repaint()
	{
		Canvas c=null;
		try
		{//得到canvas画布对象
			c=holder.lockCanvas();
			paint(c);
		}
		finally
		{
			if(c!=null)
				holder.unlockCanvasAndPost(c);
		}
	}

	@Override
	//在按下动作时被调用 
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	//滑动手势事件
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		//Toast.makeText(gameMain, "ddd", Toast.LENGTH_LONG).show();
		float x1=e1.getX();
		float x2=e2.getX();
		float y1=e1.getY();
		float y2=e2.getY();
		float x=Math.abs(x1-x2);
		float y=Math.abs(y1-y2);
		if(x>y)
			if(x1<x2)
				this.onKeyDown(22,null);
			else
				this.onKeyDown(21,null);
		else
			if(y1<y2)
				this.onKeyDown(20,null);
			else
				this.onKeyDown(19,null);
		return false;
	}
	//在长按时被调用 
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	//在按住时被调用 
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}
	
	
	public int getManX()
	{
		return row;
	}
	public int getManY()
	{
		return column;
	}
	public int getGrade()
	{
		return grade;
	}
	public byte [][] getMap()
	{
		return map;
	}
	public int getStepCount()
	{
		return StepCount;
	}
	public int getScrollCount()
	{
		return ScrollCount;
	}

}
