package com.landleaf.serial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.landleaf.serial.dnake.Rs485Executor;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：Lei on 2020/10/9
 */
public class WindActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind_test);
        final EditText etAddress = findViewById(R.id.etAddress);
        final EditText etValue = findViewById(R.id.etValue);
        final EditText etStart = findViewById(R.id.etStart);
        final EditText etLength = findViewById(R.id.etLength);
        final TextView tvSend = findViewById(R.id.tvSend);
        final TextView tvReceive = findViewById(R.id.tvReceive);
        Button btRead = findViewById(R.id.btRead);
        Button btWrite = findViewById(R.id.btWrite);

        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etStart.getText().toString();
                String value = etLength.getText().toString();
                final byte[] send = CommonUtils.getInstance().getReadSendBytes(1, 3, Integer.valueOf(address), Integer.valueOf(value));
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        Rs485Executor.getInstance().send(send);

                        Log.d("WindActivity", CommonUtils.getInstance().hexToString(send));
                        try {

                            byte[] receive = Rs485Executor.getInstance().receive(send, 300);

//                            tvSend.setText(CommonUtils.getInstance().hexToString(send));
//                            tvReceive.setText(CommonUtils.getInstance().hexToString(receive));
                            Log.d("WindActivity", CommonUtils.getInstance().hexToString(receive));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });
            }
        });

        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etAddress.getText().toString();
                String value = etValue.getText().toString();
                final byte[] send = CommonUtils.getInstance().getReadSendBytes(1, 6, Integer.valueOf(address), Integer.valueOf(value));
                Log.d("WindActivity send", CommonUtils.getInstance().hexToString(send));
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        Rs485Executor.getInstance().send(send);
                        try {
                            byte[] receive = Rs485Executor.getInstance().receive(send, 300);

//                            tvSend.setText(CommonUtils.getInstance().hexToString(send));
//                            tvReceive.setText(CommonUtils.getInstance().hexToString(receive));
                            Log.d("WindActivity receive", CommonUtils.getInstance().hexToString(receive));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });
            }
        });
    }
}
