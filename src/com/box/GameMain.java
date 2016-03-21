package com.box;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.box.MapFactory;




public class GameMain extends Activity {
    /** Called when the activity is first created. */
	//GameView���棬�ý��湦��Ϊ�Ա������еĳ���������Ⱦ
	private GameView view=null;
	private Button bt_run;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //���mainactivity�е���ͼ
        view=(GameView)findViewById(R.id.gameView);
        //���ð�ť����
        bt_run=(Button)findViewById(R.id.bt_run);
        bt_run.setOnClickListener(new OnClickListener(){
          	@Override
          	public void onClick(View v){
          		open();
			}
    	});
    }	
    //����һ��intent���������
    private void open() {
			// TODO Auto-generated method stub
			Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,0);
    }
    //���µ�activity�л��ͼƬ����
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    //����õ�ͼ�����Bitmap��bp��
	 Bitmap bp=(Bitmap)data.getExtras().get("data");
 }
   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	menu.add(0,0,0,"��һ��");
    	menu.add(0,1,0,"��һ��");
    	menu.add(0,2,0,"����");
    	menu.add(0,3,0,"����");
    	menu.add(0,4,0,"�˳�");
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 0:
    		//��һ��
    		view.priorGrade();
    		break;
    	case 1:
    		//��һ��
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
    	//�˳�ʱ������Ϸ״̬
    	save();
    }
    
    public void save()
    {
    	//�˳�ʱ������Ϸ״̬
    	//��ͼ���ؿ���
    	//Map map=new Map(view.getManX(),view.getManY(),view.getMap(),view.getGrade());
    	byte [][]map=view.getMap();
    	int row=map.length;
    	int column=map[0].length;
    	//ʹ�ù��췽�����г�ʼ��,������ʼ������StringBuffer������һ���յĶ���
    	StringBuffer mapString=new StringBuffer();
    	//mapString���ո�ʽ
    	//�����ȴ洢������֮�䶺�Ÿ���
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
    	//�������һ�����ţ�����ʱע��
    	/*�ܶ�ʱ�����ǿ����������Ҫ���û��ṩ����������ù��ܣ�
    	 * Androidƽ̨�������ṩ��һ��SharedPreferences�࣬
    	 * ����һ���������Ĵ洢�࣬�ر��ʺ����ڱ���������ò�����
    	 * ʹ��SharedPreferences�������ݣ��䱳������xml�ļ�������ݣ�
    	 * �ļ������/data/data/<package name>/shared_prefsĿ¼��
    	 */
    	SharedPreferences pre=getSharedPreferences("map", 0);
    	//��ȡ�༭��
    	SharedPreferences.Editor editor=pre.edit();
    	//���ڸ���λ�á�������״̬
    	//SharedPreferences.Editor.putXX:��SharedPreferences�������ָ����key��Ӧ����ֵ
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
