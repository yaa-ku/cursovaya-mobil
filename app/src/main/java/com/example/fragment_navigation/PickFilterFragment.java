package com.example.fragment_navigation;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PickFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    Integer diap;
    TextView textView;
    TextView textView2;
    TextView textView3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootV = inflater.inflate(com.example.fragment_navigation.R.layout.fragment_pick_filter, container, false);
        textView = (TextView) rootV.findViewById(R.id.textView);
        textView2 = (TextView) rootV.findViewById(R.id.textView2);
        textView3 = (TextView) rootV.findViewById(R.id.textView3);
        Button button3 = (Button) rootV.findViewById(R.id.button3);
        Button button1 = (Button) rootV.findViewById(R.id.button);
        Button button4 = (Button) rootV.findViewById(R.id.button4);
        Switch onOffSwitch = (Switch) rootV.findViewById(R.id.switch2);
        Switch onOffSwitchTemp = (Switch) rootV.findViewById(R.id.switch3);
        Switch onOffSwitchHumid = (Switch) rootV.findViewById(R.id.switch4);
        EditText temp = (EditText) rootV.findViewById(R.id.editTextNumber);
        EditText humid = (EditText) rootV.findViewById(R.id.editTextNumber2);
        Spinner spin = (Spinner) rootV.findViewById(R.id.spinner);
        Spinner spin2 = (Spinner) rootV.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.operations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);
        spin2.setAdapter(adapter);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textView2.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    button1.setText("Начальная дата");
                    textView.setVisibility(View.GONE);
                } else {
                    textView2.setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                    button1.setText("Выбрать дату");
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
        onOffSwitchTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    temp.setVisibility(View.VISIBLE);
                    spin.setVisibility(View.VISIBLE);
                } else {
                    temp.setVisibility(View.GONE);
                    spin.setVisibility(View.GONE);
                }
            }
        });
        onOffSwitchHumid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    humid.setVisibility(View.VISIBLE);
                    spin2.setVisibility(View.VISIBLE);
                } else {
                    humid.setVisibility(View.GONE);
                    spin2.setVisibility(View.GONE);
                }
            }
        });
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        temp.setVisibility(View.GONE);
        spin.setVisibility(View.GONE);
        humid.setVisibility(View.GONE);
        spin2.setVisibility(View.GONE);


        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String strDate = format.format(currentTime.getTime());
        //textView.setText(strDate);
        Button button = (Button) rootV.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diap = 1;
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getParentFragmentManager(), "date picker");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diap = 2;
                DialogFragment datePicker2 = new DatePickerFragment();
                datePicker2.show(getParentFragmentManager(), "date picker");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store.INSTANCE.setStartDate(textView3.getText().toString());
                if (onOffSwitch.isChecked()) {
                    Store.INSTANCE.setEndDate(textView2.getText().toString());
                } else {
                    Store.INSTANCE.setEndDate("");
                }
                if (onOffSwitchTemp.isChecked()) {
                    Store.INSTANCE.setValTemp(Integer.valueOf(temp.getText().toString()));
                } else {
                    Store.INSTANCE.setValTemp(Integer.valueOf(99));
                }
                if (onOffSwitchHumid.isChecked()) {
                    Store.INSTANCE.setValHumid(Integer.valueOf(humid.getText().toString()));
                } else {
                   Store.INSTANCE.setValHumid(Integer.valueOf(99));
                }
                if (spin.getSelectedItemPosition() == 0) {
                    Store.INSTANCE.setSelectedOperator1(0);
                } else if (spin.getSelectedItemPosition() == 1) {
                   Store.INSTANCE.setSelectedOperator1(1);
                } else if (spin.getSelectedItemPosition() == 2) {
                   Store.INSTANCE.setSelectedOperator1(2);
                } else {
                    Store.INSTANCE.setSelectedOperator1(3);
                }
                if (spin2.getSelectedItemPosition() == 0) {
                    Store.INSTANCE.setSelectedOperator2(0);
                } else if (spin2.getSelectedItemPosition() == 1) {
                    Store.INSTANCE.setSelectedOperator2(1);
                } else if (spin2.getSelectedItemPosition() == 2) {
                    Store.INSTANCE.setSelectedOperator2(2);
                } else {
                    Store.INSTANCE.setSelectedOperator2(3);
                }
                //Intent intent = new Intent(view.getContext(), Monitoring.class);
                //view.getContext().startActivity(intent);
                ChartsFragment nextFrag= new ChartsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return rootV;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String strDate = format.format(c.getTime());
        if (diap == 2) {
            // TextView textView2 = (TextView) findViewById(R.id.textView2);
            textView2.setText(strDate);
        }
        if (diap == 1) {
            //TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(strDate);
            //TextView textView3 = (TextView) findViewById(R.id.textView3);
            textView3.setText(strDate);
        }

    }
}