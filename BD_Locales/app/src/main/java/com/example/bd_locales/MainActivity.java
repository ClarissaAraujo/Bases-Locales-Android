package com.example.bd_locales;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderClient;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {

    //Referencias a los botones y otros controles del Layout
    Button btn_add, btn_viewAll, btn_search;
    EditText t_name, t_age, t_search;
    Switch sw_activecustomer;
    ListView lv_CustomerList;
    ArrayAdapter customerArrayAdapter;
    DataBaseHelper databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add=(Button)findViewById(R.id.btn_add);
        btn_viewAll=(Button)findViewById(R.id.btn_viewAll);
        btn_search=(Button)findViewById(R.id.btn_search);
        t_name=(EditText)findViewById(R.id.t_name);
        t_age=(EditText)findViewById(R.id.t_age);
        t_search=(EditText)findViewById(R.id.t_search);
        sw_activecustomer=(Switch)findViewById(R.id.sw_activecustomer);
        lv_CustomerList=(ListView)findViewById(R.id.lv_CustomerList);

        databasehelper = new DataBaseHelper(MainActivity.this);

        ShowCustomerOnListView(databasehelper);

        //listeners de los botones add y viewAll
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomerModel customermodel;
                try {
                    customermodel = new CustomerModel(-1,t_name.getText().toString(),Integer.parseInt(t_age.getText().toString()),sw_activecustomer.isChecked());
                   // Toast.makeText(MainActivity.this,customermodel.toString(),Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this,"Error al crear al cliente",Toast.LENGTH_SHORT).show();
                    customermodel = new CustomerModel(-1,"error",0,false);
                }

                DataBaseHelper databasehelper=new DataBaseHelper(MainActivity.this);
                boolean success = databasehelper.addOne(customermodel);
                ShowCustomerOnListView(databasehelper);
                Toast.makeText(MainActivity.this,"Success "+ success,Toast.LENGTH_SHORT).show();

            }
        });

        btn_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper databasehelper = new DataBaseHelper(MainActivity.this);
                CustomerModel customermodel=new CustomerModel();
                customermodel = new CustomerModel(Integer.parseInt(t_search.getText().toString()),t_name.getText().toString(),Integer.parseInt(t_age.getText().toString()),sw_activecustomer.isChecked());
                databasehelper.editOne(customermodel);
                Toast.makeText(MainActivity.this,customermodel.toString(),Toast.LENGTH_SHORT).show();
                ShowCustomerOnListView(databasehelper);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerModel customer=new CustomerModel();
                DataBaseHelper databasehelper = new DataBaseHelper(MainActivity.this);
                int id_buscado=Integer.parseInt(t_search.getText().toString());

                databasehelper.searchCustomer(customer, id_buscado);
                //Toast.makeText(MainActivity.this,customer.toString(),Toast.LENGTH_SHORT).show();
               t_name.setText(customer.getName());
               t_age.setText(String.valueOf(customer.getAge()));
                sw_activecustomer.setChecked(customer.isActive());

            }
        });

        lv_CustomerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomerModel clickCustomer=(CustomerModel)parent.getItemAtPosition(position);
                databasehelper.deleteOne(clickCustomer);
                ShowCustomerOnListView(databasehelper);
                Toast.makeText(MainActivity.this,"Deleted " + clickCustomer.toString(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void ShowCustomerOnListView(DataBaseHelper databasehelper2) {
        customerArrayAdapter = new ArrayAdapter<CustomerModel>(MainActivity.this, android.R.layout.simple_list_item_1, databasehelper2.getEveryone());
        lv_CustomerList.setAdapter(customerArrayAdapter);
    }

}