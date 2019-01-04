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

public class Main4Activity extends AppCompatActivity {

    private RecyclerView viewTwitter;
    private Button goBack;

    private ProgressDialog mProgressDialog;
    private String url;
    private ArrayList<String> mResponseTwitter = new ArrayList<>();
    private ArrayList<String> mResponseTwitterList = new ArrayList<>();
    private ArrayList<String> mReponseTempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);


        viewTwitter = (RecyclerView) findViewById(R.id.act_recyclerviewtwit);
        goBack = (Button) findViewById(R.id.returnbacktwit);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main4Activity.this,Dashboard.class));
            }
        });

        url = "http://tweeplers.com/hashtags/?cc=IN";
//        have a chance to change counrty over another
        new Description().execute();
    }

        @Override
        protected void onStart() {
            super.onStart();

        }


        @SuppressLint("StaticFieldLeak")
        private class Description extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(Main4Activity.this);
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
                    Toast.makeText(Main4Activity.this, "Error in response", Toast.LENGTH_SHORT).show();
                }
                // Locate the content attribute
                int mElementSize = 0;
                if (mElementDataSize != null) {
                    mElementSize = mElementDataSize.size();
                }

                for (int i = 0; i < 3; i++) {
                    Elements mElementAuthorName = mBlogDocument.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
                    if(mElementAuthorName == null){
                        Toast.makeText(Main4Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    String mTrendingTitle1 = mElementAuthorName.text();

                    Elements mElementTrendHref1 = mBlogDocument.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=row]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
                    String mTrendHref1 = mElementTrendHref1.attr("abs:href");

//                    Elements mElementTitle2 = mBlogDocument.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
//                    String mTrendingTitle2 = mElementTitle2.text();
//
//                    Elements mElementTrendHref2 = mBlogDocument.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
//                    String mTrendHref2 = mElementTrendHref2.attr("abs:href");


                    mResponseTwitter.add(mTrendingTitle1);
                    mResponseTwitterList.add(mTrendHref1);
                    mReponseTempList.add(mTrendHref1);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Set description into TextView


                FetchAdapter mDataAdapter = new FetchAdapter(Main4Activity.this, mReponseTempList, mResponseTwitter, mResponseTwitterList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                viewTwitter.setLayoutManager(mLayoutManager);
                viewTwitter.setAdapter(mDataAdapter);

                mProgressDialog.dismiss();
            }
        }
    }