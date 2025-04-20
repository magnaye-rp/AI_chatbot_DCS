
package beans;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.DatePicker;
import java.util.AbstractMap.SimpleEntry;


import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.function.Predicate;

public class CustomDateTimePickerBean extends JPanel {

    private final DateTimePicker dateTimePicker;
    private LocalDateTime blockStart;
    private int blockDurationMinutes;
    private final java.util.List<SimpleEntry<LocalDateTime, Integer>> blockedRanges = new java.util.ArrayList<>();

    public CustomDateTimePickerBean() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        TimePickerSettings timeSettings = new TimePickerSettings();
        
        // Set color customization
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, new Color(230, 240, 255));
        
        dateTimePicker = new DateTimePicker(dateSettings, timeSettings);
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBackground(new Color(230, 255, 230));
        setLayout(new BorderLayout());
        add(dateTimePicker, BorderLayout.CENTER);
    }

    // 🔹 Set background color
    public void setPickerBackground(Color color) {
        dateTimePicker.getDatePicker().getComponentDateTextField().setBackground(color);
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBackground(color);
    }
    public void setBlockedDateTimeRange(LocalDateTime start, int durationMinutes) {
        blockedRanges.add(new SimpleEntry<>(start, durationMinutes));

        updateVetoPolicy();
    }
    
    public void clearBlockedDateTimeRanges() {
        blockedRanges.clear();
        updateVetoPolicy();
    }
    
    private void updateVetoPolicy() {
        Predicate<LocalDate> dateVeto = date -> {
            for (SimpleEntry<LocalDateTime, Integer> pair : blockedRanges) {
                LocalDateTime blockStart = pair.getKey();
                LocalDateTime blockEnd = blockStart.plusMinutes(pair.getValue());
                if (!date.isBefore(blockStart.toLocalDate()) && !date.isAfter(blockEnd.toLocalDate())) {
                    return true;
                }
            }
            return false;
        };

        Predicate<LocalTime> timeVeto = time -> {
            LocalDate selectedDate = dateTimePicker.getDatePicker().getDate();
            if (selectedDate == null) return false;
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, time);
            for (SimpleEntry<LocalDateTime, Integer> pair : blockedRanges) {
                LocalDateTime blockStart = pair.getKey();
                LocalDateTime blockEnd = blockStart.plusMinutes(pair.getValue());
                if (!selectedDateTime.isBefore(blockStart) && !selectedDateTime.isAfter(blockEnd)) {
                    return true;
                }
            }
            return false;
        };

        dateTimePicker.getDatePicker().getSettings().setVetoPolicy(dateVeto::test);
        dateTimePicker.getTimePicker().getSettings().setVetoPolicy(timeVeto::test);
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

