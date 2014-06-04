package com.example.mensafinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.RequestConnControl;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;

public class HttpQueries {

	public static String serverUrlOrientation = "http://barracuda-vm8.informatik.uni-ulm.de/orientations";

	@SuppressLint("NewApi")
	public static String pushUpdates() {

		InputStream inputStream = null;
		String result = "";
		try {
			
//			infinite waiting time for data. No timeout. Doesnt work apparently :(
//			HttpParams httpParams = new BasicHttpParams();
//			HttpConnectionParams.setSoTimeout(httpParameters, 0);
			
//			some alternative to above. doesnt work either.
//			HttpParams httpParams = new BasicHttpParams();
//			httpParams.setLongParameter("idleTimeout", 99999999999999L);	
//			HttpClient httpclient = new DefaultHttpClient(httpParams);
			

			
			HttpClient httpclient = new DefaultHttpClient();
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);


			HttpGet httpGet = new HttpGet(
					serverUrlOrientation + "/stream");
			HttpResponse httpResponse = httpclient.execute(httpGet);
			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				convertExtendedInputStreamToString(inputStream);
			else
				result = null;

		} catch (Exception e) {
//			lets restart this method when we get Error "Chunked stream ended unexpectedly". Caused by idle connection.
//			I guess the server closes the connection.
			if(e.getMessage() != null){
				Log.d("InputStream", e.toString());
				Log.d("InputStream", e.getMessage());
				if (e.getMessage() == "Chunked stream ended unexpectedly"){
					pushUpdates();
				}
			}
			else {
				Log.d("InputStream", e.toString());
			}
		}
		return result;

	}
	
	@SuppressLint("NewApi")
	public static String sendMyOrientation(int degrees, String name) {

		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();


			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpPut httpPut = new HttpPut(
					"http://barracuda-vm8.informatik.uni-ulm.de/user/" + name + "/orientation/" + degrees);
			HttpResponse httpResponse = httpclient.execute(httpPut);
			inputStream = httpResponse.getEntity().getContent();
			
		} catch (Exception e) {
			Log.d("InputStream", e.getMessage());
		}
		return result;

	}
	
	@SuppressLint("NewApi")
	public static String requestListOfPersonsOfInterest() {

		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpGet httpGet = new HttpGet(
					serverUrlOrientation + "/snapshot");
			HttpResponse httpResponse = httpclient.execute(httpGet);
			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = null;
			
		} catch (Exception e) {
			Log.d("InputStream", e.getMessage());
		}
		System.out.println(result);
		return result;

	}
	

	
	
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null){
			result += line;
		}
		
		inputStream.close();
		return result;

	}

	
	
	private static String convertExtendedInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null){			
			System.out.println("line:" + line);
			try {
				JSONObject jObject = new JSONObject(line);
				if (jObject.has("update")) {
					
					String name = jObject.getJSONObject("update").getString("user");
					int orientation =Integer.parseInt ( jObject.getJSONObject("update").getString("orientation") );
					MainActivity.user.put(name, orientation);
					MainActivity.mCustomDrawableView.postInvalidate();
				}else if (jObject.has("logout")) {
					String name = jObject.getJSONObject("logout").getString("user");
					MainActivity.user.remove(name);
					MainActivity.mCustomDrawableView.postInvalidate();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		inputStream.close();
		return result;

	}

	@SuppressLint("NewApi")
	public static String logout(String name) {

		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();


			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpDelete httpDelete = new HttpDelete(
					"http://barracuda-vm8.informatik.uni-ulm.de/user/" + name + "/orientation"); 
			HttpResponse httpResponse = httpclient.execute(httpDelete);
			inputStream = httpResponse.getEntity().getContent();
			
		} catch (Exception e) {
			//Log.d("InputStream", e.getMessage());
		}
		return result;
	}


}
