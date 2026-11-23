const pool = require('../config/db');

const VALID_CATEGORIES = ['IEM', 'DAC_AMP', 'CABLE', 'ACCESSORY'];
const VALID_CONDITIONS = ['NEW', 'USED'];
const VALID_STATUSES = ['AVAILABLE', 'RESERVED', 'SOLD'];

// Helper query SELECT dengan JOIN ke audio_brands & users
const BASE_LISTING_SELECT = `
  SELECT
    li.id_item,
    li.seller_id,
    li.brand_id,
    ab.brand_name,
    u.nama_lengkap AS seller_name,
    u.no_telepon AS seller_phone,
    li.item_name,
    li.category,
    li.driver_type,
    li.connector_type,
    li.sound_signature,
    li.item_condition,
    li.price,
    li.description,
    li.status,
    li.created_at
  FROM listing_items li
  JOIN audio_brands ab ON li.brand_id = ab.id_brand
  JOIN users u ON li.seller_id = u.id_user
`;