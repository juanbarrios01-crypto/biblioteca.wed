import { Request, Response } from 'express';
import { BookService } from '../../services/bookService';

const bookService = new BookService();

export class BookController {
  async createBook(req: Request, res: Response) {
    try {
      const { title, author, category, stock } = req.body;

      if (!title || typeof title !== 'string' || title.trim() === '') {
        return res.status(400).json({ error: 'Title is required and must be a non-empty string.' });
      }
      if (!author || typeof author !== 'string' || author.trim() === '') {
        return res.status(400).json({ error: 'Author is required and must be a non-empty string.' });
      }
      if (!category || typeof category !== 'string' || category.trim() === '') {
        return res.status(400).json({ error: 'Category is required and must be a non-empty string.' });
      }
      if (stock !== undefined && (typeof stock !== 'number' || stock < 0 || !Number.isInteger(stock))) {
        return res.status(400).json({ error: 'Stock must be a non-negative integer.' });
      }

      const book = await bookService.createBook({
        title: title.trim(),
        author: author.trim(),
        category: category.trim(),
        stock: stock !== undefined ? stock : 0
      });
      res.status(201).json(book);
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }

  async getAllBooks(req: Request, res: Response) {
    try {
      const books = await bookService.getAllBooks(req.query);
      res.status(200).json(books);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }

  async getBookById(req: Request, res: Response) {
    try {
      const id = parseInt(req.params.id as string);
      if (isNaN(id)) {
        return res.status(400).json({ error: 'Invalid book ID.' });
      }
      const book = await bookService.getBookById(id);
      if (!book) return res.status(404).json({ error: 'Book not found' });
      res.status(200).json(book);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }

  async updateBook(req: Request, res: Response) {
    try {
      const id = parseInt(req.params.id as string);
      if (isNaN(id)) {
        return res.status(400).json({ error: 'Invalid book ID.' });
      }

      const { title, author, category, stock } = req.body;

      if (title !== undefined && (typeof title !== 'string' || title.trim() === '')) {
        return res.status(400).json({ error: 'Title must be a non-empty string.' });
      }
      if (author !== undefined && (typeof author !== 'string' || author.trim() === '')) {
        return res.status(400).json({ error: 'Author must be a non-empty string.' });
      }
      if (category !== undefined && (typeof category !== 'string' || category.trim() === '')) {
        return res.status(400).json({ error: 'Category must be a non-empty string.' });
      }
      if (stock !== undefined && (typeof stock !== 'number' || stock < 0 || !Number.isInteger(stock))) {
        return res.status(400).json({ error: 'Stock must be a non-negative integer.' });
      }

      const updateData: any = {};
      if (title !== undefined) updateData.title = title.trim();
      if (author !== undefined) updateData.author = author.trim();
      if (category !== undefined) updateData.category = category.trim();
      if (stock !== undefined) updateData.stock = stock;

      const book = await bookService.updateBook(id, updateData);
      res.status(200).json(book);
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }

  async deleteBook(req: Request, res: Response) {
    try {
      const id = parseInt(req.params.id as string);
      if (isNaN(id)) {
        return res.status(400).json({ error: 'Invalid book ID.' });
      }
      await bookService.deleteBook(id);
      res.status(204).send();
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }
}
