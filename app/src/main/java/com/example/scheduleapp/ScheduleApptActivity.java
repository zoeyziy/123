package com.example.scheduleapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleApptActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleApptActivity";

    private static final String KEY_USER = "user";
    private static final String KEY_COMPANY = "company";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TIME = "time";
    private static final String KEY_VEHICLE = "vehicle";
    private static final String KEY_DESC = "description";
    private static final String KEY_NOTES = "notes";


    private EditText editTextCompany;
    private EditText editTextLocation;
    private EditText editTextDate;

    private TextView editTextDateLabel;
    private TextView editTextTimeLabel;
    private Button buttonDate;
    private Button buttonTime;

    private EditText editTextVehicle;
    private EditText editTextDesc;
    private EditText editTextNotes;

    private Calendar apptCal = Calendar.getInstance();

    private long endTime, startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_appt);

        editTextCompany = findViewById(R.id.editCompany);
        editTextCompany.setText(getIntent().getStringExtra("title"));
        editTextLocation = findViewById(R.id.editLocation);
        editTextLocation.setText(getIntent().getExtras().get("latlng").toString());
        editTextDateLabel = findViewById(R.id.dateLabel);
        editTextTimeLabel = findViewById(R.id.timeLabel);
        buttonDate = findViewById(R.id.buttonDate);
        buttonTime = findViewById(R.id.buttonTime);

        editTextVehicle = findViewById(R.id.editVehicle);
        editTextDesc = findViewById(R.id.editDesc);
        editTextNotes = findViewById(R.id.editNotes);
    }

    public void editDateButton(View v) {

        final Calendar currentCal = Calendar.getInstance();
        int currentYear = currentCal.get(Calendar.YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH);
        int currentDate = currentCal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    Calendar pickerCal = Calendar.getInstance();
                    pickerCal.set(Calendar.YEAR, year);
                    pickerCal.set(Calendar.MONTH, monthOfYear);
                    pickerCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    if (pickerCal.before(currentCal)) {
                        Toast.makeText(ScheduleApptActivity.this, "Select a valid date.", Toast.LENGTH_SHORT).show();
                    } else {
                        apptCal.set(Calendar.YEAR, year);
                        apptCal.set(Calendar.MONTH, monthOfYear);
                        apptCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextDateLabel.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }
        }, currentYear, currentMonth, currentDate);
        datePickerDialog.getDatePicker().setMinDate(currentCal.getTimeInMillis());
        datePickerDialog.show();
    }

    public void editTimeButton(View v) {

        // Get Current Time
        final Calendar currentCal = Calendar.getInstance();
        int currentHour = currentCal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCal.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Calendar pickerTime = Calendar.getInstance();
                        pickerTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        pickerTime.set(Calendar.MINUTE, minute);

                        if (pickerTime.before(currentCal)) {
                            Toast.makeText(ScheduleApptActivity.this, "Select a valid date.", Toast.LENGTH_SHORT).show();
                        } else {
                            apptCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            apptCal.set(Calendar.MINUTE, minute);
                            editTextTimeLabel.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    public void submitAppt(View v) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> appt = new HashMap<>();
        appt.put(KEY_USER, auth.getCurrentUser().getEmail());
        appt.put(KEY_COMPANY, editTextCompany.getText().toString());
        appt.put(KEY_LOCATION, editTextLocation.getText().toString());
        appt.put(KEY_TIME, new Timestamp(apptCal.getTime()));
        appt.put(KEY_VEHICLE, editTextVehicle.getText().toString());
        appt.put(KEY_DESC, editTextDesc.getText().toString());
        appt.put(KEY_NOTES, editTextNotes.getText().toString());

        db.collection("appointments").add(appt)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ScheduleApptActivity.this, "Successfully scheduled appointment.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Appointment scheduled with id: " + documentReference.getId());
                        Intent userActivity = new Intent(ScheduleApptActivity.this, UserActivity.class);
                        ScheduleApptActivity.this.startActivity(userActivity);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleApptActivity.this, "Failed to schedule appointment.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        startTime = apptCal.getTimeInMillis();
        endTime = startTime + 30*60*1000;
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        //intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        intent.putExtra("beginTime", startTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra(CalendarContract.Events.TITLE, editTextDesc.getText().toString());

        startActivity(intent);
    }
}