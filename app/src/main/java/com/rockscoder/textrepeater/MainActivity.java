package com.rockscoder.textrepeater;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editText,numRepeat;
    TextView resultText;
    Button button;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText =findViewById(R.id.editText);
        numRepeat =findViewById(R.id.numRepeat);
        button = findViewById(R.id.button);
        resultText = findViewById(R.id.resultText);
        this.settings = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void repeat(View view) {
        int num = getIntByString(this.numRepeat.getText().toString());
        if (this.editText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Write Something",Toast.LENGTH_LONG).show();
        } else if (num <= 0) {
            Toast.makeText(this, "Set Repetitions", Toast.LENGTH_LONG).show();
        } else {
            boolean autoSpaces = this.settings.getBoolean("auto_space", true);
            boolean autoTrim = this.settings.getBoolean("auto_trim", true);
            String inputText = this.editText.getText().toString();
            new RepeatTask(num, autoSpaces, autoTrim).execute(inputText);
        }
    }

    private int getIntByString(String num) {
        if (num.trim().isEmpty()) {
            return 0;
        }
        return Integer.valueOf(num);
    }

    public class RepeatTask extends AsyncTask<String, Integer, String> {
        private boolean autoSpaces;
        private boolean autoTrim;
        private ProgressDialog progressDialog;
        private int repetitions;

        public RepeatTask(int repetitions, boolean autoSpaces, boolean autoTrim) {
            this.repetitions = repetitions;
            this.autoSpaces = autoSpaces;
            this.autoTrim = autoTrim;
        }

        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(MainActivity.this);
            this.progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    RepeatTask.this.cancel(true);
                }
            });
            this.progressDialog.setMax(this.repetitions);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setTitle(R.string.repeating);
            this.progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String inputText = strings[0];
            String resultText = "";
            for (int i = 0; i < this.repetitions; i++) {
                //resultText = resultText.concat((this.autoTrim ? inputText.trim() : inputText) + (this.autoSpaces ? " " : ""));
                resultText = resultText.concat(inputText);
                publishProgress(i);
                if (isCancelled()) {
                    break;
                }
            }
            return resultText;
        }

        protected void onProgressUpdate(Integer... values) {
            if (this.progressDialog != null) {
                this.progressDialog.setProgress(values[0]);
            }
        }

        protected void onPostExecute(String s) {
            if (!isCancelled()) {
                MainActivity.this.resultText.setText(s);
            }
            this.progressDialog.dismiss();
        }
    }


}
