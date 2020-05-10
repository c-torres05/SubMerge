package com.example.submerge.calendar.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.example.submerge.R;
import com.example.submerge.calendar.CalendarView;
import com.example.submerge.calendar.EventDay;
import com.example.submerge.calendar.adapters.CalendarPageAdapter;
import com.example.submerge.calendar.utils.CalendarProperties;
import com.example.submerge.calendar.utils.DateUtils;
import com.example.submerge.calendar.utils.DayColorsUtils;
import com.example.submerge.calendar.utils.EventDayUtils;
import com.example.submerge.calendar.utils.ImageUtils;
import com.example.submerge.calendar.utils.SelectedDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * This class is responsible for loading a one day cell.
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

class CalendarDayAdapter extends ArrayAdapter<Date> {
    private CalendarPageAdapter mCalendarPageAdapter;
    private LayoutInflater mLayoutInflater;
    private int mPageMonth;
    private Calendar mToday = DateUtils.getCalendar();

    private CalendarProperties mCalendarProperties;

    CalendarDayAdapter(CalendarPageAdapter calendarPageAdapter, Context context, CalendarProperties calendarProperties,
                       ArrayList<Date> dates, int pageMonth) {
        super(context, calendarProperties.getItemLayoutResource(), dates);
        mCalendarPageAdapter = calendarPageAdapter;
        mCalendarProperties = calendarProperties;
        mPageMonth = pageMonth < 0 ? 11 : pageMonth;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mLayoutInflater.inflate(mCalendarProperties.getItemLayoutResource(), parent, false);
        }

        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);
        GridLayout eventGrid = (GridLayout) view.findViewById(R.id.event_grid);

        Calendar day = new GregorianCalendar();
        day.setTime(getItem(position));

        // Loading an image of the event
        if (eventGrid!= null) {
            loadIcons(eventGrid, day);
        }

        setLabelColors(dayLabel, day);

        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));
        return view;
    }

    private void setLabelColors(TextView dayLabel, Calendar day) {
        // Setting not current month day color
        if (!isCurrentMonthDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getAnotherMonthsDaysLabelsColor(),
                    Typeface.NORMAL, R.drawable.background_transparent);
            return;
        }

        // Setting view for all SelectedDays
        if (isSelectedDay(day)) {
            Stream.of(mCalendarPageAdapter.getSelectedDays())
                    .filter(selectedDay -> selectedDay.getCalendar().equals(day))
                    .findFirst().ifPresent(selectedDay -> selectedDay.setView(dayLabel));

            DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);
            return;
        }

        // Setting disabled days color
        if (!isActiveDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getDisabledDaysLabelsColor(),
                    Typeface.NORMAL, R.drawable.background_transparent);
            return;
        }

        // Setting custom label color for event day
        if (isEventDayWithLabelColor(day)) {
            DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);
            return;
        }

        // Setting current month day color
        DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);
    }

    private boolean isSelectedDay(Calendar day) {
        return mCalendarProperties.getCalendarType() != CalendarView.CLASSIC && day.get(Calendar.MONTH) == mPageMonth
                && mCalendarPageAdapter.getSelectedDays().contains(new SelectedDay(day));
    }

    private boolean isEventDayWithLabelColor(Calendar day) {
        return EventDayUtils.isEventDayWithLabelColor(day, mCalendarProperties);
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth && !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate())) || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private boolean isActiveDay(Calendar day) {
        return !mCalendarProperties.getDisabledDays().contains(day);
    }

    private void loadIcons(GridLayout eventGrid, Calendar day) {
        if (mCalendarProperties.getEventDays() == null || !mCalendarProperties.getEventsEnabled()) {
            eventGrid.setVisibility(View.GONE);
            return;
        }

        List<EventDay> days = mCalendarProperties.getEventDays();
        eventGrid.removeAllViews();
        days.forEach(eventDay -> {
            if (eventDay.getCalendar().get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)) {
                ImageView new_event = new ImageView(getContext());
                Log.i("SubMerge", "Add new image view");
                ImageUtils.loadImage(new_event, eventDay.getImageDrawable());
                // If a day doesn't belong to current month then image is transparent
                if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
                    new_event.setAlpha(0.5f);
                }
                eventGrid.addView(new_event, 30, 30);
            }
        });
    }
}
