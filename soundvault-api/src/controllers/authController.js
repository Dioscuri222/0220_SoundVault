const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../config/db');

const generateTokens = (user) => {
  const payload = {
    id_user: user.id_user,
    email: user.email,
    role: user.role
  };

  const accessToken = jwt.sign(payload, process.env.JWT_ACCESS_SECRET, {
    expiresIn: process.env.JWT_ACCESS_EXPIRES || '15m'
  });

  const refreshToken = jwt.sign(payload, process.env.JWT_REFRESH_SECRET, {
    expiresIn: process.env.JWT_REFRESH_EXPIRES || '7d'
  });

  return { accessToken, refreshToken };
};
// POST /api/auth/register
exports.register = async (req, res) => {
  try {
    const { nama_lengkap, email, password, no_telepon } = req.body;

    if (!nama_lengkap || !email || !password || !no_telepon) {
      return res.status(400).json({
        success: false,
        message: 'Semua kolom wajib diisi (nama_lengkap, email, password, no_telepon)',
        data: null
      });
    }

    if (password.length < 8) {
      return res.status(400).json({
        success: false,
        message: 'Kata sandi minimal 8 karakter',
        data: null
      });
    }

    const normalizedEmail = email.trim().toLowerCase();
    const existing = await db.query('SELECT id_user FROM users WHERE email = $1', [normalizedEmail]);
    if (existing.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Email sudah terdaftar',
        data: null
      });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const result = await db.query(
      `INSERT INTO users (nama_lengkap, email, password_hash, no_telepon, role)
       VALUES ($1, $2, $3, $4, 'USER')
       RETURNING id_user, nama_lengkap, email, no_telepon, role`,
      [nama_lengkap.trim(), normalizedEmail, passwordHash, no_telepon.trim()]
    );

    return res.status(201).json({
      success: true,
      message: 'Registrasi akun berhasil',
      data: result.rows[0]
    });
  } catch (err) {
    if (err.code === '23505') {
      return res.status(409).json({ success: false, message: 'Email sudah terdaftar', data: null });
    }
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};