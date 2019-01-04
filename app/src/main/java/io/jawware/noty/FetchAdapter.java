package io.jawware.noty;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FetchAdapter extends RecyclerView.Adapter<FetchAdapter.MyViewHolder> {

    private ArrayList<String> mBlogTitleList = new ArrayList<>();
    private ArrayList<String> mAuthorNameList = new ArrayList<>();
    private ArrayList<String> mBlogUploadDateList = new ArrayList<>();
    private Activity mActivity;
    private int lastPosition = -1;

    public FetchAdapter(Activity activity, ArrayList<String> mBlogTitleList, ArrayList<String> mAuthorNameList, ArrayList<String> mBlogUploadDateList) {
        this.mActivity = activity;
        this.mBlogTitleList = mBlogTitleList;
        this.mAuthorNameList = mAuthorNameList;
        this.mBlogUploadDateList = mBlogUploadDateList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView fetchDataView;
        private LinearLayout viewContainer;

        public MyViewHolder(View view) {
            super(view);
            fetchDataView = (TextView) view.findViewById(R.id.fetch_data);
            viewContainer = (LinearLayout) view.findViewById(R.id.datacontainer);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.fetchDataView.setText(mAuthorNameList.get(position));

        holder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempData = mBlogUploadDateList.get(position);
                Intent intent = new Intent(mActivity, Web.class);
                intent.putExtra("EXTRA_SESSION_ID", tempData);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlogTitleList.size();
    }
}