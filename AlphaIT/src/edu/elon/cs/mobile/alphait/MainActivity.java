package edu.elon.cs.mobile.alphait;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button search;
	private TextView display;
	private EditText searchPhrase;
	private String result;
	private WAEngine engine;

	private final String APP_ID = "AV867G-LP7GW98GGL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		search = (Button) findViewById(R.id.search);
		display = (TextView) findViewById(R.id.result);
		searchPhrase = (EditText) findViewById(R.id.input);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("PRESSED", "Button was pressed");
				engine = new WAEngine();
				new SearchWolframAlpha().execute();
				
			}
		});

	}

	private class SearchWolframAlpha extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			WAEngine engine = new WAEngine();
			engine.setAppID(APP_ID);
			engine.addFormat("plaintext");

			WAQuery query = engine.createQuery();
			query.setInput(searchPhrase.getText().toString());

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
							if (pod.getTitle().equalsIgnoreCase("result")) {
								for (WASubpod subpod : pod.getSubpods()) {
									for (Object element : subpod.getContents()) {
										if (element instanceof WAPlainText) {
											result = ((WAPlainText) element)
													.getText();
											System.out
													.println(((WAPlainText) element)
															.getText());
											System.out.println("");
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
			return null;
		}
	}
}
