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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 28-06-2017.
 */

public class History extends AppCompatActivity {
    RecyclerView recyclerView;
    static List<RViewItems> historylist= new ArrayList<>();
    static Map<String,String> map = new HashMap<>();
    RViewAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        getSupportActionBar().hide();
        map.put("normal","#a8a77a");
        map.put("fire","#ee8130");
        map.put("water","#6390f0");
        map.put("electric","#f7d02c");
        map.put("grass","#7ac74c");
        map.put("ice","#96d9d6");
        map.put("fighting","#c22e28");
        map.put("poison","#a33ea1");
        map.put("ground","#e2bf65");
        map.put("flying","#a98ff3");
        map.put("psychic","#f95587");
        map.put("bug","#a6b91a");
        map.put("rock","#b6a136");
        map.put("ghost","#735797");
        map.put("dragon","#6f35fc");
        map.put("dark","#705746");
        map.put("steel","#b7b7ce");
        map.put("fairy","#d685ad");
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