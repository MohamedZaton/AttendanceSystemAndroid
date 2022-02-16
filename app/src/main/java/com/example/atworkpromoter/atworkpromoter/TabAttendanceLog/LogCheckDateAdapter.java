package com.pclink.attendance.system.TabAttendanceLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.Models.LogCheckdataModel;
import com.pclink.attendance.system.R;

import java.util.List;

public class LogCheckDateAdapter  extends RecyclerView.Adapter<LogCheckDateAdapter.ViewHolder> {
    private List<LogCheckdataModel> ReqList;
    private Context context;



    public LogCheckDateAdapter(List<LogCheckdataModel> reqList, Context context) {
        ReqList = reqList;
        this.context = context;

    }

    @Override
    public LogCheckDateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_attendance, parent, false);

        LogCheckDateAdapter.ViewHolder viewHolder = new LogCheckDateAdapter.ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LogCheckDateAdapter.ViewHolder holder, int position) {
        LogCheckdataModel logCheckdataModel = ReqList.get(position);
            String missStr = logCheckdataModel.getStillThereStatus();
        Log.i("MsStill" ,missStr);

        if (Integer.parseInt(logCheckdataModel.getCheckType())==5 && missStr.equals("Missed")) {
            holder.tv_locationAtt.setText(Html.fromHtml("Status : " + "<font color=red>" + "Missed" + "</font><br><br>"));

        } else {
            if (Integer.parseInt(logCheckdataModel.getCheckType())==5)
            {
                holder.tv_locationAtt.setText(Html.fromHtml("Status : " + "<font color=green>" + "Checked" + "</font><br><br>"));
            }
            else
            {
                holder.tv_locationAtt.setText("");
            }

        }
        holder.img_iconAtt.setImageResource(iconCheck(Integer.parseInt(logCheckdataModel.getCheckType()),false)); //img icon
        holder.tv_titleAtt.setText(titleCheck(Integer.parseInt(logCheckdataModel.getCheckType()),false));  // title

        holder.tv_dateAtt.setText("Date    : " + logCheckdataModel.getCheckDate());
        holder.tv_timeAtt.setText("Time   : " + logCheckdataModel.getCheckTime());
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

    String titleCheck (int checkType , boolean isMissedStill)
    {
        int[] iconsActive = DataConstant.navIconsActive;
        int[] title = DataConstant.navLabels ;
        switch (checkType)
        {
            case 1:
                return "Check in ";

            case 2:
                return "Check out ";
            case 3:
                return "Break in ";
            case 4:
                return "Break out";
            case 5:
                if(isMissedStill) {   // if missed
                    return "Missed still there";
                }
                    return "Still there";

            default:
                return "Check Activity";

        }


    }

    int iconCheck (int checkType , boolean isMissedStill )
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

            case  5 :
                if(isMissedStill)  // miss still there
                {
                    return iconsActive[2];
                  //  return R.drawable.ic_missed_still_64dp;

                }
                return iconsActive[2];
            default:
            return iconsActive[2];

        }




    }


}
