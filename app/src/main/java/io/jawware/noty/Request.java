package io.jawware.noty;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class Request extends AsyncTask<Void, Void, Void> {

    private FirebaseAuth mServiceAuth;
    private Context mContext = NotyApplication.getAppContext();
    private DatabaseReference mServiceDatabase;
    private String url = "http://tweeplers.com/hashtags/?cc=IN";
    private String url1 = "https://timesofindia.indiatimes.com/sports/cricket";
    private String url2 = "https://timesofindia.indiatimes.com/business/india-business/markets";
    private String mFetchResponse="default_value",mCricketResponse="default_value",mShareMarketResponse="default_value";

    private String serviceid;

//    SharedPreferences sharedPreferences = mContext.getSharedPreferences("request_data", MODE_PRIVATE);

    @Override
    protected Void doInBackground(Void... voids) {

        serviceid = mServiceAuth.getCurrentUser().getUid();


        // Connect to the web site
        Document mResponseDocument1 = null,mResponseDocument2=null,mResponseDocument3=null;
        try {
            mResponseDocument1 = Jsoup.connect(url).get();
            mResponseDocument2 = Jsoup.connect(url1).get();
            mResponseDocument3 = Jsoup.connect(url2).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
//9 : 27 pm -first trigger
//


//            Elements mElementAuthorName = null;
//            if (mBlogDocument != null) {
////            mElementAuthorName = mBlogDocument.select("div[class=top-newslist small]").select("div").select("ul[class=cvs_wdt clearfix]").select("li").select("span[class=w_tle]").select("a");
//
//                mElementAuthorName = mBlogDocument.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
        for(int i =0 ;i<1;i++) {
            Elements mElementTrendHref1 = null;
            if (mResponseDocument1 != null) {
                mElementTrendHref1 = mResponseDocument1.select("div[class=col-md-7]").select("div[class=panel panel-primary]").select("div[class=row]").select("div[class=col-xs-8 wordwrap]").select("a").eq(i);
            }


            if (mElementTrendHref1 != null) {
                mFetchResponse = mElementTrendHref1.text();
            }
        }

        for (int i = 0; i < 1; i++) {
            Elements mElementAuthorName = null;
            if (mResponseDocument2 != null) {
                mElementAuthorName = mResponseDocument2.select("div[class=top-newslist small]").select("div").select("ul[class=cvs_wdt clearfix]").select("li").select("span[class=w_tle]").select("a").eq(i);
            }
//                Elements mElementAuthorName = mBlogDocument.select("a[href]");
            if(mElementAuthorName!=null) {
                mCricketResponse = mElementAuthorName.text();
            }


        }

        for (int i = 0; i < 1; i++) {
            Elements mElementAuthorName = null;
            if (mResponseDocument3 != null) {
                mElementAuthorName = mResponseDocument3.select("div[class=tab-content]").select("ul").select("li").select("a").eq(i);
            }
//                Elements mElementAuthorName = mBlogDocument.select("a[href]");

            if(mElementAuthorName!=null){
                mShareMarketResponse = mElementAuthorName.text();
            }





        }

            return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        changeRequest();

        mServiceAuth = FirebaseAuth.getInstance();
        mServiceDatabase = FirebaseDatabase.getInstance().getReference("service");

    }

    @Override
    protected void onPostExecute(Void result) {

        mServiceDatabase.child(serviceid).child("share_market").child("response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checkResponse = dataSnapshot.getValue(String.class);

                if(checkResponse!=null){

                }else{
                    checkResponse = "no_updates";
                }
                if (checkResponse.equals(mShareMarketResponse)) {
                    mServiceDatabase.child(serviceid).child("share_market").child("response").setValue(checkResponse);

                } else {
                    showNotification("Noty Me", "Share Market Updates","share_market");
                    mServiceDatabase.child(serviceid).child("share_market").child("response").setValue(mShareMarketResponse);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mServiceDatabase.child(serviceid).child("cricket").child("response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checkResponse = dataSnapshot.getValue(String.class);

                if(checkResponse!=null){

                }else{
                    checkResponse = "no_updates";
                }
                if (checkResponse.equals(mFetchResponse)) {
//                    showNotification("Noty User","No Updates");
                    mServiceDatabase.child(serviceid).child("cricket").child("response").setValue(checkResponse);

                } else {
                    showNotification("Noty Me", "Cricket Updates","cricket");
                    mServiceDatabase.child(serviceid).child("cricket").child("response").setValue(mCricketResponse);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mServiceDatabase.child(serviceid).child("twitter_trending").child("response").child("top1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checkResponse = dataSnapshot.getValue(String.class);

                if(checkResponse!=null){

                }else{
                    checkResponse = "no_updates1";
                }
                    if (checkResponse.equals(mFetchResponse)) {
//                    showNotification("Noty User","No Updates");
                        mServiceDatabase.child(serviceid).child("twitter_trending").child("response").child("top1").setValue(checkResponse);

                    } else {
                        showNotification("Noty Me", "Twitter Updates","twitter");
                        mServiceDatabase.child(serviceid).child("twitter_trending").child("response").child("top1").setValue(mFetchResponse);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    void showNotification(String title, String content,String notyType) {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }else{
//                Toast.makeText(mContext, "Channel error", Toast.LENGTH_SHORT).show();
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click

        if(notyType.equals("cricket")){
            Intent intent = new Intent(mContext, Main2Activity.class);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(0, mBuilder.build());
        }else if(notyType.equals("share_market")){
            Intent intent = new Intent(mContext, Main2Activity.class);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(0, mBuilder.build());

        }else if(notyType.equals("twitter")){
            Intent intent = new Intent(mContext, Main4Activity.class);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(0, mBuilder.build());

        }


    }
}

