package com.example.rtrinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class category extends AppCompatActivity {

    ListView l,l2;
    boolean update = false;
    String script = "https://script.google.com/macros/s/AKfycbylEOzSUGabDYyoyyVJqYIZohWeAAkjrcapyoWTK314xt1k2K1Q7YnKqOozykzXpxfa/exec";
    FloatingActionButton mAddFab;
    ArrayAdapter<String> arr,arr2;
    final Context context = this;
    String temp;
    String Category=" ";
    String in,q,data;
    TextView title;
    String random[];
    String component[];
    String quantity[];
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        title = findViewById(R.id.textView5);
        l = findViewById(R.id.componentDetails);
        l2 = findViewById(R.id.componentQuantity);

        getComponetDetails();


        Intent intent = getIntent();
        data = intent.getStringExtra("category");
        title.setText(data);

        mAddFab = findViewById(R.id.add_fab);

        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialogBox(" "," ",false);

            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=arr.getItem(position);
                update = true;
                getDialogBox(value,arr2.getItem(position),update);
            }
        });
    }

    void getDialogBox(String ch,String ch1,boolean u)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.itemName);
        final EditText quantity = (EditText) promptsView
                .findViewById(R.id.quantity);
        userInput.setText(ch);
        quantity.setText(ch1);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                in = userInput.getText().toString();
                                q = quantity.getText().toString();
                                if(!u){
                                    addItemToSheet();
                                    getComponetDetails();
                                }else{

                                }

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
    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, script,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
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
                parmas.put("action","addItem");
                parmas.put("category",data);
                parmas.put("itemName",in);
                parmas.put("quantity",q);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    void getComponetDetails()
    {
        final ProgressDialog loading = ProgressDialog.show(this,"Fetching Component Deatils","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, script,
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
                        random = new String[count+1];
                        temp = response;
                        int i=0;
                        while(temp.indexOf(",")!=-1)
                        {
                            random[i++] = temp.substring(0,temp.indexOf(","));
                            temp = temp.substring(temp.indexOf(",")+1);
                        }
                        random[i] = response.substring(response.lastIndexOf(",")+1);

                        i=(int) (count-2)/3;
                        component = new String[i];
                        quantity = new String[i];
                        i=0;
                        for(int k =4;k<=count;k+=3)
                        {
                            component[i] = random[k] ;
                            quantity[i]=random[k+1];
                            i++;
                        }
                        i=0;
                        arr = new ArrayAdapter<String>(getApplicationContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                component);
                        arr2 = new ArrayAdapter<String>(getApplicationContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                quantity);
                        l.setAdapter(arr);
                        l2.setAdapter(arr2);
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
                parmas.put("action","getData");
                parmas.put("category",data);

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
