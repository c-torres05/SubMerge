package com.example.submerge.calendar.utils;

import com.annimon.stream.Optional;
import com.example.submerge.calendar.EventDay;

import java.util.Calendar;
import com.annimon.stream.Stream;

public class EventDayUtils {

    /**
     * This method is used to check whether this day is an event day with provided custom label color.
     *
     * @param day                A calendar instance representing day date
     * @param calendarProperties A calendar properties
     */
    public static boolean isEventDayWithLabelColor(Calendar day, CalendarProperties calendarProperties) {
        if (calendarProperties.getEventDays() != null || calendarProperties.getEventsEnabled()) {
            return Stream.of(calendarProperties.getEventDays()).anyMatch(eventDate ->
                    eventDate.getCalendar().equals(day) && eventDate.getLabelColor() != 0);
        }

        return false;
    }

    /**
     * This method is used to get event day which contains custom label color.
     *
     * @param day                A calendar instance representing day date
     * @param calendarProperties A calendar properties
     */
    public static Optional<EventDay> getEventDayWithLabelColor(Calendar day, CalendarProperties calendarProperties) {
        return Stream.of(calendarProperties.getEventDays())
                .filter(eventDate -> eventDate.getCalendar().equals(day) && eventDate.getLabelColor() != 0)
                .findFirst();
    }
}
