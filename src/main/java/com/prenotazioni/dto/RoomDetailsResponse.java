package com.prenotazioni.dto;

import java.util.List;

public class RoomDetailsResponse {
    private Long id;
    private String name;
    private int floor;
    private int capacity;
    private boolean isVirtual;
    private String status;
    private CurrentBooking booking;
    private BlockInfo blocked;
    private List<BookingInfo> bookings;

    // Costruttori
    public RoomDetailsResponse() {}

    public RoomDetailsResponse(Long id, String name, int floor, int capacity) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.capacity = capacity;
        this.isVirtual = false;
    }

    public RoomDetailsResponse(Long id, String name, int floor, int capacity, boolean isVirtual) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.capacity = capacity;
        this.isVirtual = isVirtual;
    }

    // Inner class per la prenotazione corrente
    public static class CurrentBooking {
        private String user;
        private String date;
        private String time;
        private String purpose;

        public CurrentBooking() {}

        public CurrentBooking(String user, String date, String time, String purpose) {
            this.user = user;
            this.date = date;
            this.time = time;
            this.purpose = purpose;
        }

        // Getters e Setters
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }

    // Inner class per le informazioni di blocco
    public static class BlockInfo {
        private String reason;
        private String blockedBy;
        private String blockedAt;

        public BlockInfo() {}

        public BlockInfo(String reason, String blockedBy, String blockedAt) {
            this.reason = reason;
            this.blockedBy = blockedBy;
            this.blockedAt = blockedAt;
        }

        // Getters e Setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public String getBlockedBy() { return blockedBy; }
        public void setBlockedBy(String blockedBy) { this.blockedBy = blockedBy; }
        
        public String getBlockedAt() { return blockedAt; }
        public void setBlockedAt(String blockedAt) { this.blockedAt = blockedAt; }
    }

    // Inner class per la lista delle prenotazioni
    public static class BookingInfo {
        private String date;
        private String startTime;
        private String endTime;
        private String user;
        private String purpose;

        public BookingInfo() {}

        public BookingInfo(String date, String startTime, String endTime, String user, String purpose) {
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.user = user;
            this.purpose = purpose;
        }

        // Getters e Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }

    // Getters e Setters principali
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public boolean isVirtual() { return isVirtual; }
    public void setVirtual(boolean isVirtual) { this.isVirtual = isVirtual; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public CurrentBooking getBooking() { return booking; }
    public void setBooking(CurrentBooking booking) { this.booking = booking; }
    
    public BlockInfo getBlocked() { return blocked; }
    public void setBlocked(BlockInfo blocked) { this.blocked = blocked; }
    
    public List<BookingInfo> getBookings() { return bookings; }
    public void setBookings(List<BookingInfo> bookings) { this.bookings = bookings; }
}
