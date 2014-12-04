package edu.elon.cs.camera;

import android.util.Log;
/**
 * Copyright George W. Smith 2014
 * AlphaIT v 1.0
 */
public class ProcessResult {
	
	private String result;
	
	public ProcessResult(String result){
		this.result = result;
	}
	
	public String process(){
		if(result.indexOf("[") != -1 && result.indexOf("]") != -1){
			result = result.substring(result.indexOf("[") + 1,result.indexOf("]"));
		}
		if(isSqrt()){
			Log.d("SQRT", "Is sqrt");
			int index = result.indexOf("s");
			if(index != -1){
				result = result.substring(index, result.lastIndexOf(')') + 1);
				return result;
			}
		}
		return result;
		
	}
	
	
	public boolean isSqrt(){
		if(result.contains("sqrt")){
			return true;
		}
		return false;
	}
	
	
}
