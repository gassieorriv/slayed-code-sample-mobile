package activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import Base.BaseEngage;
import fragments.fragment_date;
import models.users.Schedule;
import web.interfaces.UserInterface;
import static br.com.zbra.androidlinq.Linq.stream;

@SuppressLint("DefaultLocale")
public class ManageScheduleActivity  extends BaseActivity {
    private TextInputLayout mondayStart;
    private TextInputLayout mondayEnd;
    private TextInputLayout tuesdayStart;
    private TextInputLayout tuesdayEnd;
    private TextInputLayout wednesdayStart;
    private TextInputLayout wednesdayEnd;
    private TextInputLayout thursdayStart;
    private TextInputLayout thursdayEnd;
    private TextInputLayout fridayStart;
    private TextInputLayout fridayEnd;
    private TextInputLayout saturdayStart;
    private TextInputLayout saturdayEnd;
    private TextInputLayout sundayStart;
    private TextInputLayout sundayEnd;
    private Context context;
    public List<Schedule> schedules;
    public List<Schedule> customSchedule;
    public List<Schedule> deletedSchedules;
    public LinearProgressIndicator progressIndicator;
    private TableLayout scheduleTableLayout;

    private String customDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schedule);
        context = this;
        deletedSchedules = new ArrayList<>();
        scheduleTableLayout = findViewById(R.id.scheduleTableLayout);
        progressIndicator = findViewById(R.id.progress_bar);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setupDeadEndMenu("Manage Schedule");
            schedules = new ArrayList<>();
            customSchedule = new ArrayList<>();
            setSubmit();
            UserInterface userInterface = getSchedule();
            userInterface.GetUserSchedule();
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void setFields() {
        if(BaseEngage.user != null && BaseEngage.user.userSchedule != null && BaseEngage.user.userSchedule.length > 0) {
            Schedule sunday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 1).first();
            Schedule monday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 2).first();
            Schedule tuesday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 3).first();
            Schedule wednesday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 4).first();
            Schedule thursday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 5).first();
            Schedule friday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 6).first();
            Schedule saturday = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 7).first();
            List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.hour_array));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);

            if(sunday.closed) {
                ((AutoCompleteTextView) Objects.requireNonNull(sundayStart.getEditText())).setText(adapter.getItem(0), false);
                ((AutoCompleteTextView) Objects.requireNonNull(sundayEnd.getEditText())).setText(adapter.getItem(0), false);
            } else {
                String sundayFormattedMinute = String.format("%02d", sunday.startMinute);
                String sundayClosedFormattedMinute = String.format("%02d", sunday.closedMinute);
                String sundayValue = (sunday.startHour > 12) ?
                        (sunday.startHour - 12) + ":" + sundayFormattedMinute + " PM" :
                        sunday.startHour + ":" + sundayFormattedMinute + " AM";
                Objects.requireNonNull(sundayStart.getEditText()).setText(sundayValue);

                String sundayEndValue = (sunday.closedHour > 12) ?
                        (sunday.closedHour - 12) + ":" + sundayClosedFormattedMinute + " PM" :
                        sunday.closedHour + ":" + sundayClosedFormattedMinute + " AM";
                Objects.requireNonNull(sundayEnd.getEditText()).setText(sundayEndValue);
            }

            if(monday.closed) {
                Objects.requireNonNull(mondayStart.getEditText()).setText("Closed");
                Objects.requireNonNull(mondayEnd.getEditText()).setText("Closed");
            } else {
                @SuppressLint("DefaultLocale")
                String mondayFormattedMinute = String.format("%02d", monday.startMinute);
                String mondayClosedFormattedMinute = String.format("%02d", monday.closedMinute);
                String mondayValue = (monday.startHour > 12) ?
                        (monday.startHour - 12) + ":" + mondayFormattedMinute + " PM" :
                        monday.startHour + ":" + mondayFormattedMinute + " AM";
                if(mondayStart != null && mondayStart.getEditText() != null) {
                    mondayStart.getEditText().setText(mondayValue);
                }

                String mondayEndValue = (monday.closedHour > 12) ?
                        (monday.closedHour - 12) + ":" + mondayClosedFormattedMinute + " PM" :
                        monday.closedHour + ":" + mondayClosedFormattedMinute + " AM";
                Objects.requireNonNull(mondayEnd.getEditText()).setText(mondayEndValue);
            }

            if(tuesday.closed) {
                Objects.requireNonNull(tuesdayEnd.getEditText()).setText("Closed");
                Objects.requireNonNull(tuesdayStart.getEditText()).setText("Closed");
            } else {
                String tuesdayFormattedMinute = String.format("%02d", tuesday.startMinute);
                String tuesdayClosedFormattedMinute = String.format("%02d", tuesday.closedMinute);
                String tuesdayValue = (tuesday.startHour > 12) ?
                        (tuesday.startHour - 12) + ":" + tuesdayFormattedMinute + " PM" :
                        tuesday.startHour + ":" + tuesdayFormattedMinute + " AM";
                Objects.requireNonNull(tuesdayStart.getEditText()).setText(tuesdayValue);

                String tuesdayEndValue = (tuesday.closedHour > 12) ?
                        (tuesday.closedHour - 12) + ":" + tuesdayClosedFormattedMinute + " PM" :
                        tuesday.closedHour + ":" + tuesdayClosedFormattedMinute + " AM";
                Objects.requireNonNull(tuesdayEnd.getEditText()).setText(tuesdayEndValue);
            }

            if(wednesday.closed) {
                Objects.requireNonNull(wednesdayStart.getEditText()).setText("Closed");
                Objects.requireNonNull(wednesdayEnd.getEditText()).setText("Closed");
            } else {
                String wednesdayFormattedMinute = String.format("%02d", wednesday.startMinute);
                String wednesdayClosedFormattedMinute = String.format("%02d", wednesday.closedMinute);
                String wednesdayValue = (wednesday.startHour > 12) ?
                        (wednesday.startHour - 12) + ":" + wednesdayFormattedMinute + " PM" :
                        wednesday.startHour + ":" + wednesdayFormattedMinute + " AM";
                Objects.requireNonNull(wednesdayStart.getEditText()).setText(wednesdayValue);

                String wednesdayEndValue = (wednesday.closedHour > 12) ?
                        (wednesday.closedHour - 12) + ":" + wednesdayClosedFormattedMinute + " PM" :
                        wednesday.closedHour + ":" + wednesdayClosedFormattedMinute + " AM";
                Objects.requireNonNull(wednesdayEnd.getEditText()).setText(wednesdayEndValue);
            }

            if(thursday.closed) {
                Objects.requireNonNull(thursdayStart.getEditText()).setText("Closed");
                Objects.requireNonNull(thursdayEnd.getEditText()).setText("Closed");
            } else {
                String thursdayFormattedMinute = String.format("%02d", thursday.startMinute);
                String thursdayClosedFormattedMinute = String.format("%02d", thursday.closedMinute);
                String thursdayValue = (thursday.startHour > 12) ?
                        (thursday.startHour - 12) + ":" + thursdayFormattedMinute + " PM" :
                        thursday.startHour + ":" + thursdayFormattedMinute + " AM";
                Objects.requireNonNull(thursdayStart.getEditText()).setText(thursdayValue);

                String thursdayEndValue = (thursday.closedHour > 12) ?
                        (thursday.closedHour - 12) + ":" + thursdayClosedFormattedMinute + " PM" :
                        thursday.closedHour + ":" + thursdayClosedFormattedMinute + " AM";
                Objects.requireNonNull(thursdayEnd.getEditText()).setText(thursdayEndValue);
            }

            if(friday.closed) {
                Objects.requireNonNull(fridayStart.getEditText()).setText("Closed");
                Objects.requireNonNull(fridayEnd.getEditText()).setText("Closed");
            } else {
                String fridayFormattedMinute = String.format("%02d", friday.startMinute);
                String fridayClosedFormattedMinute = String.format("%02d", friday.closedMinute);
                String fridayValue = (friday.startHour > 12) ?
                        (friday.startHour - 12) + ":" + fridayFormattedMinute + " PM" :
                        friday.startHour + ":" + fridayFormattedMinute + " AM";
                Objects.requireNonNull(fridayStart.getEditText()).setText(fridayValue);

                String fridayEndValue = (friday.closedHour > 12) ?
                        (friday.closedHour - 12) + ":" + fridayClosedFormattedMinute + " PM" :
                        friday.closedHour + ":" + fridayClosedFormattedMinute + " AM";
                Objects.requireNonNull(fridayEnd.getEditText()).setText(fridayEndValue);
            }

            if(saturday.closed) {
                Objects.requireNonNull(saturdayStart.getEditText()).setText("Closed");
                Objects.requireNonNull(saturdayEnd.getEditText()).setText("Closed");
            } else {
                String saturdayStartFormattedMinute = String.format("%02d", saturday.startMinute);
                String saturdayClosedFormattedMinute = String.format("%02d", saturday.closedMinute);
                String saturdayValue = (saturday.startHour > 12) ?
                        (saturday.startHour - 12) + ":" + saturdayStartFormattedMinute + " PM" :
                        saturday.startHour + ":" + saturdayStartFormattedMinute + " AM";
                Objects.requireNonNull(saturdayStart.getEditText()).setText(saturdayValue);

                String staurdayEndValue = (saturday.closedHour > 12) ?
                        (saturday.closedHour - 12) + ":" + saturdayClosedFormattedMinute + " PM" :
                        saturday.closedHour + ":" + saturdayClosedFormattedMinute + " AM";
                Objects.requireNonNull(saturdayEnd.getEditText()).setText(staurdayEndValue);
            }

            SetCustomDate();
        }
    }

    private void loadSpinners() {
        mondayStart = findViewById(R.id.monday_start);
        mondayEnd = findViewById(R.id.monday_end);
        tuesdayStart = findViewById(R.id.tuesday_start);
        tuesdayEnd = findViewById(R.id.tuesday_end);
        wednesdayStart = findViewById(R.id.wednesday_start);
        wednesdayEnd = findViewById(R.id.wednesday_end);
        thursdayStart = findViewById(R.id.thursday_start);
        thursdayEnd = findViewById(R.id.thursday_end);
        fridayStart = findViewById(R.id.friday_start);
        fridayEnd = findViewById(R.id.friday_end);
        saturdayStart = findViewById(R.id.saturday_start);
        saturdayEnd = findViewById(R.id.saturday_end);
        sundayStart = findViewById(R.id.sunday_start);
        sundayEnd = findViewById(R.id.sunday_end);

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.hour_array));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);

        ((AutoCompleteTextView) Objects.requireNonNull(mondayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(mondayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(tuesdayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(tuesdayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(wednesdayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(wednesdayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(thursdayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(thursdayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(fridayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(fridayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(saturdayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(saturdayEnd.getEditText())).setAdapter(adapter);

        ((AutoCompleteTextView) Objects.requireNonNull(sundayStart.getEditText())).setAdapter(adapter);
        ((AutoCompleteTextView) Objects.requireNonNull(sundayEnd.getEditText())).setAdapter(adapter);
    }

    private UserInterface getSchedule() {
        return new UserInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                BaseEngage.user.userSchedule = new Gson().fromJson(data.toString(), Schedule[].class);
                loadSpinners();
                setFields();
                loadSpinners();
            } else {
                Toast.makeText(context, "Error getting user schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private UserInterface updateSchedule() {
        return new UserInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                BaseEngage.user.userSchedule = new Gson().fromJson(data.toString(), Schedule[].class);
                Toast.makeText(context, "Schedule Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, "Error updating user schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSubmit() {
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return  false;
        });
    }

     @RequiresApi(api = Build.VERSION_CODES.N)
     public void Submit() {
        progressIndicator.setVisibility(View.VISIBLE);
        UserInterface userInterface = updateSchedule();
        if(BaseEngage.user.userSchedule != null && BaseEngage.user.userSchedule.length <= 0) {
            schedules.add(setDaySchedule(1));
            schedules.add(setDaySchedule(2));
            schedules.add(setDaySchedule(3));
            schedules.add(setDaySchedule(4));
            schedules.add(setDaySchedule(5));
            schedules.add(setDaySchedule(6));
            schedules.add(setDaySchedule(7));
            userInterface.CreateUserSchedule(schedules);
        } else {
            List<Schedule> existingSchedule = new ArrayList<>(getCustomDates());
            int sundayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 1).first().id;
            int mondayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 2).first().id;
            int tuesdayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 3).first().id;
            int wednesdayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 4).first().id;
            int thursdayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 5).first().id;
            int fridayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 6).first().id;
            int saturdayId = stream(BaseEngage.user.userSchedule).where(x -> x.dayOfWeekId == 7).first().id;
            Schedule Sunday = setDaySchedule(1);
            Sunday.id = sundayId;
            existingSchedule.add(Sunday);
            Schedule Monday = setDaySchedule(2);
            Monday.id = mondayId;
            existingSchedule.add(Monday);
            Schedule Tuesday = setDaySchedule(3);
            Tuesday.id = tuesdayId;
            existingSchedule.add(Tuesday);
            Schedule Wednesday = setDaySchedule(4);
            Wednesday.id = wednesdayId;
            existingSchedule.add(Wednesday);
            Schedule Thursday = setDaySchedule(5);
            Thursday.id = thursdayId;
            existingSchedule.add(Thursday);
            Schedule Friday = setDaySchedule(6);
            Friday.id = fridayId;
            existingSchedule.add(Friday);
            Schedule Saturday = setDaySchedule(7);
            Saturday.id = saturdayId;
            existingSchedule.add(Saturday);
            userInterface.UpdateUserSchedule(existingSchedule);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Schedule> getCustomDates() {
        List<Schedule> scheduleList = new ArrayList<>();
        for(int index = 0; index < scheduleTableLayout.getChildCount(); index++) {
            View scheduleView = scheduleTableLayout.getChildAt(index);
            if(scheduleView != null) {
                Schedule schedule = new Schedule();
                Schedule attachedSchedule = (Schedule)scheduleView.getTag();
                if(attachedSchedule != null) {
                    schedule.id = attachedSchedule.id;
                }
                TextInputLayout start = scheduleView.findViewById(R.id.custom_date_start);
                TextInputLayout closed = scheduleView.findViewById(R.id.custom_date_end);
                MaterialTextView date = scheduleView.findViewById(R.id.custom_date_text);
                schedule.deleted = false;
               if(start != null && closed != null && date != null) {
                   schedule.userId = BaseEngage.user.id;
                   schedule.closed = false;
                   schedule.specificDate = date.getText().toString() + "T00:00:00";
                   String[] startHourMinutes = new String[0];
                   String[] closedHourMinutes = new String[0];

                   if (Objects.requireNonNull(start.getEditText()).getText().toString().equals("Closed") ||
                           Objects.requireNonNull(closed.getEditText()).getText().toString().equals("Closed")) {
                       schedule.closed = true;
                   } else {
                       startHourMinutes = Objects.requireNonNull(start.getEditText()).getText().toString()
                               .replace("AM", "")
                               .split(":");
                       closedHourMinutes = Objects.requireNonNull(closed.getEditText()).getText().toString()
                               .replace("AM", "")
                               .split(":");
                   }
                   if (startHourMinutes.length > 1) {
                       if (startHourMinutes[1].contains("PM")) {
                           schedule.startHour = Integer.parseInt(startHourMinutes[0].trim()) + 12;
                       } else {
                           schedule.startHour = Integer.parseInt(startHourMinutes[0].trim());
                       }
                       schedule.startMinute = Integer.parseInt(startHourMinutes[1].replace("PM", "").trim());
                   }

                   if (closedHourMinutes.length > 1) {
                       if (closedHourMinutes[1].contains("PM")) {
                           schedule.closedHour = Integer.parseInt(closedHourMinutes[0].trim()) + +12;
                       } else {
                           schedule.closedHour = Integer.parseInt(closedHourMinutes[0].trim());
                       }
                       schedule.closedMinute = Integer.parseInt(closedHourMinutes[1].replace("PM", "").trim());
                   }

                   if(deletedSchedules != null && deletedSchedules.size() > 0) {
                       Schedule existingSchedule = stream(deletedSchedules).where(x -> x != null && x.specificDate.equals(schedule.specificDate)).firstOrDefault(null);
                       if(existingSchedule != null) {
                           deletedSchedules.remove(existingSchedule);
                       }
                   }
                   scheduleList.add(schedule);
               }
            }
        }

        scheduleList.addAll(deletedSchedules);
        return scheduleList;
    }

    private void SetCustomDate() {
        List<Schedule> specificDateList = stream(BaseEngage.user.userSchedule)
                .where(x -> x.dayOfWeekId < 1 || x.specificDate != null).toList();
        for(int i = 0; i < specificDateList.size(); i++) {
            addNewDay(specificDateList.get(i));
        }
    }

    private Schedule setDaySchedule(int day) {
        Schedule schedule = new Schedule();
        schedule.userId = BaseEngage.user.id;
        schedule.deleted = false;
        schedule.closed = false;
        schedule.specificDate = null;
        String[] startHourMinutes = new String[0];
        String[] closedHourMinutes = new String[0];

        switch (day) {
            case 1:
                schedule.dayOfWeekId = 1;
                if(Objects.requireNonNull(sundayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(sundayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = sundayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = sundayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 2:
                schedule.dayOfWeekId = 2;
                if(Objects.requireNonNull(mondayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(mondayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = mondayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = mondayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 3:
                schedule.dayOfWeekId = 3;
                if(Objects.requireNonNull(tuesdayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(tuesdayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = tuesdayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = tuesdayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 4:
                schedule.dayOfWeekId = 4;
                if(Objects.requireNonNull(wednesdayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(wednesdayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = wednesdayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = wednesdayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 5:
                schedule.dayOfWeekId = 5;
                if(Objects.requireNonNull(thursdayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(thursdayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = thursdayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = thursdayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 6:
                schedule.dayOfWeekId = 6;
                if(Objects.requireNonNull(fridayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(fridayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = fridayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = fridayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            case 7:
                schedule.dayOfWeekId = 7;
                if(Objects.requireNonNull(saturdayStart.getEditText()).getText().toString().equals("Closed") ||
                   Objects.requireNonNull(saturdayEnd.getEditText()).getText().toString().equals("Closed")) {
                    schedule.closed = true;
                } else {
                    startHourMinutes = saturdayStart.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                    closedHourMinutes = saturdayEnd.getEditText().getText().toString()
                            .replace("AM", "")
                            .split(":");
                }
                break;
            default:
               schedule.closed = true;
               break;
        }

        if(startHourMinutes.length > 1) {
            if(startHourMinutes[1].contains("PM")) {
                schedule.startHour = Integer.parseInt(startHourMinutes[0].trim()) + 12;
            } else {
                schedule.startHour = Integer.parseInt(startHourMinutes[0].trim());
            }
            schedule.startMinute = Integer.parseInt(startHourMinutes[1].replace("PM","").trim());
        }

        if(closedHourMinutes.length > 1) {
            if(closedHourMinutes[1].contains("PM")) {
                schedule.closedHour = Integer.parseInt(closedHourMinutes[0].trim()) + + 12;
            } else {
                schedule.closedHour = Integer.parseInt(closedHourMinutes[0].trim());
            }
            schedule.closedMinute = Integer.parseInt(closedHourMinutes[1].replace("PM", "").trim());
        }

        return  schedule;
    }

    @SuppressLint("SetTextI18n")
    private void addNewDay(Schedule schedule) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;

        if (inflater != null) {
            view = inflater.inflate(R.layout.layout_custom_schedule, scheduleTableLayout, false);
        }

        if (view != null) {
            customSchedule.add(schedule);
            view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setTag(schedule);
            row.addView(view);
            scheduleTableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            ImageButton close = view.findViewById(R.id.schedule_delete);
            TextInputLayout start = view.findViewById(R.id.custom_date_start);
            TextInputLayout closed = view.findViewById(R.id.custom_date_end);
            MaterialTextView date = view.findViewById(R.id.custom_date_text);

            List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.hour_array));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);

            ((AutoCompleteTextView) Objects.requireNonNull(start.getEditText())).setAdapter(adapter);
            ((AutoCompleteTextView) Objects.requireNonNull(closed.getEditText())).setAdapter(adapter);
            if(schedule != null) {
                if(schedule.closed) {
                    ((AutoCompleteTextView) start.getEditText()).setText(adapter.getItem(0), false);
                    ((AutoCompleteTextView) closed.getEditText()).setText(adapter.getItem(0), false);
                } else {
                    String saturdayStartFormattedMinute = String.format("%02d", schedule.startMinute);
                    String saturdayClosedFormattedMinute = String.format("%02d", schedule.closedMinute);
                    String saturdayValue = (schedule.startHour > 12) ?
                            (schedule.startHour - 12) + ":" + saturdayStartFormattedMinute + " PM" :
                            schedule.startHour + ":" + saturdayStartFormattedMinute + " AM";
                    start.getEditText().setText(saturdayValue);

                    String staurdayEndValue = (schedule.closedHour > 12) ?
                            (schedule.closedHour - 12) + ":" + saturdayClosedFormattedMinute + " PM" :
                            schedule.closedHour + ":" + saturdayClosedFormattedMinute + " AM";
                    closed.getEditText().setText(staurdayEndValue);
                }
                if(schedule.specificDate != null) {
                    date.setText(schedule.specificDate.replace("T00:00:00", ""));
                }
                else {
                    date.setText("Unknown");
                }
            } else {
                Schedule existingSchedule = stream(customSchedule).where(x -> x != null && x.specificDate.replace("T00:00:00", "").equals(customDate)).firstOrDefault(null);
                if(existingSchedule != null) {
                    customSchedule.remove(existingSchedule);
                }
                date.setText(customDate);
                customDate = "";
            }
            View finalView = view;
            close.setOnClickListener(v -> {
                row.removeView(finalView);
                scheduleTableLayout.removeView(row);
                scheduleTableLayout.removeView(finalView);
               if( schedule != null && schedule.id > 0) {
                    schedule.deleted = true;
                    deletedSchedules.add(schedule);
                } else {
                    customSchedule.remove(schedule);
                }
            });
        }
    }

    public void showStartDatePickerDialog(View view) {
        DialogFragment dateFragment = new fragment_date();
        dateFragment.show(getSupportFragmentManager(), "startDate");
    }

    public void getDate(String value) {
        customDate = value;
        addNewDay(null);
    }
}
