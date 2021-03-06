package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.fragments.AlertFragment;
import io.krito.com.rezetopia.models.operations.UserOperations;
import io.krito.com.rezetopia.receivers.ConnectivityReceiver;
import io.krito.com.rezetopia.views.CustomButton;
import io.krito.com.rezetopia.views.CustomEditText;
import io.krito.com.rezetopia.views.CustomTextView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rmiri.buttonloading.ButtonLoading;

public class Login extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener{

    RelativeLayout loginLayout;
    CustomButton sign_up;
    CustomEditText emailView;
    CustomEditText passwordView;
    CustomTextView forgetPassword;
    ButtonLoading buttonLoading;

    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        networkListener();

        loginLayout = findViewById(R.id.loginLayout);
        sign_up = findViewById(R.id.btnRegister);
        emailView = findViewById(R.id.emailView);
        passwordView = findViewById(R.id.passwordView);
        forgetPassword = findViewById(R.id.forgetPassword);
        buttonLoading = findViewById(R.id.btnSignIn);

        sign_up.setOnClickListener(this);

        buttonLoading.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
            @Override
            public void onClick() {
                if (ConnectivityReceiver.isConnected(Login.this)){
                    if (validCredentials()) {
                        login(emailView.getText().toString(), passwordView.getText().toString());
                    } else {
                        String alert = getResources().getString(R.string.wrong_login);
                        AlertFragment.createFragment(alert).show(getFragmentManager(), null);
                        buttonLoading.setProgress(false);
                    }
                }  else {
                    Snackbar.make(loginLayout, R.string.checkingNetwork, Snackbar.LENGTH_INDEFINITE).show();
                    buttonLoading.setProgress(false);
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        RezetopiaApp.getInstance().setConnectivityListener(this);
    }

    private void login(String email, String password){
        UserOperations.login(email, password);
        UserOperations.setLoginCallback(new UserOperations.LoginCallback() {
            @Override
            public void onSuccess(String id) {
                getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .edit().putString(AppConfig.LOGGED_IN_USER_ID_SHARED, id).apply();
                startActivity(new Intent(Login.this, Main.class));
                finish();
            }

            @Override
            public void onError(int error) {
                String s = getResources().getString(error);
                Snackbar.make(loginLayout, s, Snackbar.LENGTH_INDEFINITE).show();
                buttonLoading.setProgress(false);
            }
        });
    }

    private void startRegister(){
        startActivity(new Intent(this, Registration.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignIn:
                break;
            case R.id.btnRegister:
                startRegister();
                break;
            case R.id.forgetPassword:
                break;
           default:
               break;
        }
    }

    private boolean validCredentials(){
        if (!emailView.getText().toString().isEmpty() && !passwordView.getText().toString().isEmpty()){
            return true;
        }

        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        connected = isConnected;
    }

    private void networkListener(){
        ReactiveNetwork.observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED){
                        Log.i("internetC", "onNext: " + "Connected");
                    } else if (connectivity.getState() == NetworkInfo.State.SUSPENDED){
                        Log.i("internetC", "onNext: " + "LowNetwork");
                    } else {
                        Log.i("internetC", "onNext: " + "NoInternet");
                        Flashbar.Builder builder = new Flashbar.Builder(this);
                        builder.gravity(Flashbar.Gravity.BOTTOM)
                                .backgroundColor(R.color.red2)
                                .enableSwipeToDismiss()
                                .message(R.string.checkingNetwork)
                                .enterAnimation(new FlashAnimBarBuilder(Login.this).slideFromRight().duration(200))
                                .build().show();
                    }
                });
    }
}
