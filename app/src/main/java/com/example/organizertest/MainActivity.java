package com.example.organizertest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText add_task;
    private Button submitBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add_task = findViewById(R.id.add_task_edit);
        submitBtn = findViewById(R.id.submit_btn);
        //FireStore
        db = FirebaseFirestore.getInstance();
        findViewById(R.id.submit_btn).setOnClickListener(this);

    }

    private void saveProduct() {
        Tasks tasks = new Tasks(add_task.getText().toString());
        Map<String, Object> task = new HashMap<>();
        if (tasks.getText().matches("")) {
            Toast.makeText(this, "You can't add empty task!", Toast.LENGTH_SHORT).show();
            return;
        }
        task.put("text", tasks.getText());
        db.collection("task")
                .add(task).addOnSuccessListener(suc -> {
                    Toast.makeText(this, "Task successfully added", Toast.LENGTH_SHORT).show();
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