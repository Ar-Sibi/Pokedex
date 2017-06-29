package arsibi_has_no_website.pokedex;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    ListView stats;
    Button getButton;
    AutoCompleteTextView atv;
    TextView nameText;
    StatsAdapter adapter;
    ImageView icon;
    Cacher c;
    boolean imageAvailable=false;
    String path;
    List<StatPair> pairs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadHistory();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        nameText=(TextView)findViewById(R.id.name);
        ArrayAdapter<String> autoadapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, getListForAdapter());
        stats = (ListView) findViewById(R.id.statslist);
        adapter = new StatsAdapter(getApplicationContext(), R.layout.statitem, pairs);
        stats.setAdapter(adapter);
        getButton = (Button) findViewById(R.id.button);
        atv = (AutoCompleteTextView) findViewById(R.id.autotext);
        icon=(ImageView)findViewById(R.id.imageView);
        atv.setAdapter(autoadapter);
    }
    protected void loadHistory() {
        try {
            File f = new File(getCacheDir(),"history.txt");
            if (!f.exists())
                f.createNewFile();
            else{
                ObjectInputStream inputStream =new ObjectInputStream( new FileInputStream(f));
                History.historylist=(ArrayList<RViewItems>)inputStream.readObject();
            }
        }catch (IOException e){Log.d("MOO",e.toString());}catch (ClassNotFoundException e){Log.d("MOO",e.toString());}
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
    public void loadData(View v) {
        getButton.setClickable(false);
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        File file=new File(getCacheDir(),atv.getText().toString().toLowerCase()+".jpg");
        path=atv.getText().toString().toLowerCase()+".jpg";
        imageAvailable=false;
        if(!file.exists()) {
            c = new Cacher(file);
        }
        else {
            imageAvailable = true;
            c=null;
        }
        ImageHandlerTask task = new ImageHandlerTask();
        task.execute("http://pokeapi.co/api/v2/pokemon/" + atv.getText().toString().toLowerCase());
    }

    public List<String> getListForAdapter() {
        List<String> list = new ArrayList<>();
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.pokemonlist));
        String s;
        while (scanner.hasNext()) {
            s = scanner.nextLine();
            list.add(s);
        }
        return list;
    }

    public void openHistory(View v){
        Intent intent = new Intent(this,History.class);
        startActivity(intent);
    }




    public class ImageHandlerTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pairs.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlstring = params[0];
            String stringinput = "";
            URL url;
            InputStream ip;
            try {
                url = new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                ip = new BufferedInputStream(connection.getInputStream());
                stringinput = convertString(ip);
            } catch (MalformedURLException e) {
                cancel(true);
            } catch (IOException e) {
                Log.d("MOO", e.toString());
            }
            if (!isCancelled()) {
                try {
                    JSONObject obj = new JSONObject(stringinput);
                    getAndSetStats(obj);
                    JSONObject sprites = obj.getJSONObject("sprites");
                    String s = sprites.getString("front_default");
                    return s;
                } catch (JSONException e) {
                    Log.d("MOO", e.toString());
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            getButton.setClickable(true);
            super.onPostExecute(s);
        }

        public String convertString(InputStream in) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            return sb.toString();
        }

        public void getAndSetStats(JSONObject json) throws JSONException {
            putName(json);
            if(imageAvailable==true)
                putImage(json);
            putEvolutionChain(json);
            putId(json);
            putWeight(json);
            putHeight(json);
            putTypes(json);
            putMoves(json);
            if(imageAvailable==false)
                putImage(json);

        }

        public void putId(JSONObject json) throws JSONException {
            String s = "Id";
            String id = String.format("%d", json.getInt("id"));
            addPairs(s, id);
        }

        public void putWeight(JSONObject json) throws JSONException {
            String s = "Weight";
            String value = json.getString("weight");
            int weight = Integer.parseInt(value);
            value = String.format("%d.%d kg", weight / 10, weight % 10);
            addPairs(s, value);
        }

        public void putHeight(JSONObject json) throws JSONException {
            String s = "Height";
            String value = json.getString("height");
            int height = Integer.parseInt(value);
            value = String.format("%d.%d m", height / 10, height % 10);
            addPairs(s, value);
        }

        public void putName(JSONObject json) throws JSONException {
            final String str = json.getString("name");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nameText.setText(str.substring(0, 1).toUpperCase() + str.substring(1));
                }
            });
        }

        public void putTypes(JSONObject jso) throws JSONException {
            final JSONObject json=jso;
            JSONArray arr = json.getJSONArray("types");
            String s = "Type";
            String type = "";
            for (int i = arr.length() - 1; i >= 0; i--) {
                type = ((JSONObject) arr.get(i)).getJSONObject("type").getString("name");
                addPairs(s, type.substring(0, 1).toUpperCase() + type.substring(1));
                s = "";
            }
        }

        public void putMoves(JSONObject json) throws JSONException {
            JSONArray arr = json.getJSONArray("moves");
            String s = "Moves";
            String type = "";
            for (int i = arr.length() - 1; i >= 0; i--) {
                type = ((JSONObject) arr.get(i)).getJSONObject("move").getString("name");
                addPairs(s, type.substring(0, 1).toUpperCase() + type.substring(1));
                s = "";
            }
        }

        public void putEvolutionChain(JSONObject json) throws JSONException {
            final String urlstr = json.getJSONObject("species").getString("url");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        URL url = new URL(urlstr);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        String input = convertString(connection.getInputStream());
                        JSONObject jsonObject = new JSONObject(input);
                        String urlstring = jsonObject.getJSONObject("evolution_chain").getString("url");
                        url = new URL(urlstring);
                        HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                        String input2 = convertString(connection2.getInputStream());
                        JSONObject jsonObject2 = new JSONObject(input2);
                        processEvolution(jsonObject2);
                    } catch (MalformedURLException e) {
                    } catch (IOException e) {
                    }catch (JSONException e){}
                }
            }).start();
        }

        public void processEvolution(JSONObject jsonObject) throws JSONException {
            String s = "None";
            String evolve = "Evolutions";
            jsonObject = jsonObject.getJSONObject("chain");
            int index=0;
            while (jsonObject.has("species")) {
                s = jsonObject.getJSONObject("species").getString("name");
                Log.d("MOO", s);
                addPairs(evolve, s.substring(0, 1).toUpperCase() + s.substring(1),index);
                index++;
                evolve = "";
                if (jsonObject.has("evolves_to")) {
                    if (jsonObject.getJSONArray("evolves_to").length() != 0)
                        jsonObject = jsonObject.getJSONArray("evolves_to").getJSONObject(0);
                    else
                        break;
                } else
                    break;

            }
            if (s.equals("None")) {
                addPairs(evolve, s);
            }
        }

        public void putImage(JSONObject json) throws JSONException {
            final String str = json.getJSONObject("sprites").getString("front_default");
            final RViewItems item;
            if(c!=null)
                c.execute(str);
            item = new RViewItems(json.getString("name"),str,new File(getCacheDir(),path));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    History.historylist.add(item);
                    if(imageAvailable==false)
                        Picasso.with(MainActivity.this).load(str).into(icon);
                    else
                        icon.setImageURI(Uri.fromFile(new File(getCacheDir(),path)));
                }
            });
        }
        public void addPairs(String s, String z,int ... indices) {
            final int index;
            if(indices.length!=0)
                index=indices[0];
            else
                index=-1;
            final String s1 = s;
            final String s2 = z;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(index==-1)
                        pairs.add(new StatPair(s1, s2));
                    else
                        pairs.add(index,new StatPair(s1, s2));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
