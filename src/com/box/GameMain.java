package com.box;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.box.MapFactory;




public class GameMain extends Activity {
    /** Called when the activity is first created. */
	//GameView界面，该界面功能为对本案例中的场景进行渲染。该类需要继承Android系统中的SurfaceView类,并实现SurfaceHolder.Callback接口.
	private GameView view=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //获得mainactivtty中的视图
        view=(GameView)findViewById(R.id.gameView);
    }
    
   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	menu.add(0,0,0,"上一关");
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
    	//退出时保存游戏状态
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
    			
    		}mapString.append(",");}
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
    	editor.putInt("manX", view.getManX());
    	editor.putInt("manY", view.getManY());
    	editor.putInt("grade", view.getGrade());
    	editor.putInt("row", row);
    	editor.putInt("column", column);
    	editor.putString("mapString", mapString.toString());
    	editor.commit();
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
