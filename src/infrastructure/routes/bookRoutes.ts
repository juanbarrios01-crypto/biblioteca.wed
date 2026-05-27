import { Router } from 'express';
import { BookController } from '../controllers/bookController';
import { authenticate, authorize } from '../../security/authMiddleware';

const router = Router();
const bookController = new BookController();

router.get('/', authenticate, bookController.getAllBooks);
router.get('/:id', authenticate, bookController.getBookById);
router.post('/', authenticate, authorize(['ADMIN']), bookController.createBook);
router.put('/:id', authenticate, authorize(['ADMIN']), bookController.updateBook);
router.delete('/:id', authenticate, authorize(['ADMIN']), bookController.deleteBook);

export default router;
