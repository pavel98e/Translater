package com.elshin.translater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elshin.translater.models.DirTranslation;
import com.elshin.translater.models.Translation;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SavedTranslationsActivity extends AppCompatActivity {
    Realm realm;
    RecyclerView rv;
    AdapterDB adapter;
    List<Translation> translations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_translations);

        setTitle("Сохранённые переводы");
        initRecyclerView();
        initSpinner();

        if (!((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString().equals(""))
            getTranslationsFromDB(((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString());
    }

    private void initSpinner() {
        realm = Realm.getDefaultInstance();
        List<String> dirs = new ArrayList<>();
        RealmResults<DirTranslation> dirTranslations = realm.where(DirTranslation.class).findAll();//берем все направления перевода из бд
        for (int i = 0; i < dirTranslations.size(); i++) {
            String curDir = dirTranslations.get(i).getDir();
            boolean add = true;

            for (int j = 0; j < dirs.size() && add; j++)
                if (dirs.get(j).equals(curDir))//не добавляем уже добавленные направления перевода
                    add = false;

            if (add)
                dirs.add(curDir);//заполняем выпадающий список значениями направлений перевода
        }

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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String item = (String) parent.getItemAtPosition(position);

                //получаем переводы сохранённых текстов для конкретного направления перевода
                getTranslationsFromDB(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    // инициализация RecyclerView
    private void initRecyclerView() {
        rv = findViewById(R.id.rvDB);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        adapter = new AdapterDB(translations);
        rv.setAdapter(adapter);
    }

    public void clearDB(View v) {//удаление данных из бд с выбранным направлением перевода
        try {
            final RealmResults<Translation> translationsFromDB = realm.where(Translation.class)
                    .equalTo("dirTranslation.dir", ((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString()).findAll();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    translationsFromDB.deleteAllFromRealm();
                }
            });

            Toast.makeText(this, "Данные успешно удалены", Toast.LENGTH_LONG).show();
            getTranslationsFromDB(((AppCompatSpinner) findViewById(R.id.dirs)).getSelectedItem().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Во время удаления данных произошла ошибка", Toast.LENGTH_LONG).show();
        }
    }

    private void getTranslationsFromDB(String dir) {//получение переводов сохранённых текстов для конкретного направления перевода
        translations.clear();

        RealmResults<Translation> schedulesFromDB = realm.where(Translation.class)
                .equalTo("dirTranslation.dir", dir).findAll();

        translations.addAll(schedulesFromDB);

        adapter.notifyDataSetChanged();
    }
}