package cz.jj.sotoradypce;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TwoFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DatabaseHelper myDb;
    EditText editKurz;
    Button btnSpj;
    TextView tvKurz, tvVypis;
    String den;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        try {
            myDb = new DatabaseHelper(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editKurz = (EditText) rootView.findViewById(R.id.editLinka);
        btnSpj = (Button) rootView.findViewById(R.id.btnSpj);
        tvKurz = (TextView) rootView.findViewById(R.id.tvKurz);
        tvVypis = (TextView) rootView.findViewById(R.id.tvVypis);
        //tvVypis.setMovementMethod(new ScrollingMovementMethod());
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);

        String currentDate = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        String svatek = new SimpleDateFormat("dMM", Locale.ENGLISH).format(new Date());
        String svatky[] = {"0101", "2503", "2803", "0105", "0805", "0507", "0607", "2809", "2810", "1711", "2412", "2512", "2612"};

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        spinner.setDropDownWidth(90);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        if (currentDate.equals("Saturday") || currentDate.equals("Sunday") || Arrays.asList(svatky).contains(svatek)) {
            categories.add("V");
            categories.add("X");
            categories.add("5");
        } else if (currentDate.equals("Friday")) {
            categories.add("5");
            categories.add("V");
            categories.add("X");
        } else {
            categories.add("X");
            categories.add("5");
            categories.add("V");
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        viewAll();
        smazKurz();

        editKurz.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    all();
                    return true;
                } else {
                    return false;
                }
            }
        });

        return rootView;
    }

    public void onResume() {
        super.onResume();
        editKurz.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSpj.getWindowToken(), 0);
            }
        }, 100);
    }

    public void viewAll() {
        btnSpj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        all();
                    }
                }
        );
    }

    public void all() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnSpj.getWindowToken(), 0);

        if (!editKurz.getText().toString().isEmpty()) {
            try {
                Cursor res = myDb.getAllData(editKurz.getText().toString(), den);
                if (res.getCount() == 0) {
                    Toast.makeText(getActivity(), "Kurz neexistuje.", Toast.LENGTH_LONG).show();
                    editKurz.setText("");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                while (res.moveToNext()) {
                    builder.append(res.getString(1) + " - ");
                    builder.append(res.getString(3) + " " + res.getString(2) + " > ");
                    builder.append(res.getString(5) + " " + res.getString(4) + "\n");
                }
                String npX[] = {"101", "201", "202", "302", "303", "306", "307", "401", "502", "701", "1101", "1301", "1302", "1305", "2101", "2701",
                        "601", "602", "603", "605", "801", "802", "803", "804", "901", "1001", "1002", "1003", "1201", "1202", "1203", "1401", "1403", "1404",
                        "1501", "1701", "1801", "1802", "2502", "2801", "6101"};
                String kurz = editKurz.getText().toString();

                if (den.equals("X") && Arrays.asList(npX).contains(kurz)) {
                    tvKurz.setText(kurz);
                    tvKurz.setTextColor(Color.parseColor("#F44336"));
                } else {
                    tvKurz.setText(editKurz.getText().toString());
                    tvKurz.setTextColor(tvVypis.getTextColors());
                }

                tvVypis.setText(builder.toString());
                editKurz.setText("");

            } catch (Throwable e) {
                Toast.makeText(getActivity(), "Nepodařilo se načíst data.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Kurz nebyl zadán.", Toast.LENGTH_LONG).show();
        }
    }

    public void smazKurz() {
        editKurz.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            editKurz.setText("");
                                        }
                                    }
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String item = parent.getItemAtPosition(position).toString();
        den = item;
        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}