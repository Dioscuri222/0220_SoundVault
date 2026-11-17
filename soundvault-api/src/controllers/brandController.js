const db = require('../config/db');

// GET /api/brands?search=
exports.getAllBrands = async (req, res) => {
  try {
    const search = (req.query.search || '').trim();
    let query = `
      SELECT b.id_brand, b.brand_name, b.country_origin, b.description, b.created_by,
             u.nama_lengkap AS admin_name
      FROM audio_brands b
      LEFT JOIN users u ON b.created_by = u.id_user
    `;
    const params = [];

    if (search) {
      query += ' WHERE b.brand_name ILIKE $1 OR b.country_origin ILIKE $1';
      params.push(`%${search}%`);
    }

    query += ' ORDER BY b.brand_name ASC';
    const result = await db.query(query, params);

    return res.status(200).json({
      success: true,
      message: 'Daftar merek audio berhasil dimuat',
      data: result.rows
    });
  } catch (err) {
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// GET /api/brands/:id
exports.getBrandById = async (req, res) => {
  try {
    const result = await db.query('SELECT * FROM audio_brands WHERE id_brand = $1', [req.params.id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Merek audio tidak ditemukan', data: null });
    }
    return res.status(200).json({ success: true, message: 'Detail merek audio ditemukan', data: result.rows[0] });
  } catch (err) {
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// POST /api/brands (ADMIN)
exports.createBrand = async (req, res) => {
  try {
    const { brand_name, country_origin, description } = req.body;
    if (!brand_name || !country_origin) {
      return res.status(400).json({
        success: false,
        message: 'Nama merek (brand_name) dan negara asal (country_origin) wajib diisi',
        data: null
      });
    }

    const dup = await db.query('SELECT id_brand FROM audio_brands WHERE LOWER(brand_name) = LOWER($1)', [brand_name.trim()]);
    if (dup.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Nama merek audio sudah terdaftar',
        data: null
      });
    }

    const result = await db.query(
      `INSERT INTO audio_brands (brand_name, country_origin, description, created_by)
       VALUES ($1, $2, $3, $4)
       RETURNING *`,
      [brand_name.trim(), country_origin.trim(), description || null, req.user.id_user]
    );

    return res.status(201).json({
      success: true,
      message: 'Merek audio berhasil ditambahkan',
      data: result.rows[0]
    });
  } catch (err) {
    if (err.code === '23505') {
      return res.status(409).json({ success: false, message: 'Nama merek audio sudah terdaftar', data: null });
    }
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// PUT /api/brands/:id (ADMIN)
exports.updateBrand = async (req, res) => {
  try {
    const { id } = req.params;
    const { brand_name, country_origin, description } = req.body;

    if (!brand_name || !country_origin) {
      return res.status(400).json({
        success: false,
        message: 'Nama merek dan negara asal wajib diisi',
        data: null
      });
    }

    const dup = await db.query(
      'SELECT id_brand FROM audio_brands WHERE LOWER(brand_name) = LOWER($1) AND id_brand != $2',
      [brand_name.trim(), id]
    );
    if (dup.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Nama merek audio sudah digunakan merek lain',
        data: null
      });
    }

    const result = await db.query(
      `UPDATE audio_brands
       SET brand_name = $1, country_origin = $2, description = $3
       WHERE id_brand = $4
       RETURNING *`,
      [brand_name.trim(), country_origin.trim(), description || null, id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Merek audio tidak ditemukan', data: null });
    }

    return res.status(200).json({
      success: true,
      message: 'Merek audio berhasil diperbarui',
      data: result.rows[0]
    });
  } catch (err) {
    if (err.code === '23505') {
      return res.status(409).json({ success: false, message: 'Nama merek audio sudah digunakan merek lain', data: null });
    }
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// DELETE /api/brands/:id (ADMIN)
exports.deleteBrand = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query('DELETE FROM audio_brands WHERE id_brand = $1', [id]);

    if (result.rowCount === 0) {
      return res.status(404).json({ success: false, message: 'Merek audio tidak ditemukan', data: null });
    }

    return res.status(200).json({
      success: true,
      message: 'Merek audio berhasil dihapus',
      data: null
    });
  } catch (err) {
    // PostgreSQL error code 23503: foreign_key_violation
    if (err.code === '23503') {
      return res.status(409).json({
        success: false,
        message: 'Merek tidak dapat dihapus karena masih digunakan oleh produk listing aktif',
        data: null
      });
    }
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};