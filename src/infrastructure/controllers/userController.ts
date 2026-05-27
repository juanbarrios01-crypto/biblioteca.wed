import { Request, Response } from 'express';
import { UserService } from '../../services/userService';

const userService = new UserService();

export class UserController {
  async register(req: Request, res: Response) {
    try {
      const { email, password, name, role } = req.body;

      if (!email || typeof email !== 'string') {
        return res.status(400).json({ error: 'Email is required and must be a string.' });
      }
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        return res.status(400).json({ error: 'Invalid email format.' });
      }

      if (!password || typeof password !== 'string') {
        return res.status(400).json({ error: 'Password is required and must be a string.' });
      }
      if (password.length < 6) {
        return res.status(400).json({ error: 'Password must be at least 6 characters long.' });
      }

      if (!name || typeof name !== 'string' || name.trim() === '') {
        return res.status(400).json({ error: 'Name is required and must be a non-empty string.' });
      }

      if (role && role !== 'ADMIN' && role !== 'STUDENT') {
        return res.status(400).json({ error: 'Role must be either ADMIN or STUDENT.' });
      }

      const user = await userService.register(req.body);
      res.status(201).json({ message: 'User registered successfully', user: { id: user.id, email: user.email, name: user.name, role: user.role } });
    } catch (error: any) {
      if (error.code === 'P2002') {
        res.status(400).json({ error: 'A user with this email already exists.' });
      } else {
        res.status(400).json({ error: error.message });
      }
    }
  }

  async login(req: Request, res: Response) {
    try {
      const { email, password } = req.body;

      if (!email || typeof email !== 'string') {
        return res.status(400).json({ error: 'Email is required.' });
      }
      if (!password || typeof password !== 'string') {
        return res.status(400).json({ error: 'Password is required.' });
      }

      const result = await userService.login(req.body);
      res.status(200).json(result);
    } catch (error: any) {
      res.status(400).json({ error: error.message });
    }
  }

  async getAllUsers(req: Request, res: Response) {
    try {
      const users = await userService.getAllUsers();
      res.status(200).json(users);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }

  async getUserById(req: Request, res: Response) {
    try {
      const id = parseInt(req.params.id as string);
      const user = await userService.getUserById(id);
      if (!user) {
        return res.status(404).json({ error: 'User not found' });
      }
      res.status(200).json(user);
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }
}
