package com.pclink.attendance.system.Report;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.R;

import java.util.List;

public class CompetitiveAdapter extends RecyclerView.Adapter<CompetitiveAdapter.myViewHolder> {

        Context mContext;
        List<itemCardReport> mData;

        SharedPrefData sharedPrefData ;



    public CompetitiveAdapter(Context mContext ,List<itemCardReport> mData) {
        this.mContext = mContext;
        this.mData = mData;
        sharedPrefData = new  SharedPrefData(mContext);

    }



@NonNull
@Override
public CompetitiveAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewIt;
        viewIt= LayoutInflater.from(mContext).inflate(R.layout.card_item_report_form,parent,false);
        CompetitiveAdapter.myViewHolder vHolder = new CompetitiveAdapter.myViewHolder(viewIt);
        return vHolder;
        }

@Override
public void onBindViewHolder(@NonNull CompetitiveAdapter.myViewHolder holder, int position) {


        holder.tv_titlePtt.setText(mData.get(position).getItemName());  // title

        holder.tv_pricePtt.setText(mData.get(position).getItemPrice());
        holder.tv_countPtt.setText(mData.get(position).getItemCount());
        holder.tv_skuPtt.setText(mData.get(position).getItemSkuCode());

    boolean isPriceHdn = sharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.priceCompHdn);
    boolean isCountHdn = sharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.countCompHdn);

    if(isPriceHdn){
        holder.tv_priceRowLlyt.setVisibility(View.GONE);
    }
    if(isCountHdn){
        holder.tv_countRowLlyt.setVisibility(View.GONE);
    }



    //mData.get(position).getItemID()

        }

@Override
public int getItemCount()
        {
        return mData.size();
        }




public class myViewHolder extends  RecyclerView.ViewHolder
{
    private TextView tv_titlePtt,tv_pricePtt,tv_skuPtt,tv_countPtt;
    private LinearLayout tv_priceRowLlyt , tv_countRowLlyt;

    public myViewHolder(View itemView)
    {
        super(itemView);
        tv_titlePtt=itemView.findViewById(R.id.name_item_textview_cardView);
        tv_pricePtt=itemView.findViewById(R.id.price_item_textview);
        tv_countPtt = itemView.findViewById(R.id.count_items_textview);
        tv_skuPtt=itemView.findViewById(R.id.sku_code_Textview);
        tv_priceRowLlyt = itemView.findViewById(R.id.price_row_lyt);
        tv_countRowLlyt = itemView.findViewById(R.id.count_row_lyt);

    }
}


}
