package com.test.booking.api.model;

import java.util.UUID;

public class Hotel {
    private UUID uuid;
    private String name;
    private String countryCode;
    private Location location;

    public Hotel(UUID uuid, String name, String countryCode, double locationLatitude, double locationLongitude) {
        this.uuid = uuid;
        this.name = name;
        this.countryCode = countryCode;
        this.location = new Location(locationLatitude, locationLongitude);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", location=" + location +
                '}';
    }
}

class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
