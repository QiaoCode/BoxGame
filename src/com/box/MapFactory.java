package com.box;

public class MapFactory {
//1围墙 2黄箱子（未进目的地）3红箱子（进了目的地）4目的地 5-8小人，9地面，10-13进入目的地的小人,14钻石,17终点
//map是20个二位数组
	static byte map[][][]={
			{//1
			{ 0, 0, 1,  1,  1, 0, 0, 0 },
			{ 0, 0, 1,  5,  1, 0, 0, 0 },
			{ 0, 0, 1,  9,  1, 1, 1, 0 },
			{ 0, 1, 1, 14,  9, 9, 1, 0 },
			{ 0, 1, 9, 14,  9, 1, 1, 0 },
			{ 0, 1, 1,  1,  9, 1, 0, 0 },
			{ 0, 0, 0,  1, 17, 1, 0, 0 },
			{ 0, 0, 0,  1,  1, 1, 0, 0 }
			},

			{//2
			{ 1, 1, 1, 1, 1, 0, 0, 0, 0 },
			{ 1, 9, 9, 9, 1, 0, 0, 0, 0 },
			{ 1, 9, 7, 9, 1, 0, 1, 1, 1 },
			{ 1, 9, 9, 14, 1, 0, 1, 9, 1 },
			{ 1, 1, 1, 9, 1, 1, 1, 9, 1 },
			{ 0, 1, 1, 9, 14, 9, 14, 9, 1 },
			{ 0, 1, 9, 9, 9, 1, 9, 17, 1 },
			{ 0, 1, 9, 9, 9, 1, 1, 1, 1 },
			{ 0, 1, 1, 1, 1, 1, 0, 0, 0 }
			},

			{//3
			{ 0, 1, 1, 1,  1, 1,  1, 1, 0, 0 },
			{ 0, 1, 9, 9,  9, 9,  9, 1, 1, 1 },
			{ 1, 1, 7, 1, 14, 1, 14, 9, 9, 1 },
			{ 1, 9, 9, 9,  9, 9,  9, 9, 9, 1 },
			{ 1, 9, 9, 9,  1, 9, 17, 9, 1, 1 },
			{ 1, 1, 9, 9,  1, 9,  9, 9, 1, 0 },
			{ 0, 1, 1, 1,  1, 1,  1, 1, 1, 0 }
			},

			{//4
			{ 0, 1,  1,  1, 1, 0 },
			{ 1, 1,  9,  9, 1, 0 },
			{ 1, 9,  5,  9, 1, 0 },
			{ 1, 1,  9,  9, 1, 1 },
			{ 1, 1,  9, 14, 9, 1 },
			{ 1, 4,  2,  9, 9, 1 },
			{ 1, 9, 17,  9, 9, 1 },
			{ 1, 1,  1,  1, 1, 1 }
			},

			{//5
			{ 0, 0, 1, 1, 1, 0, 0, 0 },
			{ 0, 0, 1, 17, 1, 0, 0, 0 },
			{ 0, 0, 1, 9, 1, 1, 1, 1 },
			{ 1, 1, 1, 14, 9, 2, 4, 1 },
			{ 1, 4, 9, 2, 5, 1, 1, 1 },
			{ 1, 1, 1, 1, 2, 1, 0, 0 },
			{ 0, 0, 0, 1, 4, 1, 0, 0 },
			{ 0, 0, 0, 1, 1, 1, 0, 0 }
			},
			
		    {//6
			{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 },
			{ 0, 0, 1, 1, 9, 9, 1, 9, 5, 1 },
			{ 0, 0, 1, 9, 9, 9, 1, 9, 9, 1 },
			{ 0, 0, 1, 9, 9, 9, 9,14, 9, 1 },
			{ 0, 0, 1,14, 9, 1, 1, 9, 9, 1 },
			{ 1, 1, 1, 9, 9, 9, 1, 9, 1, 1 },
			{ 1,17,14, 9, 4, 9, 2, 9, 1, 0 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }
			},
		{//7
				{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 9, 9, 9, 9, 1, 0 },
				{ 1, 1, 9, 9, 9,14, 1, 9, 1, 1 },
				{ 1, 9, 9,14, 1, 1,14, 9, 5, 1 },
				{ 1,17, 9, 9, 9, 9, 1, 9, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 9, 9, 1, 0 },
				{ 0, 0, 0, 0, 0, 1, 1, 1, 1, 0 }
			},
		{//8
				{ 0, 0,  1, 1, 1, 1, 1, 1 },
				{ 0, 0,  1, 9, 14, 9, 9, 1 },
				{ 1, 1,  1, 9, 2, 9, 9, 1 },
				{ 1, 5, 14, 9, 9, 4, 9, 1 },
				{ 1, 9,  2, 4, 9, 9, 1, 1 },
				{ 1, 1,  1, 1, 17, 9, 1, 0 },
				{ 0, 0, 0, 1, 1, 1, 1, 0 }
			},
		{//9
				{ 0, 0, 1,  1, 1,  1, 0, 0 },
				{ 0, 0, 1,  17, 9,  1, 0, 0 },
				{ 0, 1, 1,  9, 9,  1, 1, 0 },
				{ 0, 1, 9,  9, 9,  9, 1, 0 },
				{ 1, 1, 9,  9, 9, 14, 9, 1 },
				{ 1, 9, 9, 14, 2,  9, 4, 1 },
				{ 1, 5, 2,  9, 4,  9, 9, 1 },
				{ 1, 1, 1,  1, 1,  1, 1, 1 }
			},
		{//10
				{ 0,  0, 1, 1,  1, 1, 1, 0 },
				{ 1,  1, 1, 14,17, 9, 1, 0 },
				{ 1,  9, 9, 9, 14, 9, 1, 1 },
				{ 1,  9, 9, 4,  2, 4, 5, 1 },
				{ 1,  1, 1, 9,  3, 2, 9, 1 },
				{ 0,  0, 1, 9,  14, 9, 1, 1 },
				{ 0,  0, 1, 1, 1, 1, 1, 0 }
			}
			
	};
	
	static int count=map.length;//获得数组map的长度（所有关卡的数量）
	
	/*public static byte[][] getMap(int grade)
	{
		if(grade>=0 && grade<count-1)
			return map[grade].clone();
		return map[0].clone();
	}*/
	//每一关分别获得二维数组地图，存放在temp[][]
	public static byte[][] getMap(int grade)
	{   byte temp[][];
		if(grade>=0 && grade<count)
			temp=map[grade];//调出map[第几关卡]地图
		else
			temp=map[0];
		//获得地图的长和宽
		int row=temp.length;
		int column=temp[0].length;
		//新建字节型数组result,放入temp数组
		byte[][] result=new byte[row][column];
		for(int i=0;i<row;i++)
			for(int j=0;j<column;j++)
				result[i][j]=temp[i][j];
		return result;
	}
	
	public static int getCount()
	{
		return count;
	}
	
}
