-- Add hotel (Cancun)
INSERT INTO booking.hotels (name, country_code, city, latitude, longitude) VALUES ('Cancun Resort', 'MX', 'Cancun', 21.13525464852236, -86.76441511620476);

-- Add room to the Cancun hotel
INSERT INTO booking.rooms (hotel_id, type, number_of_guests, daily_rate, currency_code) VALUES (
  (SELECT id FROM booking.hotels WHERE name = 'Cancun Resort'), 'Premium', '2', '250', 'USD'
);