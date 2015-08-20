package yuanchieh.voicerecognize;

//reference :
//1.http://cw1057.blogspot.tw/2011/10/android_25.html
//2.http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity {

    Button btnVoice;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private AlertDialog.Builder alert ;
    private AlertDialog dialog;
    private static final int S2T_RESULT_CODE = 1116;
    private static final String TAG = "Speech2TextActivity";
    private TextView txtVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnVoice = (Button)findViewById(R.id.btn_voice);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        alert = new AlertDialog.Builder(MainActivity.this);

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show up alert dialog
                alert.setTitle("Title");
                alert.setMessage("Message");
                txtVoice = new TextView (MainActivity.this);
                alert.setView(txtVoice);
                alert.setPositiveButton("Start", null);
                alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MainActivity.this,txtVoice.getText(),Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = alert.create();
                dialog.show();

                //override a new positive button
                //click the button,start voice recognize
                //WARN : this function must show up after the dialog.show() , or it would be null
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說...");
                        MainActivity.this.startActivityForResult(intent, S2T_RESULT_CODE);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != S2T_RESULT_CODE) {
            Log.d(TAG, "The request code doesn't match - " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Log.d(TAG, "The result code doesn't match - " + resultCode);
            return;
        }
        // 語音辨識成功後，將結果回寫
        // only retrieve one result
        List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        txtVoice.setText(result.get(0).toString());
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
