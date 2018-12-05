package view.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import constant.Constant;
import models.CustomSpinnerAdapter;
import models.Variables;

/**
 * A simple {@link Fragment} subclass.
 */
public class Collection extends Fragment implements View.OnClickListener {


    private View fragmentView;
    private TextView chequeDate;
    private Button btnAdd;
    private EditText etxCAmount, etxCCAmount, etxCCNumber;
    private Spinner spnModeOfPayment, spnBank;
    private CheckBox cbxIsMultiple;
    private LinearLayout layoutChequq;
    private Calendar currentDate;
    private TableLayout tblCheque;
    private TableRow tableRow;
    private TableRow.LayoutParams layoutParams;

    public Collection() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_collection, container, false);
        loadView();
        initializeView();
        loadData();
        setHasOptionsMenu(true);
        return fragmentView;
    }

    private void loadView() {
        spnModeOfPayment = (Spinner) fragmentView.findViewById(R.id.spnCModeofPay);
        spnBank = (Spinner) fragmentView.findViewById(R.id.spnCCBank);
        chequeDate = (TextView) fragmentView.findViewById(R.id.txtCCDate);
        etxCAmount = (EditText) fragmentView.findViewById(R.id.etxCAmount);
        etxCCAmount = (EditText) fragmentView.findViewById(R.id.etxCCAmount);
        etxCCNumber = (EditText) fragmentView.findViewById(R.id.etxCCNumber);
        cbxIsMultiple = (CheckBox) fragmentView.findViewById(R.id.cbxCCIsMultiple);
        btnAdd = (Button) fragmentView.findViewById(R.id.btnCCAdd);
        layoutChequq = (LinearLayout) fragmentView.findViewById(R.id.linearCheque);
        tblCheque = (TableLayout) fragmentView.findViewById(R.id.tbl_layout_CCList);
    }

    private void initializeView() {
        btnAdd.setVisibility(View.GONE);
        tblCheque.setVisibility(View.GONE);
        setVisibility(View.GONE, View.GONE);
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tblCheque.setStretchAllColumns(true);
        getTableHeader();
        currentDate = Calendar.getInstance();
        cbxIsMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAdd.setVisibility(View.VISIBLE);
                    tblCheque.setVisibility(View.VISIBLE);
                } else {
                    btnAdd.setVisibility(View.GONE);
                    tblCheque.setVisibility(View.GONE);
                }
            }
        });
        btnAdd.setOnClickListener(this);
        chequeDate.setOnClickListener(this);
    }


    private void loadData() {
        String[] fa = {"Cheque", "Cash", "Transfer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, fa);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnModeOfPayment.setAdapter(adapter);
        spnBank.setAdapter(adapter);
        spnModeOfPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getTableHeader() {
        tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(layoutParams);

        TextView sNo = getTextView(getActivity(), true, "S.No");
        TextView bank = getTextView(getActivity(), true, "Bank");
        TextView date = getTextView(getActivity(), true, "Date");
        TextView number = getTextView(getActivity(), true, "Number");
        TextView delete = getTextView(getActivity(), true, "Delete");

        tableRow.addView(sNo);
        tableRow.addView(bank);
        tableRow.addView(date);
        tableRow.addView(number);
        tableRow.addView(delete);

        tblCheque.addView(tableRow, 0);
    }

    private TextView getTextView(Context context, Boolean isHeader, String setText) {
        TextView textView = new TextView(context);
        textView.setText(setText);
        textView.setGravity(Gravity.CENTER);
        if (isHeader) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(15);
            textView.setBackgroundResource(R.drawable.table_header);
        } else {
            textView.setTextSize(14);
            textView.setBackgroundResource(R.drawable.table_body);
        }
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void changeSpinner() {
        if (spnModeOfPayment.getSelectedItem() == "Cash" || spnModeOfPayment.getSelectedItem() == "Transfer") {
            setVisibility(View.VISIBLE, View.GONE);
        } else if (spnModeOfPayment.getSelectedItem() == "Cheque") {
            setVisibility(View.GONE, View.VISIBLE);
        }

    }

    private void setVisibility(int amount, int cheque) {
        etxCAmount.setVisibility(amount);
        layoutChequq.setVisibility(cheque);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), setDatepicker(), year, month, dayOfMonth);
        if (v.getId() == R.id.btnCCAdd) {
            if (checkValidation()) {
                addCheque();
            }
        }
        if (v.getId() == R.id.txtCCDate) {
            if (!chequeDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                Variables.calendarDate cal = new Variables.calendarDate(chequeDate.getText().toString());
                datePickerDialog = new DatePickerDialog(getActivity(), setDatepicker(), cal.getYear(), cal.getMonth(), cal.getDayofmonth());
            }
            datePickerDialog.getDatePicker().setTag(R.id.txtSRTDate);
            datePickerDialog.show();
        }
    }

    private void addCheque() {
        tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(layoutParams);
        Integer count = tblCheque.getChildCount();
        TextView sNo = getTextView(getActivity(), false, count.toString());
        final TextView bank = getTextView(getActivity(), false, spnBank.getSelectedItem().toString());
        TextView date = getTextView(getActivity(), false, chequeDate.getText().toString());
        TextView number = getTextView(getActivity(), false, etxCCNumber.getText().toString());
        TextView delete = getTextView(getActivity(), false, "");
        delete.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.ic_action_remove), null);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow row = (TableRow) v.getParent();
                tblCheque.removeView(row);
                setTableIndex();
            }
        });
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow temp = (TableRow) v;
                int position = tblCheque.indexOfChild(temp);
                if (position == 0) {
                    return;
                } else {
                    TextView txttemp = (TextView) temp.getChildAt(0);
                    spnBank.setSelection(0);
                    etxCCAmount.setText(txttemp.getText().toString());
                    etxCCNumber.setText(txttemp.getText().toString());
                }

            }
        });
        tableRow.addView(sNo);
        tableRow.addView(bank);
        tableRow.addView(date);
        tableRow.addView(number);
        tableRow.addView(delete);

        tblCheque.addView(tableRow, count);
        etxCCAmount.setText("");
        etxCCNumber.setText("");
    }

    private void setTableIndex() {
        int total = tblCheque.getChildCount();
        for (int j = 1; j <= total; j++) {
            View view = tblCheque.getChildAt(j);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                TextView t = (TextView) row.getChildAt(0);
                t.setText("" + (j + 1));
            }
        }
    }

    private boolean checkValidation() {
        if (chequeDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
            return false;
        }
        if (etxCCNumber.getText().toString().trim().length() == 0) {
            return false;
        }
        if (etxCCAmount.getText().toString().trim().length() == 0) {
            return false;
        }
        return true;
    }

    private DatePickerDialog.OnDateSetListener setDatepicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                chequeDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }

        };
        return onDateSetListener;
    }
}
