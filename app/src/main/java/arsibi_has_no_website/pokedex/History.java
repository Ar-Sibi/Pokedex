package arsibi_has_no_website.pokedex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            File f = new File(getCacheDir(),"history.txt");
            f.createNewFile();
            {
                ObjectOutputStream outputStream =new ObjectOutputStream(new FileOutputStream(f));
                outputStream.writeObject(History.historylist);
            }
        }catch (IOException e){Log.d("MOO",e.toString());}
    }

    public void clearHistory(View v){
        History.historylist.clear();
        adapter.notifyDataSetChanged();
    }
}