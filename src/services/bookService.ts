import { prisma } from '../infrastructure/prismaClient';

export class BookService {
  async createBook(data: any) {
    return prisma.book.create({ data });
  }

  async getAllBooks(query?: any) {
    const filter: any = {};
    if (query?.title) {
      filter.title = { contains: query.title };
    }
    if (query?.author) {
      filter.author = { contains: query.author };
    }
    if (query?.category) {
      filter.category = { contains: query.category };
    }
    return prisma.book.findMany({ where: filter });
  }

  async getBookById(id: number) {
    return prisma.book.findUnique({ where: { id } });
  }

  async updateBook(id: number, data: any) {
    return prisma.book.update({ where: { id }, data });
  }

  async deleteBook(id: number) {
    return prisma.book.delete({ where: { id } });
  }
}
