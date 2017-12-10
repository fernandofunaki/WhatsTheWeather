package com.training.whatstheweather;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    EditText cityEditText;
    TextView mainTextView;
    TextView descritionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.cityEditText);
        mainTextView = (TextView) findViewById(R.id.mainTextView);
        descritionTextView = (TextView) findViewById(R.id.descriptionTextView);




    }

    public void clearResult(){
        mainTextView.setText("");
        descritionTextView.setText("");
    }

    public void findWeather(View view){
        clearResult();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);

        DownloadTask task = new DownloadTask();

        try {

            String encodedCityName = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");

            String result =
                    task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ encodedCityName +"&appid=0b1fa8872c6209fd792b5de60f9f14f3").get();

        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.i("City name: ", cityEditText.getText().toString());
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String result = "";
            try {

                JSONObject jsonObject = new JSONObject(s);
                result = jsonObject.getString("weather");


                JSONArray arr = new JSONArray(result);

                for(int i = 0; arr.length() > i; i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Log.i("Main: " , jsonPart.getString("main"));
                    Log.i("Description: " , jsonPart.getString("description"));


                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    mainTextView.setText(main);
                    descritionTextView.setText(description);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.i("Weather", result);

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                String result = "";
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                     data = reader.read();
                }

                return result;


            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT);
            }

            return null;
        }
    }
}
