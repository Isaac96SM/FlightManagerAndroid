package com.example.isaac.flightmanager;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.helpers.ParserAdapter;

import java.sql.Timestamp;

public class HomeActivity extends AppCompatActivity {

    //Data Sources
    Customer[] listCustomers;
    Travel[] listTravels;
    Seats[] listSeats;
    Response Response;

    //Pointers
    Spinner SpinnerCustomer;
    Spinner SpinnerForTo;
    Spinner SpinnerSeat;
    TextView Result;
    EditText Departure;
    EditText Arrived;
    EditText Company;

    //Variables to set the object selected
    Customer ActiveCustomer;
    Travel ActiveTravel;
    Seats ActiveSeat;

    private class TaskGetAllCustomers extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {
            boolean result = true;

            String NAMESPACE = "http://tempuri.org/";
            String URL="http://flightwebservice.azurewebsites.net/InsertSale.asmx?";
            String METHOD_NAME = "GetCustomer";
            String SOAP_ACTION = "http://tempuri.org/GetCustomer";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                listCustomers = new Customer[resSoap.getPropertyCount()];

                for (int i = 0; i < listCustomers.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Customer cli = new Customer();
                    cli.Code = Integer.parseInt(ic.getProperty(0).toString());
                    cli.Name = ic.getProperty(1).toString();

                    listCustomers[i] = cli;
                }
            }
            catch (Exception e)
            {
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result)
            {
                if (listCustomers.length > 0) {

                    final MyAdapter adapter;
                    adapter = new MyAdapter(HomeActivity.this,
                            android.R.layout.simple_spinner_item,
                            listCustomers);

                    SpinnerCustomer.setAdapter(adapter);
                    SpinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            ActiveCustomer = adapter.getItem(pos);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                }
            }
        }
    }

    private class TaskGetAllTravels extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {
            boolean result = true;

            String NAMESPACE = "http://tempuri.org/";
            String URL="http://flightwebservice.azurewebsites.net/InsertSale.asmx?";
            String METHOD_NAME = "GetTravel";
            String SOAP_ACTION = "http://tempuri.org/GetTravel";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                listTravels = new Travel[resSoap.getPropertyCount()];

                for (int i = 0; i < listTravels.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Travel cli = new Travel();
                    cli.Code = Integer.parseInt(ic.getProperty(0).toString());
                    cli.Time_Departure = ic.getProperty(3).toString();
                    cli.Time_Arrived = ic.getProperty(4).toString();
                    cli.CompanyName = ic.getProperty(11).toString();
                    cli.Origin_Destination = ic.getProperty(12).toString();

                    listTravels[i] = cli;
                }
            }
            catch (Exception e)
            {
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result)
            {
                if (listTravels.length > 0) {

                    final MyAdapter2 adapter2;
                    adapter2 = new MyAdapter2(HomeActivity.this,
                            android.R.layout.simple_spinner_item,
                            listTravels);

                    SpinnerForTo.setAdapter(adapter2);
                    SpinnerForTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            Travel travel = adapter2.getItem(pos);
                            Departure.setText(travel.Time_Departure);
                            Arrived.setText(travel.Time_Arrived);
                            Company.setText(travel.CompanyName);

                            ActiveTravel = travel;
                            TaskGetAllSeats SeatTask = new TaskGetAllSeats();
                            SeatTask.execute();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                }
            }
        }
    }

    private class TaskGetAllSeats extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {
            boolean result = true;

            String NAMESPACE = "http://tempuri.org/";
            String URL="http://flightwebservice.azurewebsites.net/InsertSale.asmx?";
            String METHOD_NAME = "GetSeats";
            String SOAP_ACTION = "http://tempuri.org/GetSeats";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("parCode", ActiveTravel.Code);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                listSeats = new Seats[resSoap.getPropertyCount()];

                for (int i = 0; i < listSeats.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Seats cli = new Seats();
                    cli.Seat = ic.getProperty(1).toString();
                    cli.Full = ic.getProperty(2).toString();
                    cli.Row = Integer.parseInt(ic.getProperty(0).toString());

                    listSeats[i] = cli;
                }
            }
            catch (Exception e)
            {
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result)
            {
                if (listSeats.length > 0) {

                    final MyAdapter3 adapter;
                    adapter = new MyAdapter3(HomeActivity.this,
                            android.R.layout.simple_spinner_item,
                            listSeats);

                    SpinnerSeat.setAdapter(adapter);
                    SpinnerSeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            ActiveSeat = adapter.getItem(pos);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                }
            }
        }
    }

    private class TaskInsertSale extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {
            boolean result = true;

            String NAMESPACE = "http://tempuri.org/";
            String URL="http://flightwebservice.azurewebsites.net/InsertSale.asmx?";
            String METHOD_NAME = "SaleRequest";
            String SOAP_ACTION = "http://tempuri.org/SaleRequest";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("parRow", ActiveSeat.Row);
            request.addProperty("parSeat", ActiveSeat.Seat);
            request.addProperty("parTravel", ActiveTravel.Code);
            request.addProperty("parCustomer", ActiveCustomer.Code);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap = (SoapObject)envelope.getResponse();

                Response = new Response();

                Response.Result = Boolean.parseBoolean(resSoap.getProperty(0).toString());
                Response.ResultDescription = resSoap.getProperty(1).toString();
            }
            catch (Exception e)
            {
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result)
            {
                if (Response.Result) {
                    Result.setText(Response.ResultDescription);
                    TaskGetAllSeats SeatTask = new TaskGetAllSeats();
                    SeatTask.execute();
                } else {
                    Result.setText(Response.ResultDescription);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        SpinnerCustomer = (Spinner)findViewById(R.id.spinnerCustomer);

        TaskGetAllCustomers CustomerTask = new TaskGetAllCustomers();
        CustomerTask.execute();

        Departure = (EditText)findViewById(R.id.editTextDeparture);
        Departure.setEnabled(false);
        Arrived = (EditText)findViewById(R.id.editTextArrived);
        Arrived.setEnabled(false);
        Company = (EditText)findViewById(R.id.editTextCompany);
        Company.setEnabled(false);
        SpinnerSeat = (Spinner)findViewById(R.id.spinnerSeat);

        SpinnerForTo = (Spinner)findViewById(R.id.spinnerForTo);
        TaskGetAllTravels TravelTask = new TaskGetAllTravels();
        TravelTask.execute();

        Button InsertButton = (Button)findViewById(R.id.buttonBuy);
        InsertButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {

                Result = (TextView)findViewById(R.id.textViewResult);

                TaskInsertSale SaleTask = new TaskInsertSale();
                SaleTask.execute();
            }
        });
    }
}
