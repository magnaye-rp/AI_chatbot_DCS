package beans;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.AbstractMap.SimpleEntry;

public class CustomDateTimePickerBean extends JPanel {

    private final DateTimePicker dateTimePicker;
    private final List<SimpleEntry<LocalDateTime, Integer>> blockedRanges = new ArrayList<>();

    public CustomDateTimePickerBean() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        TimePickerSettings timeSettings = new TimePickerSettings();

        // Customize appearance
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, new Color(230, 240, 255));

        dateTimePicker = new DateTimePicker(dateSettings, timeSettings);

        updateVetoPolicy();

        // UI tweaks
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBackground(new Color(230, 255, 230));

        setLayout(new BorderLayout());
        add(dateTimePicker, BorderLayout.CENTER);
    }

    // UI customization helper
    public void setPickerBackground(Color color) {
        dateTimePicker.getDatePicker().getComponentDateTextField().setBackground(color);
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBackground(color);
    }

    // Set a blocked datetime range
    public void setBlockedDateTimeRange(LocalDateTime start, int durationMinutes) {
        blockedRanges.add(new SimpleEntry<>(start, durationMinutes));
        updateVetoPolicy();
    }

    public void clearBlockedDateTimeRanges() {
        blockedRanges.clear();
        updateVetoPolicy();
    }

    private void updateVetoPolicy() {
        // Clear any existing veto policies
        dateTimePicker.getDatePicker().getSettings().setVetoPolicy(null);

        TimePickerSettings timeSettings = dateTimePicker.getTimePicker().getSettings();
        timeSettings.setVetoPolicy(null);

        timeSettings.generatePotentialMenuTimes(TimePickerSettings.TimeIncrement.ThirtyMinutes, 
                                              LocalTime.of(7, 0), LocalTime.of(15, 0));

        
        timeSettings.setVetoPolicy(time -> {
            if (time == null) return false;

            LocalTime startAllowed = LocalTime.of(7, 0);
            LocalTime endAllowed = LocalTime.of(15, 0);
            if (time.isBefore(startAllowed) || time.isAfter(endAllowed)) {
                return false; // Veto times outside allowed range
            }

            // Then check if time falls within any blocked appointment
            LocalDate selectedDate = dateTimePicker.getDatePicker().getDate();
            if (selectedDate == null) return true; // Can't check if no date selected

            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, time);
            for (SimpleEntry<LocalDateTime, Integer> pair : blockedRanges) {
                LocalDateTime blockStart = pair.getKey();
                LocalDateTime blockEnd = blockStart.plusMinutes(pair.getValue());

                // Check if the selected time falls within this blocked range
                if ((selectedDateTime.isEqual(blockStart) || selectedDateTime.isAfter(blockStart)) && 
                    (selectedDateTime.isEqual(blockEnd) || selectedDateTime.isBefore(blockEnd))) {
                    return false; // Veto this time
                }
            }
            return true; // Allow all other times
        });
    }


    public LocalDateTime getDateTime() {
        if (dateTimePicker.getDatePicker().getDate() != null && dateTimePicker.getTimePicker().getTime() != null) {
            return LocalDateTime.of(
                    dateTimePicker.getDatePicker().getDate(),
                    dateTimePicker.getTimePicker().getTime()
            );
        }
        return null;
    }

    public void setDateTime(LocalDateTime dateTime) {
        dateTimePicker.setDateTimeStrict(dateTime);
    }

    public DateTimePicker getInternalPicker() {
        return dateTimePicker;
    }
}
