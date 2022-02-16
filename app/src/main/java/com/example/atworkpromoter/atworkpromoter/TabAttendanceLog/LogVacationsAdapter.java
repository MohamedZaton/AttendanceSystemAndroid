package com.pclink.attendance.system.TabAttendanceLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.Models.LogVacationsModel;
import com.pclink.attendance.system.R;

import java.util.List;

public class LogVacationsAdapter  extends  RecyclerView.Adapter<LogVacationsAdapter.ViewHolder> {
    private List<LogVacationsModel> ReqList;
    private Context context;

    public LogVacationsAdapter(List<LogVacationsModel> reqList, Context context) {
        ReqList = reqList;
        this.context = context;
    }

    @NonNull
    @Override
    public LogVacationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_attendance, parent, false);

        LogVacationsAdapter.ViewHolder viewHolder = new LogVacationsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LogVacationsAdapter.ViewHolder holder, int position) {
        LogVacationsModel logVacationsModel = ReqList.get(position);


        holder.img_iconAtt.setImageResource(R.drawable.ic_assignment_blue_24dp); //img icon

        holder.tv_titleAtt.setText("Vacation Request");  // title
        String timeFrom[] = logVacationsModel.getDateFrom().split("T");
        String timeTo[] = logVacationsModel.getDateTo().split("T");


        holder.tv_dateAtt.setText("Date From: "  + timeFrom[0]);
        holder.tv_timeAtt.setText("Date To   :"  + timeTo[0]);
        holder.tv_locationAtt.setText("Status    :" + logVacationsModel.getStatus());



    }

    @Override
    public int getItemCount() {
        return ReqList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_titleAtt,tv_dateAtt,tv_locationAtt,tv_timeAtt;
        private ImageView img_iconAtt;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tv_titleAtt=itemView.findViewById(R.id.title_textView_cardView);
            tv_dateAtt=itemView.findViewById(R.id.date_textView_cardView);
            tv_locationAtt=itemView.findViewById(R.id.location_textView_cardView);
            tv_timeAtt=itemView.findViewById(R.id.time_textView_cardView);
            img_iconAtt=itemView.findViewById(R.id.imgIcon_cardView);

        }

        @Override
        public void onClick(View v) {
        }
    }

    String titleCheck (int checkType)
    {
        int[] iconsActive = DataConstant.navIconsActive;
        int[] title = DataConstant.navLabels ;
        switch (checkType)
        {
            case 1 :
                return "Roster in " ;

            case 2:
                return "Roster out " ;


            case 3:
                return "Break in " ;

            case 4:
                return "Break out" ;


            default:
                return "Check Task" ;


        }


    }

    int iconCheck (int checkType)
    {
        int[] iconsActive = DataConstant.navIconsActive;
        int[] title = DataConstant.navLabels ;
        switch (checkType)
        {
            case 1 :
                return iconsActive[0] ;

            case 2:
                return iconsActive[0] ;


            case 3:
                return iconsActive[1] ;

            case 4:
                return iconsActive[1] ;


            default:
                return iconsActive[2];

        }


    }
}
