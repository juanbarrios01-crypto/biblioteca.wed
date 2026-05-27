import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('Starting seeding database...');

  // 1. Seed Users
  const adminEmail = 'admin@library.com';
  const studentEmail = 'student@library.com';

  const existingAdmin = await prisma.user.findUnique({
    where: { email: adminEmail }
  });

  if (!existingAdmin) {
    const hashedPassword = await bcrypt.hash('password123', 10);
    const admin = await prisma.user.create({
      data: {
        email: adminEmail,
        password: hashedPassword,
        name: 'Administrador Principal',
        role: 'ADMIN'
      }
    });
    console.log('Admin user created:', admin.email);
  } else {
    console.log('Admin user already exists');
  }

  const existingStudent = await prisma.user.findUnique({
    where: { email: studentEmail }
  });

  if (!existingStudent) {
    const hashedPassword = await bcrypt.hash('password123', 10);
    const student = await prisma.user.create({
      data: {
        email: studentEmail,
        password: hashedPassword,
        name: 'Estudiante de Prueba',
        role: 'STUDENT'
      }
    });
    console.log('Student user created:', student.email);
  } else {
    console.log('Student user already exists');
  }

  // 2. Seed Books
  const booksData = [
    {
      title: 'Clean Code',
      author: 'Robert C. Martin',
      category: 'Programación',
      stock: 5
    },
    {
      title: 'Design Patterns',
      author: 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides',
      category: 'Programación',
      stock: 4
    },
    {
      title: 'Cien años de soledad',
      author: 'Gabriel García Márquez',
      category: 'Literatura',
      stock: 8
    },
    {
      title: 'Breve historia del tiempo',
      author: 'Stephen Hawking',
      category: 'Ciencia',
      stock: 3
    },
    {
      title: 'The Pragmatic Programmer',
      author: 'Andrew Hunt, David Thomas',
      category: 'Programación',
      stock: 6
    },
    {
      title: 'Refactoring',
      author: 'Martin Fowler',
      category: 'Programación',
      stock: 2
    }
  ];

  const bookCount = await prisma.book.count();
  if (bookCount === 0) {
    for (const book of booksData) {
      const createdBook = await prisma.book.create({
        data: book
      });
      console.log(`Book created: "${createdBook.title}" by ${createdBook.author}`);
    }
  } else {
    console.log(`Books already exist in the database (count: ${bookCount})`);
  }

  console.log('Seeding finished successfully.');
}

main()
  .catch((e) => {
    console.error('Error during seeding:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
