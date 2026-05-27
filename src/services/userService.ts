import { prisma } from '../infrastructure/prismaClient';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';

const SECRET = process.env.JWT_SECRET || 'secret';

export class UserService {
  async register(data: any) {
    const { email, password, name, role } = data;
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await prisma.user.create({
      data: {
        email,
        password: hashedPassword,
        name,
        role: role || 'STUDENT',
      },
    });
    return user;
  }

  async login(data: any) {
    const { email, password } = data;
    const user = await prisma.user.findUnique({ where: { email } });
    if (!user) throw new Error('Invalid email or password');

    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) throw new Error('Invalid email or password');

    const token = jwt.sign({ id: user.id, role: user.role }, SECRET, { expiresIn: '1h' });
    return { token, user };
  }

  async getAllUsers() {
    return prisma.user.findMany({ select: { id: true, email: true, name: true, role: true } });
  }

  async getUserById(id: number) {
    return prisma.user.findUnique({
      where: { id },
      select: { id: true, email: true, name: true, role: true, loans: true }
    });
  }
}
