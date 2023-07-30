package com.example.rtrinventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    ListView l;
    FloatingActionButton mAddFab;
    final Context context = this;
    public static String categories[];
    String temp;
    ArrayAdapter<String> arr;
    String Category=" ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //categories.add
        getCatgories();

        mAddFab = findViewById(R.id.add_fab);
        l = findViewById(R.id.category);

        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.category, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.categoryName);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Toast.makeText(getApplicationContext(),userInput.getText().toString(),Toast.LENGTH_SHORT).show();
                                        Category = userInput.getText().toString();
                                        addItemToSheet();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=arr.getItem(position);
                Intent intent = new Intent(getApplicationContext(), category.class);
                intent.putExtra("category", value); //Your id
                startActivity(intent);
            }
        });

    }

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxOSUPj1LN0ZVsXLiFE334HWYKmA0U4eyEF88zPCTQJdDbwAQdPfTyJESWmlWqpU3O3/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getCatgories();
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","addCategory");
                parmas.put("category",Category);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    void getCatgories()
    {
        final ProgressDialog loading = ProgressDialog.show(this,"Fetching categories","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxOSUPj1LN0ZVsXLiFE334HWYKmA0U4eyEF88zPCTQJdDbwAQdPfTyJESWmlWqpU3O3/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int count=0;
                        for(int i=0;i<response.length();i++)
                        {
                            if(response.charAt(i) == ',')
                            {
                                count++;
                            }
                        }
                        categories = new String[count+1];
                        temp = response;
                        int i=0;
                        while(temp.indexOf(",")!=-1)
                        {
                            categories[i++] = temp.substring(0,temp.indexOf(","));
                            temp = temp.substring(temp.indexOf(",")+1);
                        }
                        categories[i] = response.substring(response.lastIndexOf(",")+1);

                        arr = new ArrayAdapter<String>(getApplicationContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                categories);
                        l.setAdapter(arr);
                        i=0;
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","getCategory");

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }
}

