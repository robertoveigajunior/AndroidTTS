package br.com.robertoveigajunior.androidtts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class TTS extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    //Objeto TTS
    private TextToSpeech tts;
    //Codigo de Verificacao
    private int REQUEST_TTS = 0;
    private int REQ_CODE_SPEECH_INPUT = 1;
    private int BT_CLICADO;
    private TextView txtText;
    private EditText etTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        Button botao = (Button) findViewById(R.id.btFalar);
        botao.setOnClickListener(this);

        Button botaoEscutar = (Button) findViewById(R.id.btEscutar);
        botaoEscutar.setOnClickListener(this);

        etTexto = (EditText) findViewById(R.id.etTexto);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(
                TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        startActivityForResult(checkTTSIntent, REQUEST_TTS);
        txtText = (TextView) findViewById(R.id.txtResult);
    }

    @Override
    public void onClick(View view) {
        BT_CLICADO = view.getId();
        if (BT_CLICADO == R.id.btFalar) {
            String texto = etTexto.getText().toString();
            falar(texto);
        } else if (BT_CLICADO == R.id.btEscutar) {
            escutar();
        }
    }

    @Override
    public void onInit(int initStatus) {
        //Verificao se foi instalado com sucesso
        if (initStatus == TextToSpeech.SUCCESS) {

            if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro TTS", Toast.LENGTH_LONG).show();
        }
    }

    private void falar(String texto) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        } else if (requestCode == REQ_CODE_SPEECH_INPUT) {
            {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtText.setText(result.get(0));
                }
            }
        }
    }

    private void escutar() {
        Intent intent = new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Fala que é noiz");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "opssss, de ruim",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
