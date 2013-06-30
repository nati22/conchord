package com.conchord.android.network.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.conchord.android.util.SafeAsyncTask;
import com.conchord.android.util.Utils;

public class BaseDeleteRequestAsyncTask extends SafeAsyncTask {

	static final String BASE_URL = "http://conchordapp.appspot.com";

	protected String responseString = "";
	private String uri = null;
	private Context context;

	protected BaseDeleteRequestAsyncTask(Context context, String uriSuffix) {
		super();
		this.uri = BASE_URL + uriSuffix;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		// Verify there is an Internet connection
		if (!Utils.isNetworkAvailable(context)) {
			// If there is no Internet connection, then don't run the AsyncTask.
			cancel(true);
		}
	}
	
	@Override
	public Object call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpUriRequest deleteRequest = new HttpDelete(uri);
		
		Log.v("BaseDeleteRequestAsyncTask", "Sending DELETE request with URI: "
				+ uri);
		String responseString = EntityUtils.toString(client
				.execute(deleteRequest).getEntity());

		if (responseString != null) {
			Log.v("BaseDeleteRequestAsyncTask", "Got HTTP result: "
					+ responseString);
		} else {
			throw new Exception("DELETE request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;
		
		return null;
	}
	
	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
	}

}