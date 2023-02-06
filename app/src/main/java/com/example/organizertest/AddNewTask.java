package com.example.organizertest;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView setDate;
    private EditText mTaskEdit;
    private Button mSaveBtn;
    private FirebaseFirestore firestore;
    private Context context;
    private String dueDate = "";
    private String sTime = "";
    private String eTime = "";
    private String id = "";
    private String destination = "";
    private TextView setDestination;
    private TextView setStartTime;
    private TextView setEndTime;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    CharSequence searchAddress;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_new_task, container);

        //set to adjust screen height automatically, when soft keyboard appears on screen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDate = view.findViewById(R.id.set_date_tv);
        mTaskEdit = view.findViewById(R.id.task_edittext);
        mSaveBtn = view.findViewById(R.id.save_btn);
        setDestination = view.findViewById(R.id.set_loc_tv);
        setStartTime = view.findViewById(R.id.set_start_time);
        setEndTime = view.findViewById(R.id.set_end_time);
        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            String dueDateUpdate = bundle.getString("due");
            String sTimeUpdate = bundle.getString("sTime");
            String eTimeUpdate = bundle.getString("eTime");
            String destinationUpdate = bundle.getString("destination");

            mTaskEdit.setText(task);
            if (!(dueDateUpdate.equals("")))
            setDate.setText(dueDateUpdate);
//            if (!(sTimeUpdate.equals("")))
            setStartTime.setText(sTimeUpdate);
//            if (!(eTimeUpdate.equals("")))
            setEndTime.setText(eTimeUpdate);
//            if (!(destinationUpdate.equals("")))
            setDestination.setText(destinationUpdate);
        }

        if (mTaskEdit.length() <= 0){
            mSaveBtn.setEnabled(false);
            mSaveBtn.setBackgroundColor(Color.GRAY);
            mSaveBtn.setTextColor(Color.WHITE);
        } else {
            mSaveBtn.setEnabled(true);
            mSaveBtn.setBackgroundColor(getResources().getColor(R.color.purple));
            mSaveBtn.setTextColor(Color.WHITE);
        }

        if(isServicesOK()){
            init();
        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (s.toString().equals("")){
                   mSaveBtn.setEnabled(false);
                   mSaveBtn.setBackgroundColor(Color.GRAY);
                   mSaveBtn.setTextColor(Color.WHITE);
               }else{
                   mSaveBtn.setEnabled(true);
                   mSaveBtn.setBackgroundColor(getResources().getColor(R.color.purple));
                   mSaveBtn.setTextColor(Color.WHITE);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        setDate.setText(dayOfMonth + "/" + month + "/" + year);
                        dueDate = dayOfMonth + "/" + month +"/"+year;

                    }
                } , YEAR , MONTH , DAY);

                datePickerDialog.show();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                setStartTime.setText(hourOfDay + ":" + minute);
                                setEndTime.setVisibility(View.VISIBLE);
                                sTime = hourOfDay + ":" + minute;
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE) + 1;

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                setEndTime.setText(hourOfDay + ":" + minute);
                                eTime = hourOfDay + ":" + minute;
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });




        boolean finalIsUpdate = isUpdate;
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String task = mTaskEdit.getText().toString();

                if (finalIsUpdate){
                    firestore.collection("task").document(id).update("task" , task , "due" , dueDate, "sTime", sTime, "eTime", eTime, "destination", destination);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (task.isEmpty()) {
                        Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show();
                    } else {

                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("sTime", sTime);
                        taskMap.put("eTime", eTime);
                        taskMap.put("destination",destination);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());

                        firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }

    private void init(){
        setDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddress = setDestination.getText();
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("address", searchAddress);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof  OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Toast.makeText(context, "Internet", Toast.LENGTH_SHORT).show();
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AddNewTask.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(context, "You can't make map requests", Toast.LENGTH_SHORT).show();
            setDestination.setEnabled(false);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1) {
            if(data != null) {
                setDestination.setText(data.getStringExtra("address"));
                destination = data.getStringExtra("address");
            }
        }
    }
}
