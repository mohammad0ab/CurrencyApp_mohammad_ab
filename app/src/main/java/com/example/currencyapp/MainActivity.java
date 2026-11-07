package com.example.currencyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txtUSD, txtEUR, txtGBP, txtDate;
    Button btnRefresh;

    RequestQueue requestQueue;

    String url = "https://open.er-api.com/v6/latest/USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUSD = findViewById(R.id.txtUSD);
        txtEUR = findViewById(R.id.txtEUR);
        txtGBP = findViewById(R.id.txtGBP);
        txtDate = findViewById(R.id.txtDate);

        btnRefresh = findViewById(R.id.btnRefresh);

        requestQueue = Volley.newRequestQueue(this);

        // دریافت اولیه اطلاعات
        getCurrencyData();

        // کلیک روی دکمه به روز رسانی
        btnRefresh.setOnClickListener(v -> {
            getCurrencyData();
        });
    }

    private void getCurrencyData() {

        txtDate.setText("در حال دریافت اطلاعات...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,

                response -> {
                    try {

                        String result = response.getString("result");

                        if (result.equals("success")) {

                            JSONObject rates =
                                    response.getJSONObject("rates");

                            double usd = rates.getDouble("USD");
                            double eur = rates.getDouble("EUR");
                            double gbp = rates.getDouble("GBP");

                            txtUSD.setText(
                                    "دلار آمریکا: " + String.format(Locale.US, "%.4f", usd)
                            );

                            txtEUR.setText(
                                    "یورو: " + String.format(Locale.US, "%.4f", eur)
                            );

                            txtGBP.setText(
                                    "پوند انگلیس: " + String.format(Locale.US, "%.4f", gbp)
                            );

                            long updateTime =
                                    response.getLong("time_last_update_unix");

                            Date date = new Date(updateTime * 1000);

                            SimpleDateFormat sdf =
                                    new SimpleDateFormat(
                                            "yyyy/MM/dd HH:mm",
                                            Locale.US
                                    );

                            txtDate.setText(
                                    "آخرین به روز رسانی: " + sdf.format(date)
                            );

                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "دریافت اطلاعات ناموفق بود",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                MainActivity.this,
                                "خطا در پردازش اطلاعات",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                },

                error -> {

                    txtDate.setText("خطا در اتصال به اینترنت");
                    Toast.makeText(
                            MainActivity.this,
                            "اتصال به وب سرویس برقرار نشد",
                            Toast.LENGTH_LONG
                    ).show();

                }
        );

        requestQueue.add(request);
    }
}