const express = require('express');
const router = express.Router();
const brandController = require('../controllers/brandController');
const { verifyToken, requireAdmin } = require('../middleware/authMiddleware');

router.get('/', verifyToken, brandController.getAllBrands);
router.get('/:id', verifyToken, brandController.getBrandById);
router.post('/', verifyToken, requireAdmin, brandController.createBrand);
router.put('/:id', verifyToken, requireAdmin, brandController.updateBrand);
router.delete('/:id', verifyToken, requireAdmin, brandController.deleteBrand);

module.exports = router;