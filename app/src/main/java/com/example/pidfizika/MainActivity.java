package com.example.pidfizika;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    private TextView txtCurrentTemperature, txtStatus, txtSetTemperature;
    private EditText edtSetTemperature;
    private Button btnSendSetTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mqttHelper = new MqttHelper(getApplicationContext(), "zhb/#");

        txtCurrentTemperature = findViewById(R.id.txtCurrentTemperature);
        txtStatus = findViewById(R.id.txtStatus);
        txtSetTemperature = findViewById(R.id.txtSetTemperature);

        edtSetTemperature = findViewById(R.id.edtSetTemperature);

        btnSendSetTemperature = findViewById(R.id.btnSendSetTemperature);

        btnSendSetTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtSetTemperature.getText().toString().isEmpty())
                    mqttHelper.sendMessage("zhb/set/vrednost", String.valueOf(edtSetTemperature.getText()));
                else {
                    Log.w("ERROR", "edittext mora biti broj");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startMqtt();
    }

    private void startMqtt() {
        mqttHelper.setCallback(new MqttCallbackExtended() {
            //sendMessage
            //mqttHelper.sendMessage("kuca/pokusaj", "evoMe");
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                String message = mqttMessage.toString();

                switch (topic) {
                    case "zhb/temperature":
                        txtCurrentTemperature.setText(message + "°C");
                        break;
                    case "zhb/set":
                        txtSetTemperature.setText(message + "°C");
                        break;
                    case "zhb/status":
                        if (message.equals("1")) {
                            txtStatus.setText("UKLJUCENO");
                            txtStatus.setTextColor(Color.parseColor("#00FF00"));
                        } else {
                            txtStatus.setText("ISKLJUCENO");
                            txtStatus.setTextColor(Color.parseColor("#FF0000"));
                        }
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                //mqttHelper.client.close();
            }
        });
    }
}
