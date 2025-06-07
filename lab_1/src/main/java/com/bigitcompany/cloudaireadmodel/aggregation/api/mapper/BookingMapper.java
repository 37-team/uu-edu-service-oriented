package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.BookingDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;

import java.util.List;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(
            booking.getStart(),
            booking.getEnd(),
            booking.getLocation() != null ? LocationMapper.toDto(booking.getLocation()) : null,
            booking.getJob() != null ? BookingJobMapper.toDto(booking.getJob()) : null,
            booking.isExclusive()
        );
    }

    public static List<BookingDto> toDtos(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDto).toList();
    }
}