package com.pclink.attendance.system.TabRequest;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.R;

import java.util.ArrayList;
import java.util.List;

class ExuseListAdapter  extends RecyclerView.Adapter<ExuseListAdapter.myViewHolder> {
    Context mContext;
    List<itemCardViewExcuseList> mData;
    SharedPrefData sharedPrefData;
    String namefile = DataConstant.promoterDataNameSpFile;
    int selectedItem =-22;
    List<CardView>cardViewList = new ArrayList<>();

    public ExuseListAdapter(Context mContext, List<itemCardViewExcuseList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        sharedPrefData = new SharedPrefData(this.mContext);
    }



    @NonNull
    @Override
    public ExuseListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewIt;
        viewIt=LayoutInflater.from(mContext).inflate(R.layout.card_item_excuse_list,parent,false);


        ExuseListAdapter.myViewHolder vHolder = new ExuseListAdapter.myViewHolder(viewIt);


        return vHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ExuseListAdapter.myViewHolder holder, final int position) {

        if (!cardViewList.contains(holder.excuse_cardview))
        {
            cardViewList.add(holder.excuse_cardview);
        }
        holder.reason_textview.setText(mData.get(position).getExuseTxt());



            holder.excuse_cardview.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    for(CardView cardView : cardViewList)
                    {
                        sharedPrefData.putElement(namefile,DataConstant.saveExcuseIdjson, mData.get(position).getExId());
                        sharedPrefData.putElement(namefile,DataConstant.saveExcuseTxtjson, mData.get(position).getExuseTxt());

                        cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                    }
                    //The selected card is set to colorSelected
                    holder.excuse_cardview.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorStillThere));
                }
            });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    public class myViewHolder extends  RecyclerView.ViewHolder
    {

        public  View  viewitemX ;
        private TextView reason_textview;
        private CardView excuse_cardview;
        public myViewHolder(View itemView)
        {
            super(itemView);
            viewitemX =itemView;

            reason_textview=itemView.findViewById(R.id.reason_textview);
            excuse_cardview = itemView.findViewById(R.id.excuse_cardView);



        }
    }


}
