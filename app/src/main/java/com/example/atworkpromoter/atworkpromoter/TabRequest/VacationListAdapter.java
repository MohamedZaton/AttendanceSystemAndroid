package com.pclink.attendance.system.TabRequest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.R;

import java.util.ArrayList;
import java.util.List;

public class VacationListAdapter  extends RecyclerView.Adapter<VacationListAdapter.myViewHolder>  {
    Context mContext;
    List<itemCardViewVacationList> mData;
    SharedPrefData sharedPrefData;
    CardView vacationCardView;
    String namefile = DataConstant.promoterDataNameSpFile;
    int selectedItem =-22;
    List<CardView>cardViewList = new ArrayList<>();

    public VacationListAdapter(Context mContext, List<itemCardViewVacationList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        sharedPrefData = new SharedPrefData(this.mContext);
    }



    @NonNull
    @Override
    public VacationListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewIt;
        viewIt=LayoutInflater.from(mContext).inflate(R.layout.card_item_excuse_list,parent,false);



        VacationListAdapter.myViewHolder vHolder = new VacationListAdapter.myViewHolder(viewIt);



        return vHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        if (!cardViewList.contains(holder.vacation_cardview)) {
            cardViewList.add(holder.vacation_cardview);
        }
        holder.reason_textview.setText(mData.get(position).getVacationTxt());



        holder.vacation_cardview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(CardView cardView : cardViewList){
                    sharedPrefData.putElement(namefile,DataConstant.saveVacationIdjson, mData.get(position).getVacId());
                    sharedPrefData.putElement(namefile,DataConstant.saveVacationTxtjson, mData.get(position).getVacationTxt());

                    cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                }
                //The selected card is set to colorSelected
                holder.vacation_cardview.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorStillThere));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder
    {

        public View viewitemX ;
        private TextView reason_textview;
        private CardView vacation_cardview;
        public myViewHolder(View itemView)
        {
            super(itemView);
            viewitemX =itemView;
            reason_textview=itemView.findViewById(R.id.reason_textview);
            vacation_cardview = itemView.findViewById(R.id.excuse_cardView);
        }
    }
}
