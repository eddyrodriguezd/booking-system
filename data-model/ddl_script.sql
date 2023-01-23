-- Create extension for UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema
CREATE SCHEMA booking;

-- Create "hotels" table
CREATE TABLE booking.hotels 
( 
  id uuid default uuid_generate_v4 () PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  country_code VARCHAR(2) NOT NULL,
  city VARCHAR(60) NOT NULL,
  latitude float8,
  longitude float8
);

-- Create "rooms" table
CREATE TABLE booking.rooms 
( 
  id uuid DEFAULT uuid_generate_v4 () PRIMARY KEY,
  hotel_id uuid NOT NULL,
  type VARCHAR(50),
  number_of_guests smallint NOT NULL,
  daily_rate int4 NOT NULL,
  currency_code VARCHAR(3) NOT NULL,
  constraint fk_hotel_id
     foreign key (hotel_id) 
     REFERENCES booking.hotels (id)
);

-- Create "reservations" table
CREATE TABLE booking.reservations 
( 
  id uuid DEFAULT uuid_generate_v4 () PRIMARY KEY,
  room_id uuid NOT NULL,
  check_in_date date NOT NULL,
  check_out_date date NOT NULL,
  guest_id uuid NOT NULL,
  status VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  constraint fk_room_id
     foreign key (room_id) 
     REFERENCES booking.rooms (id)
);