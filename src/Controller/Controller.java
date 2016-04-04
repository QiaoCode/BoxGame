package Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import TopCodes.TopCode;

import android.graphics.Bitmap;
import android.view.KeyEvent;

import com.box.GameMain;
import com.box.GameView;

public class Controller {
//这个类用来读取图片上的指令，对人物进行控制
	private byte[][] map=null;
	//指令链表
	private ArrayList InstructionList=new ArrayList();
	//指令块信息链表
	private ArrayList BlockInfoList=new ArrayList();
	private Object BEGIN=0;
	private Object END=1;
	private GameView view=null;
	//topcodes码
	List<TopCode> TopCodeList =new ArrayList<TopCode>();
//全部指令获得后，用runall()函数判断执行
//未加记歩，如何直接调用keycode方法？
	public void runall(){
	  for(int i=0;i<InstructionList.size();++i){
		switch (Instruction.toString(InstructionList.get(i)))
		{
		case UP:
			view.moveUp();
			break;
		case DOWN:
			view.moveDown();
			break;
		case LEFT:
			view.moveLeft();
			break;
		case RIGHT:
			view.moveRight();
			break;
		  }
	  }
	}
//使用switch枚举类型
	public enum Instruction
	{
	UP, DOWN, LEFT, RIGHT,NOVALUE;
	
	public static Instruction toString(Object object)
	{
	try {
	return valueOf((String) object);
	    }
	catch (Exception ex) {
	return NOVALUE;
	    }
	}
	}
    //获得图片
    public void getBitmap(){
   // 	TopCodeList.scan(bp);
    }
	//用来退出执行状态
    public void Exit(){
    	//所有的topcode和指令list都要清空
    	if(TopCodeList!=null){
    		/*if(TopCodeList.get(0)!=BEGIN||TopCodeList.get(TopCodeList.size()-1)!=END) {
    			如果topcodes码的list最前的元素和最后的元素不是开始和结束，则删除list*/
    		TopCodeList.clear();
    	}
//编译后的程序指令都要清空
    	if(InstructionList.size()!=0){
    		InstructionList.clear();
    	}
    	
    }
    //判断预路线，看看路线上是否有错误
    public void PreExe(){
    	try
    	{   int tempWhileStart;//临时开始的位置
    		int CurrentBlock=0;//当前的编程块
    	/*	if(Read()==-1)	//读取图片信息指令并转换为程序代码
    		{	
    			CurrentInstruction = 10000;	//没有木块
    			return errorInfo(1000);
    		}
    		if (m_InstructionList.size()!=0)
    		{
    			int k=m_InstructionList.size();
    		}*/

    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }

	private Object errorInfo(int i) {
		// TODO Auto-generated method stub
		return null;
	}
//用来读取和捕捉摄像头图片，并做代码转化
	private int Read() {
		// TODO Auto-generated method stub
		try{
			Capture();//捕捉摄像头图片
			ConverCode();//将图片做代码转化

			//初始化数组 对数组进行清空
			if(InstructionList.size() > 0)
			{
				InstructionList.clear();

			}
		}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
		return 0;
	}
private void ConverCode() {
	// TODO Auto-generated method stub
	
}
private void Capture() {
	// TODO Auto-generated method stub
	//获得GameMain中的Bitmap bp
	
	
}
     

}
