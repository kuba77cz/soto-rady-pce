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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OneFragment extends Fragment {
    DatabaseHelper myDb;
    EditText editDen, editLinka, editCas;
    Button btnOdj, button;
    TextView tvNazevZast, tvVypisOdj;

    private AutoCompleteTextView autoComplete;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        try {
            myDb = new DatabaseHelper(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editDen = (EditText) rootView.findViewById(R.id.editDen);
        editLinka = (EditText) rootView.findViewById(R.id.editTextLinka);
        editCas = (EditText) rootView.findViewById(R.id.editCas);
        btnOdj = (Button) rootView.findViewById(R.id.btnVloz);
        button = (Button) rootView.findViewById(R.id.button);
        tvNazevZast = (TextView) rootView.findViewById(R.id.textViewNazevZast);
        tvVypisOdj = (TextView) rootView.findViewById(R.id.textViewVypis);

        String currentDate = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        //String svatek = new SimpleDateFormat("dMM", Locale.ENGLISH).format(new Date());
        //String svatky[] = {"0101", "2503", "2803", "0105", "0805", "0507", "0607", "2809", "2810", "1711", "2412", "2512", "2612" };
        editDen.setText("X");
        /*
        if (currentDate.equals("Sunday") || Arrays.asList(svatky).contains(svatek) || (currentDate.equals("Saturday"))) {
            editDen.setText("V");
        } else {
            editDen.setText("X");
        }
        */
        if (currentDate.equals("Friday")) {
            editDen.setText("5");
        } else {
            editDen.setText("X");
        }
        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        editCas.setText(currentTime);

        String[] zastavky = getResources().getStringArray(R.array.Zast);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item, R.id.text1, zastavky);
        autoComplete = (AutoCompleteTextView) rootView.findViewById(R.id.editZast);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1);
        autoComplete.requestFocus();

        autoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        editCas.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        viewAll();
        aktCas();
        smazZast();
        smazKurz();
        return rootView;
    }

    public void all() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editLinka.getWindowToken(), 0);
        Cursor res = myDb.getAllOdjezdy(editDen.getText().toString().toUpperCase(), autoComplete.getText().toString(), editLinka.getText().toString(), editCas.getText().toString());
        if (res.getCount() == 0) {
            autoComplete.setText("");
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (res.moveToNext()) {
            builder.append(res.getString(3) + "  " + res.getString(1) + "  " + res.getString(2) + "  " + res.getString(4) + " (" + res.getString(5) + ") \n");
        }
        tvNazevZast.setText(autoComplete.getText().toString());
        tvVypisOdj.setText(builder.toString());
    }

    public void viewAll() {
        btnOdj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        all();
                    }
                }
        );
    }

    public void aktCas() {
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                        editCas.setText(currentTime);
                    }
                }
        );
    }

    public void smazZast() {
        autoComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoComplete.setText("");
            }
        });
    }

    public void smazKurz() {
        editLinka.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             editLinka.setText("");
                                         }
                                     }
        );

    }
}