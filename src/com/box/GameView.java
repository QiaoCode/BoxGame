package com.box;

import java.util.ArrayList;

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
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.box.Map;
import com.box.MapFactory;

//����ʹ��RelativeLayout��FrameLayout,Ȼ��view���߾�Ϊmatch_parent ����������view���²�
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) @SuppressLint("NewApi") public class GameView extends SurfaceView implements SurfaceHolder.Callback,OnGestureListener,OnTouchListener{
	

	private SurfaceHolder holder;
	private int grade=0;
	//row,column�����˵��к� �к�
	//leftX,leftY �������Ͻ�ͼƬ��λ��  ����ͼƬ��(0,0)���꿪ʼ
	private int row=7,column=7,leftX=0,leftY=0;
	//���ص�ͼ��������
	private int mapRow=0,mapColumn=0;
	//width,height ������Ļ�Ĵ�С
	private int width=0,height=0;
	//acceptKey�жϰ����¼�
	private boolean acceptKey=true;
	//�������õ���ͼƬ
	private Bitmap pic[]=null;
	private Bitmap game_bg;
	//�����ʯ�ļ���
	private int diamondcount=0;
	//����һЩ��������Ӧ��ͼ��Ԫ��
	final byte WALL=1,BOX=2,BOXONEND=3,END=4,MANDOWN=5,MANLEFT=6,MANRIGHT=7,
			MANUP=8,GRASS=9,MANDOWNONEND=10,MANLEFTONEND=11,MANRIGHTONEND=12,
			MANUPONEND=13,DIAMOND=14,WATER=15,TARGET=16;
	private Paint paint=null;
	private GameMain gameMain=null;
	private byte[][] map=null;
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
			//����
			if(list.size()>0)
			{
				//��Ҫ���� �����߹�
				Map priorMap=(Map)list.get(list.size()-1);
				map=priorMap.getMap();
				row=priorMap.getManX();
				column=priorMap.getManY();
				repaint();
				list.remove(list.size()-1);
			}
			else
				
				Toast.makeText(this.getContext(), "�����ٳ�����", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this.getContext(), "�˹�����ɣ����ܳ�����", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void nextGrade()
	{
		//grade++;
		if(grade>=MapFactory.getCount()-1)
			{
			Toast.makeText(this.getContext(), "��ϲ��������йؿ���", Toast.LENGTH_LONG).show();
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
	//����
	public void initMap()
	{
		map=gameMain.getMap(grade);
		list.clear();
		getMapSizeAndPosition();
		getManPosition();
//		Map currMap=new Map(row, column, map);
//		list.add(currMap);
	}
	
	public void resumeGame()
	{//����SharedPreferences�е�����
		SharedPreferences pre=this.getContext().getSharedPreferences("map", 0);
		//getString()�ڶ�������Ϊȱʡֵ�����preference�в����ڸ�key��������ȱʡֵ
		String mapString=pre.getString("mapString", "");
		if(mapString.equals(""))
			initMap();
		else
		{//���򻻳��û���һ���˳������
		row=pre.getInt("manX", 0);
		column=pre.getInt("manY", 0);
		int rowCount=pre.getInt("row", 0);
		int columnCount=pre.getInt("column", 0);
		grade=pre.getInt("grade", 0);
		map=new byte[rowCount][columnCount];
		String str[]=mapString.split(",");
		int index=0;
		for(int i=0;i<rowCount;i++)
			for(int j=0;j<columnCount;j++)
			{
				map[i][j]=(byte)Integer.parseInt(str[index++]);
			}
		getMapSizeAndPosition();}
		//getManPosition();���û���˵�λ�ã���Ϊ��ͼ��ʼ������ֱ������˵�λ�õĻع�
	}
//���췽��
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameMain=(GameMain)context;
		getPic();
		//ʵ��holder
		holder=this.getHolder();
		//���Ӽ���
		holder.addCallback(this);
		this.setOnTouchListener(this);
		this.setLongClickable(true);
		WindowManager manager=gameMain.getWindowManager();
		width=manager.getDefaultDisplay().getWidth();
		height=manager.getDefaultDisplay().getHeight();
		this.setFocusable(true);
		//����
		GestureDetector localGestureDetector = new GestureDetector(this);
	    this.mGestureDetector = localGestureDetector;
		//initMap();
	    //���췽��ִ��ʱ�����������лָ���Ϸ
	    //�ؿ��л�ʱ����initMap()
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
		pic=new Bitmap[15];
		game_bg=BitmapFactory.decodeResource(getResources(), R.drawable.game_bg);
		//pic[0]=BitmapFactory.decodeResource(getResources(), R.drawable.pic0);
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
		repaint();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//����Ĭ�ϵ�onKeyDown������������°����˸���ͻ᷵��true ���Իص�����ϵͳ��رյ�ǰactivity 
		if(!acceptKey)//���ð���������
			return super.onKeyDown(keyCode, event);
		/*KEYCODE_DPAD_UP=19;
		KEYCODE_DPAD_DOWN=20;
		KEYCODE_DPAD_LEFT=21;
		KEYCODE_DPAD_RIGHT=22;*/
		if(keyCode==19)
		{
			//����
			moveUp();
		}
		if(keyCode==20)
		{
			//����
			moveDown();
		}
		if(keyCode==21)
		{
			//����
			moveLeft();
		}
		if(keyCode==22)
		{
			//����
			moveRight();
		}
		repaint();
		///////////////////
		if(isFinished())
		{
			//���ð���
			acceptKey=false;
			//��ʾ������һ��
			Builder builder=new AlertDialog.Builder(gameMain);
			builder.setTitle("��ϲ����!");
			builder.setMessage("������һ����?");
			builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//������һ��
					acceptKey=true;
					nextGrade();
				}
			});
			builder.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
				
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
	//���뿪���޸��˵����꣬Ĭ�Ϸ�����GRASS��������뿪ǰ��λ��ʱMANEND���򷵻�END
	{
		byte result=GRASS;
		if(man==MANDOWNONEND || man==MANLEFTONEND || man==MANRIGHTONEND || man==MANUPONEND)
			result=END;//END�����ӵ��յ�
		return result;
	}
	
	private void moveUp()
	{
		//��һλΪBOX,BOXONEND,WALL
		//row��column���˵����к�
		if(map[row-1][column]<4)//1��2��3�ֱ�Ϊǽ��������ӣ������ߵģ�
		{
			//��һλΪ BOX,BOXONEND
			if(map[row-1][column]==BOX || map[row-1][column]==BOXONEND)
			{
				//����һλΪ END,GRASS������һ��,�������ô���
				if(map[row-2][column]==END || map[row-2][column]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row-2][column]==END?BOXONEND:BOX;
					byte manTemp=map[row-1][column]==BOX?MANUP:MANUPONEND;
					//���ӱ��temp,������ǰһ��
					map[row-2][column]=boxTemp;
					//�˱��MANUP,������һ��
					map[row-1][column]=manTemp;
					//�˸ղ�վ�ĵط����GRASS����END
					map[row][column]=grassOrEnd(map[row][column]);
					//���뿪���޸��˵�����
					row--;
				}
			}
		}
		else
		{
			//��һλΪ GRASS,END,����������ô���
			if(map[row-1][column]==GRASS || map[row-1][column]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row-1][column]==END?MANUPONEND:MANUP;
				//�˱��temp,��������һ��
				map[row-1][column]=temp;
				//�˸ղ�վ�ĵط����GRASS����END
				map[row][column]=grassOrEnd(map[row][column]);
				//���뿪���޸��˵�����
				row--;
			}else{
				//��һλ����ʯ
				if(map[row-1][column]==DIAMOND)
				{
					Map currMap=new Map(row,column,map);
					list.add(currMap);
					byte temp=MANUP;
					map[row-1][column]=temp;
					map[row][column]=grassOrEnd(map[row][column]);
					//��ʯ����+1
					diamondcount++;
					row--;
				}
			}
		}
	}
	
	private void moveDown()
	{
		//��һλΪBOX,BOXONEND,WALL
		if(map[row+1][column]<4)
		{
			//��һλΪ BOX,BOXONEND
			if(map[row+1][column]==BOX || map[row+1][column]==BOXONEND)
			{
				//����һλΪ END,GRASS������һ��,�������ô���
				if(map[row+2][column]==END || map[row+2][column]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row+2][column]==END?BOXONEND:BOX;
					byte manTemp=map[row+1][column]==BOX?MANDOWN:MANDOWNONEND;
					//���ӱ��boxTemp,��������һ��
					map[row+2][column]=boxTemp;
					//�˱��manTemp,������һ��
					map[row+1][column]=manTemp;
					//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					row++;
					
				}
			}
		}
		else
		{
			//��һλΪ GRASS,END,����������ô���
			if(map[row+1][column]==GRASS || map[row+1][column]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row+1][column]==END?MANDOWNONEND:MANDOWN;
				//�˱��temp,��������һ��
				map[row+1][column]=temp;
				//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				row++;
				
			}		
		}
	}
	
	private void moveLeft()
	{
		//��һλΪBOX,BOXONEND,WALL
		if(map[row][column-1]<4)
		{
			//��һλΪ BOX,BOXONEND
			if(map[row][column-1]==BOX || map[row][column-1]==BOXONEND)
			{
				//����һλΪ END,GRASS������һ��,�������ô���
				if(map[row][column-2]==END || map[row][column-2]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row][column-2]==END?BOXONEND:BOX;
					byte manTemp=map[row][column-1]==BOX?MANLEFT:MANLEFTONEND;
					//���ӱ��boxTemp,��������һ��
					map[row][column-2]=boxTemp;
					//�˱��manTemp,������һ��
					map[row][column-1]=manTemp;
					//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					column--;
					
				}
			}
		}
		else
		{
			//��һλΪ GRASS,END,����������ô���
			if(map[row][column-1]==GRASS || map[row][column-1]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row][column-1]==END?MANLEFTONEND:MANLEFT;
				//�˱��temp,��������һ��
				map[row][column-1]=temp;
				//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				column--;
				
			}		
		}
	}
	
	private void moveRight()
	{
		//��һλΪBOX,BOXONEND,WALL
		if(map[row][column+1]<4)
		{
			//��һλΪ BOX,BOXONEND
			if(map[row][column+1]==BOX || map[row][column+1]==BOXONEND)
			{
				//����һλΪ END,GRASS������һ��,�������ô���
				if(map[row][column+2]==END || map[row][column+2]==GRASS)
				{
					Map currMap=new Map(row, column, map);
					list.add(currMap);
					byte boxTemp=map[row][column+2]==END?BOXONEND:BOX;
					byte manTemp=map[row][column+1]==BOX?MANRIGHT:MANRIGHTONEND;
					//���ӱ��boxTemp,��������һ��
					map[row][column+2]=boxTemp;
					//�˱��manTemp,������һ��
					map[row][column+1]=manTemp;
					//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
					map[row][column]=grassOrEnd(map[row][column]);
					column++;
					
				}
			}
		}
		else
		{
			//��һλΪ GRASS,END,����������ô���
			if(map[row][column+1]==GRASS || map[row][column+1]==END)
			{
				Map currMap=new Map(row, column, map);
				list.add(currMap);
				byte temp=map[row][column+1]==END?MANRIGHTONEND:MANRIGHT;
				//�˱��temp,��������һ��
				map[row][column+1]=temp;
				//�˸ղ�վ�ĵط���� grassOrEnd(map[row][column])
				map[row][column]=grassOrEnd(map[row][column]);
				column++;
				
			}		
		}
	}
	
	public boolean isFinished()
	{
		for(int i=0;i<mapRow;i++)
			for(int j=0;j<mapColumn;j++)
				if(map[i][j]==END || map[i][j]==MANDOWNONEND || map[i][j]==MANUPONEND || map[i][j]==MANLEFTONEND || map[i][j]==MANRIGHTONEND)
				return false;
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
				//������ͼ i��������,j��������
				if(map[i][j]!=0)
				canvas.drawBitmap(pic[map[i][j]], leftX+j*30,leftY+i*30, paint);
			//1.λͼʵ��2��3λͼ��X,Y���꣬4����ʵ��
			}
	}
	
	public void repaint()
	{
		Canvas c=null;
		try
		{//�õ�canvas��������
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
	//�ڰ��¶���ʱ������ 
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	//���������¼�
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
	//�ڳ���ʱ������ 
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
	//�ڰ�סʱ������ 
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

}