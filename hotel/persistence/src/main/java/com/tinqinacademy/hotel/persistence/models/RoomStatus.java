package com.tinqinacademy.hotel.persistence.models;

public enum RoomStatus {
    AVAILABLE("available"),
    OCCUPIED("occupied"),

    INACTIVE("inactive");

    private final String statusText;

    RoomStatus(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public static RoomStatus fromString(String text) {
        for (RoomStatus status : RoomStatus.values()) {
            if (status.statusText.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RoomStatus: " + text);
    }
}
