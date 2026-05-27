import { prisma } from '../infrastructure/prismaClient';

export class LoanService {
  async createLoan(data: { bookId: number; userId: number; estimatedReturn: string }) {
    const book = await prisma.book.findUnique({ where: { id: data.bookId } });
    if (!book || book.stock <= 0) {
      throw new Error('Book not available for loan');
    }

    const loan = await prisma.$transaction(async (prisma) => {
      await prisma.book.update({
        where: { id: data.bookId },
        data: { stock: { decrement: 1 } },
      });

      return prisma.loan.create({
        data: {
          bookId: data.bookId,
          userId: data.userId,
          estimatedReturn: new Date(data.estimatedReturn),
          status: 'BORROWED',
        },
      });
    });

    return loan;
  }

  async returnLoan(id: number) {
    const loan = await prisma.loan.findUnique({ where: { id } });
    if (!loan || loan.status === 'RETURNED') {
      throw new Error('Loan not found or already returned');
    }

    const updatedLoan = await prisma.$transaction(async (prisma) => {
      await prisma.book.update({
        where: { id: loan.bookId },
        data: { stock: { increment: 1 } },
      });

      return prisma.loan.update({
        where: { id },
        data: { status: 'RETURNED' },
      });
    });

    return updatedLoan;
  }

  async getAllLoans(query?: any) {
    return prisma.loan.findMany({
      where: query,
      include: { book: true, user: { select: { id: true, name: true, email: true } } },
    });
  }

  async getLoanById(id: number) {
    return prisma.loan.findUnique({
      where: { id },
      include: { book: true, user: { select: { id: true, name: true, email: true } } },
    });
  }

  async getLoansByUser(userId: number) {
    return prisma.loan.findMany({
      where: { userId },
      include: { book: true },
    });
  }
}
