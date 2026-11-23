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

// 3. POST /api/listings — Pasang Produk Audio Baru (REQ-15, REQ-16, REQ-22)
const createListing = async (req, res) => {
  try {
    const sellerId = req.user.id_user ?? req.user.id;
    const {
      brand_id,
      item_name,
      category,
      driver_type,
      connector_type,
      sound_signature,
      item_condition,
      price,
      description
    } = req.body;

    // Validasi kelengkapan kolom wajib
    if (
      !brand_id ||
      !item_name || item_name.trim() === '' ||
      !category ||
      !driver_type || driver_type.trim() === '' ||
      !connector_type || connector_type.trim() === '' ||
      !sound_signature || sound_signature.trim() === '' ||
      !item_condition ||
      price === undefined || price === null
    ) {
      return res.status(400).json({
        success: false,
        message: 'Semua kolom spesifikasi wajib diisi',
        data: null
      });
    }

    const normalizedCategory = category.trim().toUpperCase();
    const normalizedCondition = item_condition.trim().toUpperCase();
    const numericPrice = Number(price);
    const parsedBrandId = parseInt(brand_id, 10);

    if (!VALID_CATEGORIES.includes(normalizedCategory)) {
      return res.status(400).json({
        success: false,
        message: `Kategori tidak valid. Pilihan: ${VALID_CATEGORIES.join(', ')}`,
        data: null
      });
    }

    if (!VALID_CONDITIONS.includes(normalizedCondition)) {
      return res.status(400).json({
        success: false,
        message: `Kondisi barang tidak valid. Pilihan: ${VALID_CONDITIONS.join(', ')}`,
        data: null
      });
    }

    if (isNaN(numericPrice) || numericPrice <= 0) {
      return res.status(400).json({
        success: false,
        message: 'Harga produk harus lebih dari 0',
        data: null
      });
    }

    if (isNaN(parsedBrandId)) {
      return res.status(400).json({
        success: false,
        message: 'ID merek (brand_id) tidak valid',
        data: null
      });
    }

    // Pastikan brand_id tersedia pada tabel audio_brands (REQ-22)
    const brandCheck = await pool.query(
      'SELECT id_brand FROM audio_brands WHERE id_brand = $1',
      [parsedBrandId]
    );
    if (brandCheck.rows.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'Merek audio (brand_id) tidak ditemukan dalam katalog master',
        data: null
      });
    }

    const insertQuery = `
      INSERT INTO listing_items (
        seller_id, brand_id, item_name, category,
        driver_type, connector_type, sound_signature,
        item_condition, price, description, status
      )
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, 'AVAILABLE')
      RETURNING id_item
    `;

    const insertValues = [
      sellerId,
      parsedBrandId,
      item_name.trim(),
      normalizedCategory,
      driver_type.trim(),
      connector_type.trim(),
      sound_signature.trim(),
      normalizedCondition,
      numericPrice,
      description && description.trim() !== '' ? description.trim() : null
    ];

    const inserted = await pool.query(insertQuery, insertValues);
    const { rows } = await pool.query(
      `${BASE_LISTING_SELECT} WHERE li.id_item = $1`,
      [inserted.rows[0].id_item]
    );

    return res.status(201).json({
      success: true,
      message: 'Produk audio berhasil dipublikasikan',
      data: rows[0]
    });
  } catch (error) {
    console.error('Error createListing:', error);
    return res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan pada server saat menambahkan produk',
      data: null
    });
  }
};

