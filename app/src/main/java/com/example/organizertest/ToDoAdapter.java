package com.example.organizertest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;
    private TextToSpeech tts;

    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> todoList) {
        this.todoList = todoList;
        activity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(Variables.isInclusive()) {
            view = LayoutInflater.from(activity).inflate(R.layout.each_task_inclusive, parent, false);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.each_task, parent, false);
        }

        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

    public void deleteTask(int position) {
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext() {
        return activity;
    }

    public void editTask(int position) {
        ToDoModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", toDoModel.getTask());
        bundle.putString("due", toDoModel.getDue());
        bundle.putString("id", toDoModel.TaskId);
        bundle.putString("destination", toDoModel.getDestination());
        bundle.putString("sTime", toDoModel.getsTime());
        bundle.putString("eTime", toDoModel.geteTime());

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDoModel toDoModel = todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    tts.setLanguage(Locale.ENGLISH);
            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Variables.isInclusive()) {
                    String text = holder.mCheckBox.getText().toString();
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



        if(!toDoModel.getDue().isEmpty())
            holder.mDueDateTv.setText(toDoModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        if(!toDoModel.getDestination().isEmpty()) {
            holder.mNavigation.setVisibility(View.VISIBLE);
            holder.destinationTv.setText(toDoModel.getDestination());
        }

        if(!toDoModel.getsTime().isEmpty()){
            if(!toDoModel.geteTime().isEmpty()){
                holder.timeTv.setText(toDoModel.getsTime() + " - " + toDoModel.geteTime());
            }
            else {
                holder.timeTv.setText(toDoModel.getsTime());
            }
        }

        checkPaintStatus(holder);

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 1);
                    checkPaintStatus(holder);
                } else {
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                    checkPaintStatus(holder);
                }
            }
        });

        checkStatus(toDoModel, holder);

        holder.mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sSource = "";
                String sDestination = toDoModel.getDestination();
                DisplayTrack(sSource, sDestination);
            }
        });

        holder.mCheckBox.setText(toDoModel.getTask());

        if(!toDoModel.getDue().isEmpty())
            holder.mDueDateTv.setText(toDoModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        if(!toDoModel.getDestination().isEmpty()) {
            holder.mNavigation.setVisibility(View.VISIBLE);
            holder.destinationTv.setText(toDoModel.getDestination());
        }

        if(!toDoModel.getsTime().isEmpty()){
            if(!toDoModel.geteTime().isEmpty()){
                holder.timeTv.setText(toDoModel.getsTime() + " - " + toDoModel.geteTime());
            }
            else {
                holder.timeTv.setText(toDoModel.getsTime());
            }
        }

    }

    private void checkStatus(ToDoModel toDoModel, MyViewHolder holder) {
        if(toDoModel.getStatus() == 1) {
            holder.mCheckBox.setChecked(true);
        }
    }


    private void DisplayTrack(String sSource, String sDestination) {
        try {
            Uri uri = Uri.parse("http://www.google.co.in/maps/dir/" + sSource + "/"
                    + sDestination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }



    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void checkPaintStatus (@NonNull MyViewHolder holder) {
        if(holder.mCheckBox.isChecked()){
            holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDueDateTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.destinationTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.timeTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mNavigation.setEnabled(false);
            if(!Variables.isInclusive()) {
                holder.mCheckBox.setTextColor(Color.LTGRAY);
                holder.destinationTv.setTextColor(Color.LTGRAY);
                holder.timeTv.setTextColor(Color.LTGRAY);
                holder.mDueDateTv.setTextColor(Color.LTGRAY);
                holder.mNavigation.setColorFilter(Color.LTGRAY);
            }
        }
        if(((!holder.mCheckBox.isChecked()) && holder.mCheckBox.getPaintFlags() != 0)){
            holder.mCheckBox.setPaintFlags(0);
            holder.mDueDateTv.setPaintFlags(0);
            holder.destinationTv.setPaintFlags(0);
            holder.timeTv.setPaintFlags(0);
            holder.mNavigation.setEnabled(true);
            if(!Variables.isInclusive()) {
                holder.mCheckBox.setTextColor(Color.BLACK);
                holder.destinationTv.setTextColor(Color.BLACK);
                holder.timeTv.setTextColor(Color.BLACK);
                holder.mDueDateTv.setTextColor(Color.BLACK);
                holder.mNavigation.setColorFilter(Color.BLACK);
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTv;
        TextView timeTv;
        TextView destinationTv;
        CheckBox mCheckBox;
        ImageView mNavigation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            mNavigation = itemView.findViewById(R.id.navigation);
            timeTv = itemView.findViewById(R.id.time_tv);
            destinationTv = itemView.findViewById(R.id.destination_tv);
        }
    }

}
