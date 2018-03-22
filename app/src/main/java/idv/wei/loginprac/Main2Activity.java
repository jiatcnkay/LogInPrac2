package idv.wei.loginprac;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {
        private final static String TAG = "LoginDialogActivity";
        private EditText etUser;
        private EditText etPassword;
        private AsyncTask logInTask;
        private ProgressDialog progressDialog;
        private MemberVO member;
        private  String user,password;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);
            findViews();
            //預設結果皆為失敗，除非驗證成功才會將結果設為RESULT_OK
            setResult(RESULT_CANCELED);
        }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        boolean login = pref.getBoolean("login",false);
        if(login){
            String name = pref.getString("user","");
            String password = pref.getString("password","");
            if (isUserValid(name, password)) {
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(Main2Activity.this,"不正確",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void findViews() {
            etUser = findViewById(R.id.etUser);
            etPassword = findViewById(R.id.etPassword);
            Button btLogin = findViewById(R.id.btLogin);

            btLogin.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    user = etUser.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    if (user.length() <= 0 || password.length() <= 0) {
                        Toast.makeText(Main2Activity.this,"請輸入",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        logInTask = new LogInTask(user).execute(Util.LOGURL);
                    }
                }
            });
        }

        class LogInTask extends AsyncTask<String, Void, MemberVO> {
            private String account;

            public LogInTask(String name) {
                this.account = name;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(Main2Activity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected MemberVO doInBackground(String... params) {
                String url = params[0];
                String jsonIn;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("param", "account");
                jsonObject.addProperty("account", account);
                jsonIn = getRemoteData(url, jsonObject.toString());
                Gson gson = new Gson();
                Log.d(TAG,"hahahahaha:"+gson.fromJson(jsonIn, MemberVO.class));
                return  gson.fromJson(jsonIn, MemberVO.class);
            }

            @Override
            protected void onPostExecute(MemberVO memberVO) {
                member = memberVO;
                SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                if (isUserValid(user, password)) {
                    pref.edit()
                            .putBoolean("login", true)
                            .putString("user", user)
                            .putString("password", password)
                            .apply();
                    setResult(RESULT_OK);
                    finish();
                }
                progressDialog.cancel();
            }
        }

        public String getRemoteData(String url, String outStr) {
            HttpURLConnection connection = null;
            StringBuilder inStr = new StringBuilder();
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset","UTF-8");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bw.write(outStr);
                bw.close();
                ///////////////////
                int responseCode = connection.getResponseCode();
                if(responseCode==200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while((line = br.readLine()) != null){
                        inStr.append(line);
                    }
                }else {
                    Log.d(TAG, "response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.d(TAG, "input: " + inStr);
            return inStr.toString();
        }

    private boolean isUserValid(String name, String password) {
        if(member.getMemAccount()==null){
            Toast.makeText(Main2Activity.this,"無此帳號，請重新輸入",Toast.LENGTH_LONG).show();
            etUser.setText("");
            etPassword.setText("");
            return false;
        } else if (!member.getMemPassword().equals(password)) {
            Toast.makeText(Main2Activity.this, "密碼不正確，請重新輸入", Toast.LENGTH_LONG).show();
            etPassword.setText("");
            return false;
        } else
            return true;
    }
}
