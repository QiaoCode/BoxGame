package Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.box.GameMain;

public class Controller {
//�����������ȡͼƬ�ϵ�ָ���������п���
	private byte[][] map=null;
	//topcodes��
	private ArrayList TopCodeList=new ArrayList();
	//ָ������
	private ArrayList InstructionList=new ArrayList();
	//ָ�����Ϣ����
	private ArrayList BlockInfoList=new ArrayList();
	//���嵱ǰ�ı���ָ��
	private int CurrentInstruction=0;
	
	private Object BEGIN=0;
	private Object END=1;

	//�����˳�ִ��״̬
    public void Exit(){
    	//���е�topcode��ָ��list��Ҫ���
    	if(TopCodeList!=null){
    		/*if(TopCodeList.get(0)!=BEGIN||TopCodeList.get(TopCodeList.size()-1)!=END) {
    			���topcodes���list��ǰ��Ԫ�غ�����Ԫ�ز��ǿ�ʼ�ͽ�������ɾ��list*/
    		TopCodeList.clear();
    	}
//�����ĳ���ָ�Ҫ���
    	if(InstructionList.size()!=0){
    		InstructionList.clear();
    	}
    	
    }
    //�ж�Ԥ·�ߣ�����·�����Ƿ��д���
    public void PreExe(){
    	try
    	{   int tempWhileStart;//��ʱ��ʼ��λ��
    		CurrentInstruction=0;
    		int CurrentBlock=0;//��ǰ�ı�̿�
    	/*	if(Read()==-1)	//��ȡͼƬ��Ϣָ�ת��Ϊ�������
    		{	
    			CurrentInstruction = 10000;	//û��ľ��
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
//������ȡ�Ͳ�׽����ͷͼƬ����������ת��
	private int Read() {
		// TODO Auto-generated method stub
		try{
			Capture();//��׽����ͷͼƬ
			ConverCode();//��ͼƬ������ת��

			//��ʼ������ ������������
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
	//���GameMain�е�Bitmap bp
	
	
}
     
     
}
