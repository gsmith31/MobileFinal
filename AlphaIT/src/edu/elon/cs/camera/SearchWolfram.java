package edu.elon.cs.camera;

import android.os.AsyncTask;
import android.util.Log;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;
/**
 * Copyright George W. Smith 2014
 * AlphaIT v 1.0
 */
public class SearchWolfram {
	
	private String input;
	private String result;
	private boolean isDone;
	private final String APP_ID = "AV867G-LP7GW98GGL";
	
	public SearchWolfram(String input){
		isDone = false;
		this.input = input;
		result = null;
	}
	
	public void executeQuery(){
		new SearchWolframAlpha().execute();
	}
	
	public boolean getIsDone(){
		return isDone;
	}
	public String getResult(){
		return result;
	}
	
	private class SearchWolframAlpha extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			result = null;
			

			WAEngine engine = new WAEngine();
			engine.setAppID(APP_ID);
			engine.addFormat("plaintext");

			WAQuery query = engine.createQuery();
			query.setInput(input);
			Log.d("INPUT", "Input is : " + input);
			try {
				System.out.println(engine.toURL(query));
				WAQueryResult queryResult = engine.performQuery(query);
				if (queryResult.isError()) {
					System.out.println(queryResult.getErrorMessage());
				} else if (!queryResult.isSuccess()) {
					System.out
							.println("Query was not understood; no results available.");
				} else {
					for (WAPod pod : queryResult.getPods()) {
						if (!pod.isError()) {
							System.out.println(pod.getTitle());
							if (pod.getTitle().equalsIgnoreCase("result") || pod.getTitle().equalsIgnoreCase("decimal approximation") || pod.getTitle().equalsIgnoreCase("exact result") || pod.getTitle().equalsIgnoreCase("response")) {
								for (WASubpod subpod : pod.getSubpods()) {
									for (Object element : subpod.getContents()) {
										if (element instanceof WAPlainText) {
											if(pod.getTitle().equalsIgnoreCase("result") || pod.getTitle().equalsIgnoreCase("exact result") || pod.getTitle().equalsIgnoreCase("response")){
												if(result == null){
													result = "Result: " +((WAPlainText) element)
															.getText();
												}else{
												result +=  "\n" + ((WAPlainText) element)
														.getText();
												}
											}
											if(pod.getTitle().equalsIgnoreCase("decimal approximation")){
												if(result == null){
													result = "Decimal Approximation: " +((WAPlainText) element)
															.getText();
												}else{
												result +=  "\n" + "Decimal Approximation: " + ((WAPlainText) element)
														.getText();
												}
											}
										
											
										}
									}
								}
							}
							System.out.println("");
						}
					}
				}
			} catch (WAException e) {
				e.printStackTrace();
			}
			isDone = true;
			return null;
		}
	}
}
