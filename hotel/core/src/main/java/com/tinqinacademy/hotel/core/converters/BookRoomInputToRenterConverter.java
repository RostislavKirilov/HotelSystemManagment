package com.tinqinacademy.hotel.core.converters;

import org.springframework.core.convert.converter.Converter;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.persistence.entitites.Renter;
import org.springframework.stereotype.Component;

@Component
public class BookRoomInputToRenterConverter implements Converter<BookRoomInput, Renter> {

    @Override
    public Renter convert(BookRoomInput bookRoomInput) {
        return Renter.builder()
                .firstName(bookRoomInput.getFirstName())
                .lastName(bookRoomInput.getLastName())
                .startDate(bookRoomInput.getStartDate())
                .endDate(bookRoomInput.getEndDate())
                .build();
    }
}
