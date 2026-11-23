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


-- Data Awal untuk Listing Item 
INSERT INTO listing_items (
  seller_id, brand_id, item_name, category,
  driver_type, connector_type, sound_signature,
  item_condition, price, description, status
)
VALUES
  (
    (SELECT id_user FROM users WHERE role = 'USER' ORDER BY id_user ASC LIMIT 1),
    (SELECT id_brand FROM audio_brands WHERE brand_name ILIKE 'Tangzu' LIMIT 1),
    'Tangzu Wan''er S.G Jade Edition',
    'IEM',
    '1DD',
    '2-Pin 0.78mm',
    'Warm',
    'USED',
    210000,
    'Pemakaian 2 bulan, lengkap box, kabel bawaan, dan eartips Tang Sancai.',
    'AVAILABLE'
  ),
  (
    (SELECT id_user FROM users WHERE role = 'USER' ORDER BY id_user ASC LIMIT 1),
    (SELECT id_brand FROM audio_brands WHERE brand_name ILIKE 'Moondrop' LIMIT 1),
    'Moondrop Blessing 3',
    'IEM',
    'Hybrid',
    '2-Pin 0.78mm',
    'Harman',
    'USED',
    3850000,
    'Konfigurasi 2DD + 4BA mulus tanpa dent, nozzle bersih, lengkap carrying case.',
    'AVAILABLE'
  ),
  (
    (SELECT id_user FROM users WHERE role = 'USER' ORDER BY id_user ASC LIMIT 1),
    (SELECT id_brand FROM audio_brands WHERE brand_name ILIKE 'FiiO' LIMIT 1),
    'FiiO KA13 Portable DAC/Amp',
    'DAC_AMP',
    'Dual CS43131',
    '4.4mm Balanced',
    'Neutral',
    'NEW',
    1150000,
    'BNIB segel resmi, mendukung mode desktop hingga 550mW.',
    'AVAILABLE'
  ),
  (
    (SELECT id_user FROM users WHERE role = 'USER' ORDER BY id_user ASC LIMIT 1),
    (SELECT id_brand FROM audio_brands WHERE brand_name ILIKE 'Moondrop' LIMIT 1),
    'Moondrop Line K Upgrade Cable',
    'CABLE',
    'Silver-Plated OCC',
    '4.4mm Balanced',
    'Bright',
    'USED',
    320000,
    'Kabel upgrade ringan tidak microphonic, konektor 2-Pin 0.78mm ke 4.4mm.',
    'AVAILABLE'
  );