package com.mobiquel.urbanclap.adapter;

/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mobiquel.urbanclap.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final ArrayList<String> filteredList = new ArrayList<>();
    private final Context context;

    private ArrayList<String> selectedTags = new ArrayList<>();
    private JSONArray productList,getProductArray;
    private String getProductIds;

    public ProductAdapter(Context context,JSONArray productList,String getProductIds ) {
        this.context = context;
        this.productList=productList;
        this.getProductIds=getProductIds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_tags, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try
        {
            holder.text.setText(productList.getJSONObject(position).getString("name"));

            if(getProductIds!=null)
            {
                if(ifProductIdMapped(getProductIds,productList.getJSONObject(position).getString("productId")))
                {
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.bakground));
                    holder.text.setTextColor(Color.parseColor("#FFFFFF"));
                    selectedTags.add(productList.getJSONObject(position).getString("productId"));
                }
                else
                {
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.tag_background));
                    holder.text.setTextColor(Color.parseColor("#000000"));
                    if(selectedTags.contains(productList.getJSONObject(position).getString("productId")))
                    {
                        selectedTags.remove(productList.getJSONObject(position).getString("productId"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public String getSelectedProduct(){
        return selectedTags.toString();
    }

    @Override
    public int getItemCount() {
        return productList.length();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private TextView text;

        public ViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            text = (TextView) itemView.findViewById(R.id.text);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if (selectedTags.contains(productList.getJSONObject(getLayoutPosition()).getString("productId")))
                        {
                            selectedTags.remove(productList.getJSONObject(getLayoutPosition()).getString("productId"));
                           cardView.setCardBackgroundColor(context.getResources().getColor(R.color.tag_background));
                            text.setTextColor(Color.parseColor("#000000"));
                        }
                        else
                        {
                            try
                            {
                                selectedTags.add(productList.getJSONObject(getLayoutPosition()).getString("productId"));
                                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.bakground));
                                text.setTextColor(Color.parseColor("#FFFFFF"));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //notifyDataSetChanged();
                }
            });

        }
    }
    private boolean ifProductIdMapped(String materialIds,String itemId)
    {
        String [] mappedIdArray = materialIds.split(",");
        Log.e("MAPPED_SIZE","== "+mappedIdArray.length);

        for(int i=0;i<mappedIdArray.length;i++)
        {
            Log.e("MAPPED_VALE","== "+mappedIdArray[i]);
            if(itemId.equals(mappedIdArray[i]))
            {
                return true;
            }
        }
        return false;

    }

}