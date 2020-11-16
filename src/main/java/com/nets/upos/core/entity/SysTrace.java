package com.nets.upos.core.entity;

public class SysTrace {

	static{
		int iMin = 0;
		int iMax = 999999;
		instance=new SysTrace(iMin,iMax);		
	}
	private static SysTrace instance;
	private int count;
	private int min;
	private int max;
	
    public SysTrace(int min,int max) {
    	count = min;
    	this.min = min;
    	this.max = max;
    }

    public static SysTrace getInstance() {
        return instance;
    }    
    
    public synchronized int getAndIncrement(){
    	int tmp = count;
    	if(count >= max)
    		count = min;
    	else
    		count++;

    	return tmp;
    }
}
