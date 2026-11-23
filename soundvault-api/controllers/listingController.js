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

// 1. GET /api/listings — Read & Multi-Filter Spesifikasi Audio (REQ-17 & REQ-18)
const getAllListings = async (req, res) => {
  try {
    const {
      search,
      category,
      brand_id,
      driver_type,
      connector_type,
      sound_signature,
      item_condition,
      status
    } = req.query;

    const conditions = [];
    const values = [];
    let paramIndex = 1;

    if (search && search.trim() !== '') {
      conditions.push(`(li.item_name ILIKE $${paramIndex} OR li.description ILIKE $${paramIndex})`);
      values.push(`%${search.trim()}%`);
      paramIndex++;
    }

    if (category && category.trim() !== '') {
      conditions.push(`li.category = $${paramIndex}`);
      values.push(category.trim().toUpperCase());
      paramIndex++;
    }

    if (brand_id && !isNaN(parseInt(brand_id, 10))) {
      conditions.push(`li.brand_id = $${paramIndex}`);
      values.push(parseInt(brand_id, 10));
      paramIndex++;
    }

    if (driver_type && driver_type.trim() !== '') {
      conditions.push(`li.driver_type ILIKE $${paramIndex}`);
      values.push(`%${driver_type.trim()}%`);
      paramIndex++;
    }

    if (connector_type && connector_type.trim() !== '') {
      conditions.push(`li.connector_type ILIKE $${paramIndex}`);
      values.push(`%${connector_type.trim()}%`);
      paramIndex++;
    }

    if (sound_signature && sound_signature.trim() !== '') {
      conditions.push(`li.sound_signature ILIKE $${paramIndex}`);
      values.push(`%${sound_signature.trim()}%`);
      paramIndex++;
    }

    if (item_condition && item_condition.trim() !== '') {
      conditions.push(`li.item_condition = $${paramIndex}`);
      values.push(item_condition.trim().toUpperCase());
      paramIndex++;
    }

    if (status && status.trim() !== '') {
      const normalizedStatus = status.trim().toUpperCase();
      if (VALID_STATUSES.includes(normalizedStatus)) {
        conditions.push(`li.status = $${paramIndex}`);
        values.push(normalizedStatus);
        paramIndex++;
      }
    }

    const whereClause = conditions.length > 0 ? `WHERE ${conditions.join(' AND ')}` : '';
    const query = `
      ${BASE_LISTING_SELECT}
      ${whereClause}
      ORDER BY li.created_at DESC
    `;

    const { rows } = await pool.query(query, values);

    return res.status(200).json({
      success: true,
      message: 'Daftar listing audio berhasil dimuat',
      data: rows
    });
  } catch (error) {
    console.error('Error getAllListings:', error);
    return res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan pada server saat memuat daftar listing',
      data: null
    });
  }
};

// 2. GET /api/listings/:id — Detail Lengkap Produk Audio (REQ-17)
const getListingById = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (isNaN(id)) {
      return res.status(400).json({
        success: false,
        message: 'ID produk tidak valid',
        data: null
      });
    }

    const { rows } = await pool.query(`${BASE_LISTING_SELECT} WHERE li.id_item = $1`, [id]);

    if (rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Produk audio tidak ditemukan',
        data: null
      });
    }

    return res.status(200).json({
      success: true,
      message: 'Detail produk audio berhasil dimuat',
      data: rows[0]
    });
  } catch (error) {
    console.error('Error getListingById:', error);
    return res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan pada server saat memuat detail produk',
      data: null
    });
  }
};