package arsibi_has_no_website.pokedex;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    StatsAdapter adapter;
    List<StatPair> pairs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ArrayAdapter<String> autoadapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, getListForAdapter());
        stats = (ListView) findViewById(R.id.statslist);
        adapter = new StatsAdapter(getApplicationContext(), R.layout.statitem, pairs);
        stats.setAdapter(adapter);
        getButton = (Button) findViewById(R.id.button);
        atv = (AutoCompleteTextView) findViewById(R.id.autotext);
        atv.setAdapter(autoadapter);
    }

    public void loadImage(View v) {
        getButton.setClickable(false);
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        ImageHandlerTask task = new ImageHandlerTask();
        task.execute("http://pokeapi.co/api/v2/pokemon/" + ((AutoCompleteTextView) findViewById(R.id.autotext)).getText().toString().toLowerCase());
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
            try {
                putEvolutionChain(json);
            } catch (Exception e) {
                Log.d("MOO", e.toString());
            }
            putId(json);
            putWeight(json);
            putHeight(json);
            putTypes(json);
            putMoves(json);
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
                    TextView tv = (TextView) findViewById(R.id.name);
                    tv.setText(str.substring(0, 1).toUpperCase() + str.substring(1));
                }
            });
        }

        public void putTypes(JSONObject json) throws JSONException {
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
            String urlstring = json.getJSONObject("species").getString("url");
            try {
                URL url = new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String input = convertString(connection.getInputStream());
                JSONObject jsonObject = new JSONObject(input);
                urlstring = jsonObject.getJSONObject("evolution_chain").getString("url");
                url = new URL(urlstring);
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                String input2 = convertString(connection2.getInputStream());
                JSONObject jsonObject2 = new JSONObject(input2);
                processEvolution(jsonObject2);
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
        }

        public void processEvolution(JSONObject jsonObject) throws JSONException {
            String s = "None";
            String evolve = "Evolutions";
            jsonObject = jsonObject.getJSONObject("chain");
            Log.d("MOO", "hi");
            Log.d("MOO", jsonObject.toString());
            while (jsonObject.has("species")) {
                Log.d("MOO", "hi");
                s = jsonObject.getJSONObject("species").getString("name");
                Log.d("MOO", s);
                addPairs(evolve, s.substring(0, 1).toUpperCase() + s.substring(1));

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView img = (ImageView) findViewById(R.id.imageView);
                    Picasso.with(MainActivity.this).load(str).into(img);

                }
            });
        }

        public void addPairs(String s, String z) {
            final String s1 = s;
            final String s2 = z;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pairs.add(new StatPair(s1, s2));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
