package io.jawware.noty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebSearch extends AppCompatActivity {


    WebView wb;

    private static final String TAG = "WebSearch";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_search);

         wb = (WebView) findViewById(R.id.webSearch);
        String fetchData = getIntent().getStringExtra("REQUEST_WEB_SEARCH_FROM_SERVER");


        String searchStringNoSpaces = fetchData.replace(" ", "+");

        String url  = "https://www.google.com/search?q="+searchStringNoSpaces;





//        resultTextView = (TextView) findViewById(R.id.textView1);
//        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
//

//
//        getSearchResponse(fetchData);


        wb.setWebViewClient(new myWebClient());
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl(url);


    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
// TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
// TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wb.canGoBack()) {
            wb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void getSearchResponse(String searchString){
//
//
//        Log.d(TAG, "Searching for : " + searchString);
//        resultTextView.setText("Searching for : " + searchString);
//
////        // hide keyboard
////        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//        // looking for
//        String searchStringNoSpaces = searchString.replace(" ", "+");
//
//        // Your API key
//        // TODO replace with your value
//        String key="AIzaSyAARKR75ccLhk5E8-xVxJmfYoVabw2yFL8";
//
//        // Your Search Engine ID
//        // TODO replace with your value
//        String cx = "016629028317849603532:kraguuw5mwy";
//
//        String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
//        URL url = null;
//        try {
//            url = new URL(urlString);
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "ERROR converting String to URL " + e.toString());
//        }
//        Log.d(TAG, "Url = "+  urlString);
//
//
//        // start AsyncTask
////        GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
////        searchTask.execute(url);
//
//    }

//    private class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {
//
//        protected void onPreExecute(){
//            Log.d(TAG, "AsyncTask - onPreExecute");
//            // show progressbar
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//
//        @Override
//        protected String doInBackground(URL... urls) {
//
//            URL url = urls[0];
//            Log.d(TAG, "AsyncTask - doInBackground, url=" + url);
//
//            // Http connection
//            HttpURLConnection conn = null;
//            try {
//                conn = (HttpURLConnection) url.openConnection();
//            } catch (IOException e) {
//                Log.e(TAG, "Http connection ERROR " + e.toString());
//            }
//
//
//            try {
//                responseCode = conn.getResponseCode();
//                responseMessage = conn.getResponseMessage();
//            } catch (IOException e) {
//                Log.e(TAG, "Http getting response code ERROR " + e.toString());
//            }
//
//            Log.d(TAG, "Http response code =" + responseCode + " message=" + responseMessage);
//
//            try {
//
//                if(responseCode == 200) {
//
//                    // response OK
//
//                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//
//                    while ((line = rd.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    rd.close();
//
//                    conn.disconnect();
//
//                    result = sb.toString();
//
//                    Log.d(TAG, "result=" + result);
//
//                    return result;
//
//                }else{
//
//                    // response problem
//
//                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
//                    Log.e(TAG, errorMsg);
//                    result = errorMsg;
//                    return  result;
//
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Http Response ERROR " + e.toString());
//            }
//
//
//            return null;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);
//
//        }
//
//        protected void onPostExecute(String result) {
//
//            Log.d(TAG, "AsyncTask - onPostExecute, result=" + result);
//
//            // hide progressbar
//            progressBar.setVisibility(View.GONE);
//
//            // make TextView scrollable
//            resultTextView.setMovementMethod(new ScrollingMovementMethod());
//            // show result
//            resultTextView.setText(result);
//
//        }
//
//
//    }

}
