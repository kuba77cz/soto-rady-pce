package cz.jj.sotoradypce;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FourFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DatabaseHelper myDb;
    EditText editLinka;
    Button btnSpj;
    TextView tvLinka, tvVypis;
    String den;

    public FourFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_four, container, false);
        try {
            myDb = new DatabaseHelper(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editLinka = (EditText) rootView.findViewById(R.id.editLinka);
        btnSpj = (Button) rootView.findViewById(R.id.btnSpj);
        tvLinka = (TextView) rootView.findViewById(R.id.tvLinka);
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
        if (currentDate.equals("Saturday") || currentDate.equals("Sunday")  || Arrays.asList(svatky).contains(svatek)) {
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


        editLinka.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        editLinka.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editLinka.getWindowToken(), 0);
            }
        }, 100);
    }

    public void all() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editLinka.getWindowToken(), 0);

        if (!editLinka.getText().toString().isEmpty()) {
            try {
                Cursor res = myDb.getAllDataLine(editLinka.getText().toString(), den);
                if (res.getCount() == 0) {
                    Toast.makeText(getActivity(), "Linka neexistuje.", Toast.LENGTH_LONG).show();
                    editLinka.setText("");
                }
                StringBuilder builder = new StringBuilder();
                while (res.moveToNext()) {
                    builder.append(res.getString(0) + " - ");
                    builder.append(res.getString(3) + " " + res.getString(2) + " > ");
                    builder.append(res.getString(5) + " " + res.getString(4) + "\n");
                }

                tvLinka.setText(editLinka.getText().toString());
                tvVypis.setText(builder.toString());
                editLinka.setText("");

            } catch (Throwable e) {
                Toast.makeText(getActivity(), "Nepodařilo se načíst data.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Linka nebyla zadána.", Toast.LENGTH_LONG).show();
        }
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

    public void smazKurz() {
        editLinka.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             editLinka.setText("");
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