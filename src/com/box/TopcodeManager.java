package com.box;

import java.util.Map;
import java.util.HashMap;
import java.util.*;

import TopCodes.TopCode;

public class TopcodeManager {
	private static final TopcodeManager INSTANCE = new TopcodeManager();
	
	private static Map<Integer, int[]> topCodeMap;
	private static Map<Integer, Integer> loopMap;
	
	private static final int END = 179;
	
	private void init() {
		topCodeMap = new HashMap<Integer, int[]>();
		loopMap = new HashMap<Integer, Integer>();
		// read Topcodes from file
		loadTopcodes();
	}
	
	private TopcodeManager(){
		init();
	}
	
	public static TopcodeManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 
	 */
	public int[] getKeyCode(int topCode){
		return topCodeMap.get(topCode);
	}
	
	public int getLoop(int topCode){
		return loopMap.get(topCode);
	}
	
	public boolean containsLoopCode(int topCode){
		return loopMap.containsKey(topCode);
	}

	
	/**
	 * File Reader 
	 */
	public void loadTopcodes() {
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
	}
	
	public boolean nullOrEmptyOrBlankString(String s){
		if (s == null || s.trim().length() == 0){
			return true;
		}
		return false;
	}
	 //对循环块进行处理
		public static List<TopCode> produceLoopArray(List<TopCode> codes){
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
				//else if (TopcodeManager.getInstance().containsLoopCode(codes.get(i).getCode())){
					isStart = true;
					times = loopMap.get(codes.get(i).getCode());
					//times = TopcodeManager.getInstance().getLoop(codes.get(i).getCode());
					tmp = new ArrayList<TopCode>();
				}
				else if (codes.get(i).getCode() == END){
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
	// test
	public static void main(String[] args){
		TopcodeManager topcodeManager = TopcodeManager.getInstance();
		int[] keyCode = topcodeManager.getKeyCode(47);// expected to be 19 : 1
		System.out.println(keyCode[0] + ": " + keyCode[1]);
		int frequency = topcodeManager.getLoop(171);// expected to be 4
		System.out.println(frequency);
	}
}