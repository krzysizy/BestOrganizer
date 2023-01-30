package com.example.organizertest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText add_task;
    private Button submitBtn;
    private FirebaseFirestore db;
    private TextView dataTV;
    private Button dataPicker;
    private DatePickerDialog.OnDateSetListener setListener;
    private int d;
    private int m;
    private int y;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Task
        add_task = findViewById(R.id.add_task_edit);
        submitBtn = findViewById(R.id.submit_btn);
        //FireStore
        db = FirebaseFirestore.getInstance();
        findViewById(R.id.submit_btn).setOnClickListener(this);
        //Data picker
        dataPicker = findViewById(R.id.dataPicker);
        dataTV = findViewById(R.id.dataTV);
        Calendar calendar = Calendar.getInstance();
        final int year =  calendar.get(Calendar.YEAR);
        final int month =  calendar.get(Calendar.MONTH);
        final int day =  calendar.get(Calendar.DAY_OF_MONTH);

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date =  day+"/"+month+"/"+year;
                dataTV.setText(date);
            }
        };

        dataPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month =  month+1;
                        String date =  day+"/"+month+"/"+year;
                        if (month <= 10) {
                            date =  day+"/"+"0"+month+"/"+year;
                        }
                        if (day <= 10) {
                            date =  "0" + date;
                        }
                        d = day;
                        m = month;
                        y = year;
                        dataTV.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }


    private void saveProduct() {
        Tasks tasks = new Tasks(add_task.getText().toString(),y
                ,m
                ,d);
        Map<String, Object> task = new HashMap<>();
        if (tasks.getText().matches("")) {
            Toast.makeText(this, "You can't add empty task!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tasks.getDay()<1 || tasks.getDay()>31) {
            Toast.makeText(this, "Wrong day format!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tasks.getMonth()<1 || tasks.getMonth()>12) {
            Toast.makeText(this, "Wrong month format!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tasks.getYear()<0 || tasks.getMonth()>3000) {
            Toast.makeText(this, "Wrong year format!", Toast.LENGTH_SHORT).show();
            return;
        }
        task.put("Task", tasks.getText());
        task.put("Year", y);
        task.put("Month", m);
        task.put("Day", d);
        db.collection("task")
                .add(task).addOnSuccessListener(suc -> {
                    Toast.makeText(this, "Task successfully added", Toast.LENGTH_SHORT).show();
                    add_task.setText("");
                })
                .addOnFailureListener(err ->
                {
                    Toast.makeText(this, "Cannot add task to db: "+ err.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.submit_btn:
                saveProduct();
                break;
//            case R.id.textview_view_products:
//                startActivity(new Intent(this, ProductsActivity.class));
//                break;
        }

    }

}