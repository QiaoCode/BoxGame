package com.box;

import java.util.Map;
import java.util.HashMap;

public class TopcodeManager {
	private static final TopcodeManager INSTANCE = new TopcodeManager();
	
	private static Map<Integer, int[]> topCodeMap;
	private static Map<Integer, Integer> loopMap;
	
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
	
	// test
	public static void main(String[] args){
		TopcodeManager topcodeManager = TopcodeManager.getInstance();
		int[] keyCode = topcodeManager.getKeyCode(47);// expected to be 19 : 1
		System.out.println(keyCode[0] + ": " + keyCode[1]);
		int frequency = topcodeManager.getLoop(171);// expected to be 4
		System.out.println(frequency);
	}
}