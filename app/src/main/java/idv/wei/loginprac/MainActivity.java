package idv.wei.loginprac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btlogin,btlogout;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        btlogin = findViewById(R.id.btlogin);
        btlogout = findViewById(R.id.btlogout);
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivityForResult(intent,1);

            }
        });
        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                pref.edit().putBoolean("login",false).apply();
                view.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                btlogin.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"登出了",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        boolean login = pref.getBoolean("login",false);
        if(login) {
            text.setVisibility(View.VISIBLE);
            btlogout.setVisibility(View.VISIBLE);
        }else {
            text.setVisibility(View.INVISIBLE);
            btlogout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            btlogin.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this,"登入了",Toast.LENGTH_SHORT).show();
        }
    }
}
