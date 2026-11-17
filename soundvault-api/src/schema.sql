-- Buat database terlebih dahulu jika belum ada:
-- CREATE DATABASE soundvault_db;

-- 1. Tabel users
CREATE TABLE IF NOT EXISTS users (
    id_user SERIAL PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    no_telepon VARCHAR(20) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    refresh_token VARCHAR(512) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabel audio_brands
CREATE TABLE IF NOT EXISTS audio_brands (
    id_brand SERIAL PRIMARY KEY,
    brand_name VARCHAR(80) NOT NULL UNIQUE,
    country_origin VARCHAR(60) NOT NULL,
    description TEXT NULL,
    created_by INT NOT NULL,
    CONSTRAINT fk_brand_admin FOREIGN KEY (created_by)
        REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 3. Tabel listing_items
CREATE TABLE IF NOT EXISTS listing_items (
    id_item SERIAL PRIMARY KEY,
    seller_id INT NOT NULL,
    brand_id INT NOT NULL,
    item_name VARCHAR(120) NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('IEM', 'DAC_AMP', 'CABLE', 'ACCESSORY')),
    driver_type VARCHAR(60) NOT NULL,
    connector_type VARCHAR(40) NOT NULL,
    sound_signature VARCHAR(50) NOT NULL,
    item_condition VARCHAR(10) NOT NULL CHECK (item_condition IN ('NEW', 'USED')),
    price NUMERIC(12, 2) NOT NULL,
    description TEXT NULL,
    status VARCHAR(15) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'RESERVED', 'SOLD')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_seller FOREIGN KEY (seller_id)
        REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_item_brand FOREIGN KEY (brand_id)
        REFERENCES audio_brands(id_brand)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 4. Tabel transaction_offers
CREATE TABLE IF NOT EXISTS transaction_offers (
    id_offer SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    buyer_id INT NOT NULL,
    offered_price NUMERIC(12, 2) NOT NULL,
    offer_type VARCHAR(15) NOT NULL CHECK (offer_type IN ('NEGO', 'DIRECT_BUY')),
    status VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_offer_item FOREIGN KEY (item_id)
        REFERENCES listing_items(id_item)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_offer_buyer FOREIGN KEY (buyer_id)
        REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Trigger otomatis untuk memperbarui kolom updated_at pada transaction_offers
CREATE OR REPLACE FUNCTION set_updated_at_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_transaction_offers_updated_at ON transaction_offers;
CREATE TRIGGER trg_transaction_offers_updated_at
BEFORE UPDATE ON transaction_offers
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_timestamp();

-- 5. Tabel wishlists
CREATE TABLE IF NOT EXISTS wishlists (
    id_wishlist SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    notes VARCHAR(200) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_wishlist_item UNIQUE (user_id, item_id),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id)
        REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_item FOREIGN KEY (item_id)
        REFERENCES listing_items(id_item)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);