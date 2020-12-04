package com.example.bd_locales;

//Clase que maneja todas las operaciones de la Base de datos SQLite

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE"; //A la hora de crear metodos es mas facil usar las constantes, por eso el refractor
    public static final String COLUMN_CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String COLUMN_CUSTOMER_AGE = "CUSTOMER_AGE";
    public static final String COLUMN_ACTIVE_CUSTOMER = "ACTIVE_CUSTOMER";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "customer.db", null, 1);

    }

    //Este metodo se llama la primera vez que se accede a la base de datos, debe haber codigo aqui para generar la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se crea la sentencia SQL para crear una nueva tabla
        String createTableStatement= "CREATE TABLE " + CUSTOMER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CUSTOMER_NAME + " TEXT, " + COLUMN_CUSTOMER_AGE + " INT, " + COLUMN_ACTIVE_CUSTOMER + " BOOL)";
      // String createTableStatement2= "CREATE TABLE "CUSTOMER_TABLE2" (ID INTEGER PRIMARY KEY AUTOINCREMENT, CUSTOMER_NAME TEXT, CUSTOMER_AGE INT, ACTIVE_CUSTOMER BOOL)";
        db.execSQL(createTableStatement);

    }

    //Este metodo es llamado si el numero de version de nuestra base de datos cambia,evita que la aplicación de  usuarios anteriores se rompa cuando se cambia el diseño de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //METODO AGREGAR

    public boolean addOne(CustomerModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase(); //este metodo viene con la clase SQLOpenHelper
        ContentValues cv = new ContentValues(); //Parecido a un Hashmap

        cv.put(COLUMN_CUSTOMER_NAME, customerModel.getName());
        cv.put(COLUMN_CUSTOMER_AGE, customerModel.getAge());
        cv.put(COLUMN_ACTIVE_CUSTOMER, customerModel.isActive());
        //cv.put(COLUMN_ID,customerModel.getId()); No es necesario poner el ID por que lo declaramos autoincrementable

        long insert = db.insert(CUSTOMER_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {

            return true;
        }
    }

    //METODO BUSCAR
    public void searchCustomer(CustomerModel customerModel,int id_buscado){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + id_buscado, null);

        if(cursor.moveToFirst()){
            do{
                customerModel.setId(cursor.getInt(0));
                customerModel.setName(cursor.getString(1));
                customerModel.setAge(cursor.getInt(2));
                customerModel.setActive(cursor.getInt(3) == 1 ? true: false);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    //METODO EDITAR
    public void editOne(CustomerModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "UPDATE " + CUSTOMER_TABLE + " SET " + COLUMN_CUSTOMER_NAME + " = '" + customerModel.getName()
                + "',"+ COLUMN_CUSTOMER_AGE + " = '" + customerModel.getAge() + "'," + COLUMN_ACTIVE_CUSTOMER + " = '" + customerModel.isActive() +
                "' WHERE " + COLUMN_ID + " = " + customerModel.getId();

        if(db!=null){
            db.execSQL(queryString);
            db.close();
        }
    }

    //METODO ELIMINAR

    public boolean deleteOne(CustomerModel customerModel){
        //busca el cliente seleccionado en la base de datos, si lo encuentra, lo borrara y regresara true
        //si no es encontrado regresa falso

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + customerModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
           return true;
        }else{
            return false;
        }

    }

    //METODO PARA MOSTRAR TODOS LOS CLIENTES

    public List<CustomerModel> getEveryone(){
        //crear una lista vacía
        List<CustomerModel> returnList = new ArrayList<>();

        //obtener info de la base de datos
        String queryString="SELECT * FROM " + CUSTOMER_TABLE;

        //referencia a la base de datos

        SQLiteDatabase db=this.getReadableDatabase();

        //SQLiteDatabase db = this.getWritableDatabase();
        //La funcion getWritableDatabase bloquea la fila de datos para que otros procesos no puedan acceder a ella

        Cursor cursor = db.rawQuery(queryString,null);
        //Cursor es el conjunto de resultados de una declaracion SQL
        if(cursor.moveToFirst()){//regresa true si se seleccionaron items de la db
        //se cicla a travez de cursor (que es nuestro conjunto de resultados) y crea nuevos objetos de tipo customerModel para ir llenando la lista
        do{
            //obtenemos los valores que guardo cursor

            int customerID=cursor.getInt(0);
            String customerName=cursor.getString(1);
            int customerAge=cursor.getInt(2);
            //En SQLite no existe el tipo de dato bool o boolean, dentro de la base de datos se almacena como entero
            //Ternary operator
            boolean customerActive=cursor.getInt(3) == 1 ? true: false;

            //se crea un cliente nuevo, con los valores anteriores
            CustomerModel customer=new CustomerModel(customerID,customerName,customerAge,customerActive);

            //agregamos el nuevo cliente a la lista
            returnList.add(customer);

        }while(cursor.moveToNext());

        } else{
            //no se añaden ele,elementos a la lista
        }

        //cerrar la base de datos cuando se acabe el ciclo
        cursor.close();
        db.close();

        //mandar al MainActivity
        return returnList;
    }
}
