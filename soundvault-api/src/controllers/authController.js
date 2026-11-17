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