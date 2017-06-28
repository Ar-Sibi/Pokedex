package arsibi_has_no_website.pokedex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hp on 28-06-2017.
 */

public class History extends AppCompatActivity {
    RecyclerView recyclerView;
    static List<RViewItems> historylist= new ArrayList<>();
    RViewAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        getSupportActionBar().hide();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        adapter = new RViewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                History.historylist.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeChanged(0,History.historylist.size());
            }
        };
        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
    public void clearHistory(View v){
        History.historylist.clear();
        adapter.notifyDataSetChanged();
    }
}

