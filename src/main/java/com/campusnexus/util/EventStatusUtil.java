package com.campusnexus.util;

import com.campusnexus.entity.Event;
import com.campusnexus.enums.EventStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Pure static utility for computing the effective EventStatus of an event.
 *
 * <p><b>Design rule:</b>
 * <ul>
 *   <li>CANCELLED is the only user-persisted status override. Once set, it is never
 *       overridden by time-based logic.</li>
 *   <li>UPCOMING, ONGOING, and COMPLETED are ALWAYS derived from start/end datetimes
 *       and the current UTC time. They are NEVER written to the database at read time.</li>
 * </ul>
 *
 * <p>Both datetimes stored in the DB are treated as UTC (frontend must send UTC).
 */
public final class EventStatusUtil {

    private EventStatusUtil() {
        // utility class — no instantiation
    }

    /**
     * Compute the effective status of an event without touching the database.
     *
     * @param event the event entity (never null)
     * @return the effective {@link EventStatus}
     */
    public static EventStatus compute(Event event) {
        // CANCELLED is a permanent user action — it always wins.
        if (event.getStatus() == EventStatus.CANCELLED) {
            return EventStatus.CANCELLED;
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime start = event.getStartDateTime();
        LocalDateTime end   = event.getEndDateTime();

        if (now.isAfter(end)) {
            return EventStatus.COMPLETED;
        }
        if (!now.isBefore(start)) {
            // now >= start AND now <= end
            return EventStatus.ONGOING;
        }
        return EventStatus.UPCOMING;
    }

    /**
     * Returns true if new registrations are allowed for the event.
     * Registrations are only permitted for UPCOMING or ONGOING events.
     *
     * @param event the event entity
     * @return true if registration is open
     */
    public static boolean isRegistrationOpen(Event event) {
        EventStatus status = compute(event);
        return status == EventStatus.UPCOMING || status == EventStatus.ONGOING;
    }
}
