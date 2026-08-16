package com.example.apitest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apitest.databinding.ActivityMainBinding;
import com.example.apitest.databinding.ItemMainBinding;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "apitest";
    private static final String KEY = "12808f33bce443cbb6e8742300db323c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainList, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.mainList.setLayoutManager(new LinearLayoutManager(this));


        RetrofitService networkService = RetrofitFactory.create();
        networkService.getList(KEY, "json", 1, 100, null)
                .enqueue(new Callback<PageListModel>() {
                    @Override
                    public void onResponse(Call<PageListModel> call, Response<PageListModel> response) {
                        PageListModel body = response.body();
                        List<ItemModel> rows = body == null ? null : body.findRows();
                        if (response.isSuccessful() && rows != null) {
                            Log.d(TAG, "loaded " + rows.size() + " / total " + body.totalCount());
                            binding.mainList.setAdapter(new MyAdapter(rows));
                            return;
                        }
                        // 이 API 는 에러·무데이터도 HTTP 200 + RESULT 봉투로 준다
                        ResultModel result = body == null ? null : body.findResult();
                        String message = result == null
                                ? "HTTP " + response.code()
                                : result.CODE + " " + result.MESSAGE;
                        Log.e(TAG, "api error: " + message);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<PageListModel> call, Throwable t) {
                        Log.e(TAG, "network failure", t);
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemMainBinding binding;

        public ItemViewHolder(ItemMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        List<ItemModel> rows;

        public MyAdapter(List<ItemModel> rows) {
            this.rows = rows;
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMainBinding binding = ItemMainBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            ItemModel item = rows.get(position);
            holder.binding.itemTitle.setText(item.LOC_INFO);
            holder.binding.itemSub.setText(String.format(Locale.KOREA, "%s · %s년 · %s",
                    item.SIGUN_NM, item.ACDNT_YY, item.ACDNT_DIV_NM));
            holder.binding.itemDesc.setText(String.format(Locale.KOREA,
                    "발생 %d건 · 사망 %d · 중상 %d · 경상 %d",
                    item.OCCUR_CNT, item.DPRS_CNT, item.SERINJRY_INDVDL_CNT, item.SLTINJRY_INDVDL_CNT));
            holder.binding.itemCoord.setText(String.format(Locale.KOREA,
                    "위도 %.6f · 경도 %.6f", item.LAT, item.LOGT));
        }
    }
}
