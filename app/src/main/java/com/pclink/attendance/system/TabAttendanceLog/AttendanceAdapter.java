package com.pclink.attendance.system.TabAttendanceLog;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pclink.attendance.system.R;

import java.util.List;


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.myViewHolder> {

    Context mContext;
    List<itemCardViewAttendance> mData;


    public AttendanceAdapter(Context mContext, List<itemCardViewAttendance> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View viewIt;
          viewIt=LayoutInflater.from(mContext).inflate(R.layout.card_item_attendance,parent,false);
        myViewHolder vHolder = new myViewHolder(viewIt);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.img_iconAtt.setImageResource(mData.get(position).getMiconAtt()); //img icon

        holder.tv_titleAtt.setText(mData.get(position).getTitleAtt());  // title

        holder.tv_dateAtt.setText(mData.get(position).getDateAtt());
        holder.tv_timeAtt.setText(mData.get(position).getTimeAtt());
        holder.tv_locationAtt.setText(mData.get(position).getLocationAtt());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    public class myViewHolder extends  RecyclerView.ViewHolder
    {
        private TextView tv_titleAtt,tv_dateAtt,tv_locationAtt,tv_timeAtt;
        private  ImageView img_iconAtt;

        public myViewHolder(View itemView)
        {
            super(itemView);
            tv_titleAtt=itemView.findViewById(R.id.title_textView_cardView);
            tv_dateAtt=itemView.findViewById(R.id.date_textView_cardView);
            tv_locationAtt=itemView.findViewById(R.id.location_textView_cardView);
            tv_timeAtt=itemView.findViewById(R.id.time_textView_cardView);
            img_iconAtt=itemView.findViewById(R.id.imgIcon_cardView);

        }
    }


}
