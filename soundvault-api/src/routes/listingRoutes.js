const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/authMiddleware');
const {
  getAllListings,
  getListingById,
  createListing,
  updateListing,
  deleteListing
} = require('../controllers/listingController');

router.use(verifyToken);

router.get('/', getAllListings);
router.get('/:id', getListingById);
router.post('/', createListing);
router.put('/:id', updateListing);
router.delete('/:id', deleteListing);

module.exports = router;