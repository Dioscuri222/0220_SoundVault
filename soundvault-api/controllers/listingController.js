const pool = require('../config/db');

const VALID_CATEGORIES = ['IEM', 'DAC_AMP', 'CABLE', 'ACCESSORY'];
const VALID_CONDITIONS = ['NEW', 'USED'];
const VALID_STATUSES = ['AVAILABLE', 'RESERVED', 'SOLD'];