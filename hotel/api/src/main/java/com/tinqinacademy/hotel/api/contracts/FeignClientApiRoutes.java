package com.tinqinacademy.hotel.api.contracts;

public class FeignClientApiRoutes {

    public static final String BOOK_ROOM = "POST " + RestApiRoutes.BOOK_ROOM;
    public static final String ADD_ROOM = "POST " + RestApiRoutes.ADD_ROOM;
    public static final String AVAILABLE_ROOMS = "GET " + RestApiRoutes.AVAILABLE_ROOMS +
            "?startDate={startDate}&endDate={endDate}";
    public static final String REGISTER_VISITOR = "POST " + RestApiRoutes.REGISTER_VISITOR;
    public static final String REMOVE_BOOKING = "DELETE " + RestApiRoutes.REMOVE_BOOKING;
    public static final String DELETE_ROOM = "DELETE " + RestApiRoutes.DELETE_ROOM;
    public static final String FIND_ROOM = "GET " + RestApiRoutes.FIND_ROOM;
}
