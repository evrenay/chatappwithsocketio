package com.example.evren.chatappwithsocketio;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EVREN on 15.10.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    Context context;
    ArrayList<Message> messageArrayList;

    public MessageAdapter(ArrayList<Message> messageArrayList, Context context){
        this.messageArrayList = messageArrayList;
        this.context=context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_message,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        holder.message.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView message;

        public ViewHolder(View itemView){
            super(itemView);
            message=itemView.findViewById(R.id.message_txt);
        }
    }


}
