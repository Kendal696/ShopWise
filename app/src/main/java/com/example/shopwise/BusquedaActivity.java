package com.example.shopwise;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusquedaActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private LinearLayout productLayout;
    private ScrollView scrollView;

    private static final String API_ENDPOINT = "https://world.openfoodfacts.org";
    private static final String SEARCH_ENDPOINT = "/cgi/search.pl";
    private static final String PRODUCT_ENDPOINT = "/api/v0/product/%s.json";
    private static final String USER_AGENT = "OpenFoodFactsApp/1.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        productLayout = findViewById(R.id.productLayout);
        scrollView = findViewById(R.id.scrollView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchEditText.getText().toString();
                new SearchProductsTask().execute(searchTerm);
            }
        });
    }

    private class SearchProductsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String searchTerm = params[0];
            String urlStr = API_ENDPOINT + SEARCH_ENDPOINT + "?search_terms=" + searchTerm + "&search_simple=1&action=process&json=1";

            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    return response.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            productLayout.removeAllViews();

            try {
                JSONObject responseObject = new JSONObject(response);
                JSONArray productsArray = responseObject.getJSONArray("products");
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject productObject = productsArray.getJSONObject(i);

                    String productName = productObject.optString("product_name", "");
                    String productImageURL = productObject.optString("image_front_small_url", "");
                    String productNutritionGrade = productObject.optString("nutrition_grade_fr", "");
                    String productIngredients = productObject.optString("ingredients_text", "");

                    // Creamos un LinearLayout horizontal para cada producto
                    LinearLayout productItemLayout = new LinearLayout(BusquedaActivity.this);
                    productItemLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, dpToPx(16));
                    productItemLayout.setLayoutParams(layoutParams);

                    // ImageView para mostrar la imagen del producto
                    ImageView productImageView = new ImageView(BusquedaActivity.this);
                    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                            dpToPx(80),
                            dpToPx(80)
                    );
                    imageParams.setMargins(0, 0, dpToPx(16), 0);
                    productImageView.setLayoutParams(imageParams);
                    Glide.with(BusquedaActivity.this).load(productImageURL).into(productImageView);

                    // Creamos un LinearLayout vertical para la información del producto
                    LinearLayout productInfoLayout = new LinearLayout(BusquedaActivity.this);
                    productInfoLayout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    productInfoLayout.setLayoutParams(infoParams);

                    // TextView para el nombre del producto
                    TextView productNameTextView = new TextView(BusquedaActivity.this);
                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    productNameTextView.setLayoutParams(nameParams);
                    productNameTextView.setTextAppearance(BusquedaActivity.this, android.R.style.TextAppearance_Material_Headline);
                    productNameTextView.setText(productName);

                    // TextView para el grado de nutrición del producto
                    TextView productNutritionGradeTextView = new TextView(BusquedaActivity.this);
                    LinearLayout.LayoutParams gradeParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    gradeParams.setMargins(0, dpToPx(4), 0, 0);
                    productNutritionGradeTextView.setLayoutParams(gradeParams);
                    productNutritionGradeTextView.setTextAppearance(BusquedaActivity.this, android.R.style.TextAppearance_Material_Body1);
                    productNutritionGradeTextView.setText("Nutrition Grade: " + productNutritionGrade);

                    // TextView para la descripción del producto
                    TextView productIngredientsTextView = new TextView(BusquedaActivity.this);
                    LinearLayout.LayoutParams ingredientsParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    ingredientsParams.setMargins(0, dpToPx(4), 0, 0);
                    productIngredientsTextView.setLayoutParams(ingredientsParams);
                    productIngredientsTextView.setTextAppearance(BusquedaActivity.this, android.R.style.TextAppearance_Material_Body2);
                    productIngredientsTextView.setText("Ingredients: " + productIngredients);

                    // Agregar vistas al LinearLayout de la información del producto
                    productInfoLayout.addView(productNameTextView);
                    productInfoLayout.addView(productNutritionGradeTextView);
                    productInfoLayout.addView(productIngredientsTextView);

                    // Agregar vistas al LinearLayout del producto
                    productItemLayout.addView(productImageView);
                    productItemLayout.addView(productInfoLayout);

                    // Agregar el LinearLayout del producto al LinearLayout principal
                    productLayout.addView(productItemLayout);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Hacer que el ScrollView se desplace hasta la parte superior
            scrollView.scrollTo(0, 0);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
