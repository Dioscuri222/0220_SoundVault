const jwt = require('jsonwebtoken');

const verifyToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      success: false,
      message: 'Token autentikasi tidak ditemukan',
      data: null
    });
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, process.env.JWT_ACCESS_SECRET);
    req.user = decoded; // { id_user, email, role }
    next();
  } catch (err) {
    return res.status(401).json({
      success: false,
      message: 'Access token tidak valid atau sudah kedaluwarsa',
      data: null
    });
  }
};

const requireAdmin = (req, res, next) => {
  if (!req.user || req.user.role !== 'ADMIN') {
    return res.status(403).json({
      success: false,
      message: 'Akses ditolak: hanya ADMIN yang diizinkan melakukan operasi ini',
      data: null
    });
  }
  next();
};

module.exports = { verifyToken, requireAdmin };