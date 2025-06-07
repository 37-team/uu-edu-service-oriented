package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingJob;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.UUID;

// TODO Refactor PersonReservationClient to not use this class
public record BookingQueryApi(Instant start,
                              Instant end,
                              Location location,
                              BookingJob job,
                              boolean exclusive,
                              UUID personId
) {
}
