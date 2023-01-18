-- Create extension for UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema
CREATE SCHEMA booking;

-- Create "hotels" table
CREATE TABLE booking.hotels 
( 
  id uuid default uuid_generate_v4 () primary key,
  name VARCHAR(255) not null,
  country_code VARCHAR(2) not null,
  city VARCHAR(60) not null,
  latitude float8,
  longitude float8
);

-- Create "rooms" table
CREATE TABLE booking.rooms 
( 
  id uuid DEFAULT uuid_generate_v4 () primary key,
  hotel_id uuid not NULL,
  type VARCHAR(50),
  number_of_guests smallint not null,
  price int4 not null,
  currency_code VARCHAR(3) not null,
  constraint fk_hotel_id
     foreign key (hotel_id) 
     REFERENCES booking.hotels (id)
);

-- Create "reservations" table
CREATE TABLE booking.reservations 
( 
  id uuid DEFAULT uuid_generate_v4 () primary key,
  room_id uuid not NULL,
  checkin_date date not null,
  checkout_date date not null,
  guest_id uuid not NULL,
  constraint fk_room_id
     foreign key (room_id) 
     REFERENCES booking.rooms (id)
);