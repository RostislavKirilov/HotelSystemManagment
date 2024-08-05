package com.tinqinacademy.hotel.api.contracts;

public class RestApiRoutes {

    private static final String API = "/api/v1";
    public static final String API_HOTEL = API + "/hotel";
    public static final String API_SYSTEM = API + "/system";
    public static final String BOOK_ROOM = API_HOTEL + "/book";
    public static final String ADD_ROOM = API_HOTEL + "/room/add";
    public static final String AVAILABLE_ROOMS = API_HOTEL + "/available-rooms";
    public static final String UPDATE_ROOM = API_SYSTEM + "/update/{roomId}";
    public static final String PARTIAL_UPDATE_ROOM = API_SYSTEM + "/partial-update";
    public static final String REGISTER_VISITOR = API_SYSTEM + "/register";
    public static final String REMOVE_BOOKING = API_HOTEL + "/booking/{bookId}";
    public static final String DELETE_ROOM = API_HOTEL + "/deleteRoomById/{id}";
    public static final String FIND_ROOM = API_HOTEL + "/{roomId}";
}