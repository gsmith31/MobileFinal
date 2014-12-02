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

	private final String APP_ID = "AV867G-WAK5YX24VV";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		search = (Button) findViewById(R.id.search);
		display = (TextView) findViewById(R.id.result);
		searchPhrase = (EditText) findViewById(R.id.input);
		search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new SearchWolframAlpha().execute(searchPhrase.getText().toString());
				
			}
			
		});
	
		
		}

	private class SearchWolframAlpha extends
			AsyncTask<String, Void, WAQueryResult> {

		@Override
		protected WAQueryResult doInBackground(String... arg0) {
			String input = arg0[0];
			WAEngine engine = new WAEngine();
			engine.setAppID(APP_ID);
			engine.addFormat("plaintext");
			// Create the query.
			WAQuery query = engine.createQuery();
			// Set properties of the query.
			query.setInput(input);
			try {
				return engine.performQuery(query);
			} catch (WAException e) {
				Log.d("Error", "Message:  " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(WAQueryResult result) {
			if (result != null) {
				for (WAPod pod : result.getPods()) {
					if (!pod.isError()) {
						Log.d("Pod", pod.getTitle());
						if (pod.getTitle().equalsIgnoreCase("result")) {
							for (WASubpod subpod : pod.getSubpods()) {
								for (Object element : subpod.getContents()) {
									if (element instanceof WAPlainText) {
										display.setText(((WAPlainText) element)
														.getText());
										Log.d("Content",
												((WAPlainText) element)
														.getText());
									}
								}
							}
						}

					}

				}
			}

		}

	}
}
