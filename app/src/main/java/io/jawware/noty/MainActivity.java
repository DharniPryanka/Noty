package io.jawware.noty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private Button homeReturn;
    private String url;
    private ArrayList<String> mAuthorNameList = new ArrayList<>();
    private ArrayList<String> mBlogUploadDateList = new ArrayList<>();
    private ArrayList<String> mBlogTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        url = getIntent().getStringExtra("REQUEST_URL_FROM_SERVER");

        url="https://timesofindia.indiatimes.com/business/india-business/markets";


        homeReturn = (Button)findViewById(R.id.returnback_share_market);

        homeReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Dashboard.class));
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        new Description().execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Requesting");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Connect to the web site
            Document mBlogDocument = null;
            try {
                mBlogDocument = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Using Elements to get the Meta data
            Elements mElementDataSize = null;
            if (mBlogDocument != null) {
                mElementDataSize = mBlogDocument.select("div[class=tab-content]").select("ul");
            }else{
                Toast.makeText(MainActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
            }
            // Locate the content attribute
            int mElementSize = 0;
            if (mElementDataSize != null) {
                mElementSize = mElementDataSize.size();
            }

            for (int i = 0; i < 4; i++) {
                Elements mElementAuthorName = mBlogDocument.select("div[class=tab-content]").select("ul").select("li").select("a").eq(i);
//                Elements mElementAuthorName = mBlogDocument.select("a[href]");
                String mAuthorName = mElementAuthorName.text();

                Elements mElementBlogUploadDate = mBlogDocument.select("div[class=tab-content]").select("ul").select("li").select("a[href]").eq(i);
                String mBlogUploadDate = mElementBlogUploadDate.attr("abs:href");

//                Elements mElementBlogTitle = mBlogDocument.title();
                String mBlogTitle = mBlogDocument.title();

                mAuthorNameList.add(mAuthorName);
                mBlogUploadDateList.add(mBlogUploadDate);
                mBlogTitleList.add(mBlogTitle);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView

            RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.act_recyclerview);

            FetchAdapter mDataAdapter = new FetchAdapter(MainActivity.this, mBlogTitleList, mAuthorNameList, mBlogUploadDateList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }
    }
}