// 4. PUT /api/listings/:id — Pembaruan Data Listing Audio (REQ-19, REQ-20, REQ-22)
const updateListing = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (isNaN(id)) {
      return res.status(400).json({
        success: false,
        message: 'ID produk tidak valid',
        data: null
      });
    }

    const currentUserId = req.user.id_user ?? req.user.id;
    const currentUserRole = req.user.role;

    const existingRes = await pool.query('SELECT * FROM listing_items WHERE id_item = $1', [id]);
    if (existingRes.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Produk audio tidak ditemukan',
        data: null
      });
    }

    const existing = existingRes.rows[0];

    // Kontrol kepemilikan: hanya pemilik (seller_id) atau ADMIN (REQ-19)
    if (existing.seller_id !== currentUserId && currentUserRole !== 'ADMIN') {
      return res.status(403).json({
        success: false,
        message: 'Anda tidak memiliki izin untuk mengubah produk milik pengguna lain',
        data: null
      });
    }

    // Tolak pembaruan jika barang sudah SOLD (REQ-20)
    if (existing.status === 'SOLD') {
      return res.status(400).json({
        success: false,
        message: 'Produk yang sudah terjual (SOLD) tidak dapat diubah',
        data: null
      });
    }

    const {
      brand_id,
      item_name,
      category,
      driver_type,
      connector_type,
      sound_signature,
      item_condition,
      price,
      description
    } = req.body;

    if (
      !brand_id ||
      !item_name || item_name.trim() === '' ||
      !category ||
      !driver_type || driver_type.trim() === '' ||
      !connector_type || connector_type.trim() === '' ||
      !sound_signature || sound_signature.trim() === '' ||
      !item_condition ||
      price === undefined || price === null
    ) {
      return res.status(400).json({
        success: false,
        message: 'Semua kolom spesifikasi wajib diisi',
        data: null
      });
    }

    const normalizedCategory = category.trim().toUpperCase();
    const normalizedCondition = item_condition.trim().toUpperCase();
    const numericPrice = Number(price);
    const parsedBrandId = parseInt(brand_id, 10);

    if (!VALID_CATEGORIES.includes(normalizedCategory) || !VALID_CONDITIONS.includes(normalizedCondition)) {
      return res.status(400).json({
        success: false,
        message: 'Kategori atau kondisi produk tidak sesuai pilihan yang diizinkan',
        data: null
      });
    }

    if (isNaN(numericPrice) || numericPrice <= 0 || isNaN(parsedBrandId)) {
      return res.status(400).json({
        success: false,
        message: 'Harga produk atau merek tidak valid',
        data: null
      });
    }

    const brandCheck = await pool.query(
      'SELECT id_brand FROM audio_brands WHERE id_brand = $1',
      [parsedBrandId]
    );
    if (brandCheck.rows.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'Merek audio (brand_id) tidak ditemukan',
        data: null
      });
    }

    const updateQuery = `
      UPDATE listing_items
      SET
        brand_id = $1,
        item_name = $2,
        category = $3,
        driver_type = $4,
        connector_type = $5,
        sound_signature = $6,
        item_condition = $7,
        price = $8,
        description = $9
      WHERE id_item = $10
    `;

    await pool.query(updateQuery, [
      parsedBrandId,
      item_name.trim(),
      normalizedCategory,
      driver_type.trim(),
      connector_type.trim(),
      sound_signature.trim(),
      normalizedCondition,
      numericPrice,
      description && description.trim() !== '' ? description.trim() : null,
      id
    ]);

    const { rows } = await pool.query(`${BASE_LISTING_SELECT} WHERE li.id_item = $1`, [id]);

    return res.status(200).json({
      success: true,
      message: 'Data produk audio berhasil diperbarui',
      data: rows[0]
    });
  } catch (error) {
    console.error('Error updateListing:', error);
    return res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan pada server saat memperbarui produk',
      data: null
    });
  }
};

// 5. DELETE /api/listings/:id — Penghapusan Data Listing Audio (REQ-19 & REQ-21)
const deleteListing = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (isNaN(id)) {
      return res.status(400).json({
        success: false,
        message: 'ID produk tidak valid',
        data: null
      });
    }

    const currentUserId = req.user.id_user ?? req.user.id;
    const currentUserRole = req.user.role;

    const existingRes = await pool.query('SELECT * FROM listing_items WHERE id_item = $1', [id]);
    if (existingRes.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Produk audio tidak ditemukan',
        data: null
      });
    }

    const existing = existingRes.rows[0];

    // Kontrol kepemilikan: hanya pemilik (seller_id) atau ADMIN (REQ-19)
    if (existing.seller_id !== currentUserId && currentUserRole !== 'ADMIN') {
      return res.status(403).json({
        success: false,
        message: 'Anda tidak memiliki izin untuk menghapus produk milik pengguna lain',
        data: null
      });
    }

    // Tolak penghapusan jika sudah SOLD atau terikat transaksi ACCEPTED/COMPLETED (REQ-21)
    if (existing.status === 'SOLD') {
      return res.status(409).json({
        success: false,
        message: 'Produk yang sudah terjual (SOLD) tidak dapat dihapus',
        data: null
      });
    }

    const activeOfferCheck = await pool.query(
      `SELECT COUNT(*)::int AS total
       FROM transaction_offers
       WHERE item_id = $1 AND status IN ('ACCEPTED', 'COMPLETED')`,
      [id]
    );

    if (activeOfferCheck.rows[0].total > 0) {
      return res.status(409).json({
        success: false,
        message: 'Produk tidak dapat dihapus karena sudah terikat transaksi yang disetujui',
        data: null
      });
    }

    await pool.query('DELETE FROM listing_items WHERE id_item = $1', [id]);

    return res.status(200).json({
      success: true,
      message: 'Produk audio berhasil dihapus',
      data: null
    });
  } catch (error) {
    if (error.code === '23503') {
      return res.status(409).json({
        success: false,
        message: 'Produk tidak dapat dihapus karena masih memiliki relasi transaksi aktif',
        data: null
      });
    }
    console.error('Error deleteListing:', error);
    return res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan pada server saat menghapus produk',
      data: null
    });
  }
};

module.exports = {
  getAllListings,
  getListingById,
  createListing,
  updateListing,
  deleteListing
};