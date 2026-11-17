const bcrypt = require('bcryptjs');
const db = require('./src/config/db');

async function seed() {
  try {
    const hash = await bcrypt.hash('password123', 10);

    const adminRes = await db.query(
      `INSERT INTO users (nama_lengkap, email, password_hash, no_telepon, role)
       VALUES ($1, $2, $3, $4, 'ADMIN')
       ON CONFLICT (email) DO UPDATE SET password_hash = EXCLUDED.password_hash
       RETURNING id_user`,
      ['Admin SoundVault', 'admin@soundvault.id', hash, '081200000001']
    );

    await db.query(
      `INSERT INTO users (nama_lengkap, email, password_hash, no_telepon, role)
       VALUES ($1, $2, $3, $4, 'USER')
       ON CONFLICT (email) DO UPDATE SET password_hash = EXCLUDED.password_hash`,
      ['Audiophile Buyer', 'user@soundvault.id', hash, '081200000002']
    );

    const adminId = adminRes.rows[0].id_user;

    const brands = [
      ['Tangzu', 'China', 'Produsen In-Ear Monitor dengan tuning vokal dan seri populer seperti Waner SG serta Xuan NV.'],
      ['Moondrop', 'China', 'Merek personal audio yang dikenal dengan target kurva VDSF dan lini IEM/TWS.'],
      ['FiiO', 'China', 'Produsen DAC/Amp portabel, Digital Audio Player, dan IEM.']
    ];

    for (const [name, country, desc] of brands) {
      await db.query(
        `INSERT INTO audio_brands (brand_name, country_origin, description, created_by)
         VALUES ($1, $2, $3, $4)
         ON CONFLICT (brand_name) DO NOTHING`,
        [name, country, desc, adminId]
      );
    }

    console.log('Seed data berhasil dijalankan! Password akun uji coba: password123');
    process.exit(0);
  } catch (err) {
    console.error('Gagal menjalankan seed:', err);
    process.exit(1);
  }
}

seed();