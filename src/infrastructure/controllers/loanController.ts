import { Request, Response } from 'express';
import { LoanService } from '../../services/loanService';
import { AuthRequest } from '../../security/authMiddleware';

const loanService = new LoanService();

export class LoanController {
  async createLoan(req: AuthRequest, res: Response) {
    try {
      const { bookId, estimatedReturn } = req.body;

      if (bookId === undefined || typeof bookId !== 'number' || !Number.isInteger(bookId)) {
        return res.status(400).json({ error: 'Book ID is required and must be an integer.' });
      }

      if (!estimatedReturn || typeof estimatedReturn !== 'string') {
        return res.status(400).json({ error: 'Estimated return date is required and must be a ISO string.' });
      }

      const parsedDate = Date.parse(estimatedReturn);
      if (isNaN(parsedDate)) {
        return res.status(400).json({ error: 'Invalid estimated return date format.' });
      }

      const returnDate = new Date(parsedDate);
      if (returnDate <= new Date()) {
        return res.status(400).json({ error: 'Estimated return date must be in the future.' });
      }

      const data = { bookId, estimatedReturn, userId: req.user?.id as number };
      const loan = await loanService.createLoan(data);
      res.status(201).json(loan);
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }

  async returnLoan(req: Request, res: Response) {
    try {
      const id = parseInt(req.params.id as string);
      if (isNaN(id)) {
        return res.status(400).json({ error: 'Invalid loan ID.' });
      }
      const loan = await loanService.returnLoan(id);
      res.status(200).json(loan);
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }

  async getAllLoans(req: Request, res: Response) {
    try {
      const loans = await loanService.getAllLoans(req.query);
      res.status(200).json(loans);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }

  async getMyLoans(req: AuthRequest, res: Response) {
    try {
      const userId = req.user?.id;
      if (!userId) return res.status(401).json({ error: 'Unauthorized' });
      const loans = await loanService.getLoansByUser(userId);
      res.status(200).json(loans);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }
}
