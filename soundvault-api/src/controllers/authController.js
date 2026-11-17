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

// POST /api/auth/login
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Email dan kata sandi wajib diisi',
        data: null
      });
    }

    const result = await db.query('SELECT * FROM users WHERE email = $1', [email.trim().toLowerCase()]);
    if (result.rows.length === 0) {
      return res.status(401).json({
        success: false,
        message: 'Email atau kata sandi salah',
        data: null
      });
    }

    const user = result.rows[0];
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'Email atau kata sandi salah',
        data: null
      });
    }

    const { accessToken, refreshToken } = generateTokens(user);
    await db.query('UPDATE users SET refresh_token = $1 WHERE id_user = $2', [refreshToken, user.id_user]);

    return res.status(200).json({
      success: true,
      message: 'Login berhasil',
      data: {
        access_token: accessToken,
        refresh_token: refreshToken,
        user: {
          id_user: user.id_user,
          nama_lengkap: user.nama_lengkap,
          email: user.email,
          no_telepon: user.no_telepon,
          role: user.role
        }
      }
    });
  } catch (err) {
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// POST /api/auth/refresh
exports.refresh = async (req, res) => {
  try {
    const { refresh_token } = req.body;
    if (!refresh_token) {
      return res.status(401).json({
        success: false,
        message: 'Refresh token wajib disertakan',
        data: null
      });
    }

    let decoded;
    try {
      decoded = jwt.verify(refresh_token, process.env.JWT_REFRESH_SECRET);
    } catch (e) {
      return res.status(401).json({
        success: false,
        message: 'Refresh token kedaluwarsa atau tidak valid',
        data: null
      });
    }

    const result = await db.query(
      'SELECT id_user, email, role, refresh_token FROM users WHERE id_user = $1',
      [decoded.id_user]
    );

    if (result.rows.length === 0 || result.rows[0].refresh_token !== refresh_token) {
      return res.status(401).json({
        success: false,
        message: 'Sesi refresh token sudah dicabut',
        data: null
      });
    }

    const userRow = result.rows[0];
    const newAccessToken = jwt.sign(
      { id_user: userRow.id_user, email: userRow.email, role: userRow.role },
      process.env.JWT_ACCESS_SECRET,
      { expiresIn: process.env.JWT_ACCESS_EXPIRES || '15m' }
    );

    return res.status(200).json({
      success: true,
      message: 'Access token berhasil diperbarui',
      data: {
        access_token: newAccessToken
      }
    });
  } catch (err) {
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};

// POST /api/auth/logout
exports.logout = async (req, res) => {
  try {
    await db.query('UPDATE users SET refresh_token = NULL WHERE id_user = $1', [req.user.id_user]);
    return res.status(200).json({
      success: true,
      message: 'Logout berhasil, sesi telah dicabut',
      data: null
    });
  } catch (err) {
    return res.status(500).json({ success: false, message: err.message, data: null });
  }
};