package ru.practicum.shareit.booking;

public enum BookingStatus {

    WAITING("Новое бронирование, ожидает одобрения."),
    APPROVED("Бронирование подтверждено владельцем."),
    REJECTED("Бронирование отклонено владельцем."),
    CANCELED("Бронирование отменено создателем.");

    private String title;

    BookingStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
