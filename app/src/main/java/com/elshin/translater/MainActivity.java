package com.elshin.translater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elshin.translater.models.DirTranslation;
import com.elshin.translater.models.Translation;
import com.elshin.translater.network.Repository;
import com.elshin.translater.network.ResponseCallback;
import com.elshin.translater.network.pojo.LanguagesResponse;
import com.elshin.translater.network.pojo.TextResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    Repository repository = Repository.getInstance();
    private Realm realm;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Realm.deleteRealm(Realm.getDefaultConfiguration()); //НЕНУЖНО (это для очистки всей БД при каждом запуске)

        realm = Realm.getDefaultInstance();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

        initButtonsClickListeners();
        initEditTextEditableListener();

        getSchedules();
    }

    private void getSchedules() {
        repository.getLanguages(new ResponseCallback<LanguagesResponse>() {//запрос к серверу на получение направлений возможных переводов
            @Override
            public void onEnd(LanguagesResponse apiResponse) {
                List<String> dirs = apiResponse.getDirs();
                initSpinner(dirs);
            }
        });
    }

    private void initButtonsClickListeners() {//слушатель нажатия на кнопку "Перевести"
        Button translateButton = findViewById(R.id.translate_btn);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);//показываем прогресс
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //скрываем клавиатуру
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                repository.getText(new ResponseCallback<TextResponse>() {//запрос на перевод текста
                    @Override
                    public void onEnd(TextResponse apiResponse) {//коллбэк (сюда попадаем, когда сервер отдал ответ)
                        EditText translatedText = findViewById(R.id.translatedText_et);
                        translatedText.setText(apiResponse.getText().get(0));//устаналиваем переведённый текст в edittext
                        ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.INVISIBLE);//скрываем прогресс
                    }
                }, ((EditText) findViewById(R.id.sourceText)).getText().toString(), ((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString());
            }
        });

        final ImageView likeButton = findViewById(R.id.like_btn);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        DirTranslation dir;
                        String dirStr = ((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString();

                        RealmResults<DirTranslation> results = realm.where(DirTranslation.class).equalTo("dir", dirStr).findAll();//находим направление перевода

                        if (results.size() == 0) {//если такого направления перевода нет в бд
                            dir = realm.createObject(DirTranslation.class);//создаём направление перевода в БД
                            dir.setDir(dirStr);
                        } else
                            dir = results.first();//иначе - получаем направление из бд

                        String translatedText = ((EditText) findViewById(R.id.translatedText_et)).getText().toString();
                        String sourceText = ((EditText) findViewById(R.id.sourceText)).getText().toString();

                        if (!translatedText.equals("")) {
                            dir.getTranslations().add(new Translation(sourceText, translatedText, dirStr));//добавляем к этому направлению перевода ещё один перевод

                            Toast toast = Toast.makeText(getApplicationContext(), R.string.added, Toast.LENGTH_LONG);
                            toast.show();
                            likeButton.setImageResource(R.drawable.ic_heart_fill);
                        }
                    }
                });
            }
        });

        Button showSavedTranslations = findViewById(R.id.show_saved_btn);

        showSavedTranslations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SavedTranslationsActivity.class);
                startActivity(intent);
            }
        });

        ImageView listenInputTextBtn = findViewById(R.id.listen_input_text_btn);

        listenInputTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sourceText = findViewById(R.id.sourceText);
                AppCompatSpinner spinner = findViewById(R.id.dirs);
                speak(spinner.getSelectedItem().toString().substring(0, 1), sourceText.getText().toString());
            }
        });

        ImageView listenTranslationButton = findViewById(R.id.listen_translation_btn);

        listenTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText translatedText = findViewById(R.id.translatedText_et);
                AppCompatSpinner spinner = findViewById(R.id.dirs);
                speak(spinner.getSelectedItem().toString().substring(3), translatedText.getText().toString());
            }
        });
    }

    private void speak(String language, String text) {
        if (text.length() == 0) {
            textToSpeech.speak("You haven't typed text", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            textToSpeech.setLanguage(new Locale(language));
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void initEditTextEditableListener() {//слушатель изменения текста
        EditText sourceEditText = findViewById(R.id.sourceText);

        sourceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((EditText) findViewById(R.id.translatedText_et)).setText("");//при изменении текста очищаем окно переведённого текста
                final ImageView likeButton = findViewById(R.id.like_btn);
                likeButton.setImageResource(R.drawable.ic_heart);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initSpinner(List<String> dirs) {
        AppCompatSpinner spinner = findViewById(R.id.dirs);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dirs);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

        //слушатель изменения выбора
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((EditText) findViewById(R.id.translatedText_et)).setText("");//при изменении направления перевода очищаем окно переведённого текста

                final ImageView likeButton = findViewById(R.id.like_btn);
                likeButton.setImageResource(R.drawable.ic_heart);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        spinner.setOnItemSelectedListener(itemSelectedListener);
    }
}