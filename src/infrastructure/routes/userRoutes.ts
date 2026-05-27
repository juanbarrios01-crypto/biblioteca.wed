import { Router } from 'express';
import { UserController } from '../controllers/userController';
import { authenticate, authorize } from '../../security/authMiddleware';

const router = Router();
const userController = new UserController();

router.post('/register', userController.register);
router.post('/login', userController.login);
router.get('/', authenticate, authorize(['ADMIN']), userController.getAllUsers);
router.get('/:id', authenticate, authorize(['ADMIN']), userController.getUserById);

export default router;
