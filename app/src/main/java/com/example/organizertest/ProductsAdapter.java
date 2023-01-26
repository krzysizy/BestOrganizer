package com.example.organizertest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Tasks> tasksList;

    public ProductsAdapter(Context mCtx, List<Tasks> tasksList) {
        this.mCtx = mCtx;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_task, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Tasks task = tasksList.get(position);

        holder.textViewText.setText(task.getText());
//        holder.textViewBrand.setText(task.getBrand());
//        holder.textViewDesc.setText(task.getDescription());
//        holder.textViewPrice.setText("INR " + task.getPrice());
//        holder.textViewQty.setText("Available Units: " + task.getQty());
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewText;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.textview_text);
//            textViewBrand = itemView.findViewById(R.id.textview_brand);
//            textViewDesc = itemView.findViewById(R.id.textview_desc);
//            textViewPrice = itemView.findViewById(R.id.textview_price);
//            textViewQty = itemView.findViewById(R.id.textview_quantity);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Tasks task = tasksList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdateTaskActivity.class);
            intent.putExtra("product", product);
            mCtx.startActivity(intent);
        }
    }
}
