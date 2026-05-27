import { Router } from 'express';
import { LoanController } from '../controllers/loanController';
import { authenticate, authorize } from '../../security/authMiddleware';

const router = Router();
const loanController = new LoanController();

router.post('/', authenticate, loanController.createLoan);
router.post('/:id/return', authenticate, authorize(['ADMIN']), loanController.returnLoan);
router.get('/', authenticate, authorize(['ADMIN']), loanController.getAllLoans);
router.get('/my-loans', authenticate, loanController.getMyLoans);

export default router;
