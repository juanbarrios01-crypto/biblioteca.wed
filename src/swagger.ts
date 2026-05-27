const swaggerSpec = {
  openapi: '3.0.0',
  info: {
    title: 'Library Management System API',
    version: '1.0.0',
    description: 'API for managing library books, users, and loans',
  },
  components: {
    securitySchemes: {
      bearerAuth: {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT',
      },
    },
  },
  security: [
    { bearerAuth: [] }
  ],
  paths: {
    '/api/users/register': {
      post: {
        summary: 'Register a new user',
        tags: ['Users'],
        security: [],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  email: { type: 'string' },
                  password: { type: 'string' },
                  name: { type: 'string' },
                  role: { type: 'string', example: 'STUDENT' }
                }
              }
            }
          }
        },
        responses: {
          201: { description: 'User registered successfully' }
        }
      }
    },
    '/api/users/login': {
      post: {
        summary: 'Login a user',
        tags: ['Users'],
        security: [],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  email: { type: 'string' },
                  password: { type: 'string' }
                }
              }
            }
          }
        },
        responses: {
          200: { description: 'Login successful, returns token' }
        }
      }
    },
    '/api/users': {
      get: {
        summary: 'Get all users (Admin only)',
        tags: ['Users'],
        responses: {
          200: { description: 'Returns a list of all users' }
        }
      }
    },
    '/api/users/{id}': {
      get: {
        summary: 'Get user by ID with loan history (Admin only)',
        tags: ['Users'],
        parameters: [
          {
            name: 'id',
            in: 'path',
            required: true,
            schema: { type: 'integer' },
            description: 'User ID'
          }
        ],
        responses: {
          200: { description: 'Returns user details with loan history' },
          404: { description: 'User not found' }
        }
      }
    },
    '/api/books': {
      get: {
        summary: 'Get all books (Searchable by query)',
        tags: ['Books'],
        security: [],
        parameters: [
          {
            name: 'title',
            in: 'query',
            required: false,
            schema: { type: 'string' },
            description: 'Filter books by title'
          },
          {
            name: 'author',
            in: 'query',
            required: false,
            schema: { type: 'string' },
            description: 'Filter books by author'
          },
          {
            name: 'category',
            in: 'query',
            required: false,
            schema: { type: 'string' },
            description: 'Filter books by category'
          }
        ],
        responses: {
          200: { description: 'Returns all books' }
        }
      },
      post: {
        summary: 'Create a book (Admin only)',
        tags: ['Books'],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  title: { type: 'string' },
                  author: { type: 'string' },
                  category: { type: 'string' },
                  stock: { type: 'number' }
                }
              }
            }
          }
        },
        responses: {
          201: { description: 'Book created' }
        }
      }
    },
    '/api/books/{id}': {
      get: {
        summary: 'Get book by ID',
        tags: ['Books'],
        security: [],
        parameters: [
          {
            name: 'id',
            in: 'path',
            required: true,
            schema: { type: 'integer' },
            description: 'Book ID'
          }
        ],
        responses: {
          200: { description: 'Returns book details' },
          404: { description: 'Book not found' }
        }
      },
      put: {
        summary: 'Update book details (Admin only)',
        tags: ['Books'],
        parameters: [
          {
            name: 'id',
            in: 'path',
            required: true,
            schema: { type: 'integer' },
            description: 'Book ID'
          }
        ],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  title: { type: 'string' },
                  author: { type: 'string' },
                  category: { type: 'string' },
                  stock: { type: 'number' }
                }
              }
            }
          }
        },
        responses: {
          200: { description: 'Book updated successfully' },
          404: { description: 'Book not found' }
        }
      },
      delete: {
        summary: 'Delete a book (Admin only)',
        tags: ['Books'],
        parameters: [
          {
            name: 'id',
            in: 'path',
            required: true,
            schema: { type: 'integer' },
            description: 'Book ID'
          }
        ],
        responses: {
          204: { description: 'Book deleted successfully' },
          404: { description: 'Book not found' }
        }
      }
    },
    '/api/loans': {
      get: {
        summary: 'Get all loans (Admin only)',
        tags: ['Loans'],
        responses: {
          200: { description: 'Returns all loans' }
        }
      },
      post: {
        summary: 'Create a new loan',
        tags: ['Loans'],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  bookId: { type: 'number' },
                  estimatedReturn: { type: 'string', format: 'date-time' }
                }
              }
            }
          }
        },
        responses: {
          201: { description: 'Loan created successfully' }
        }
      }
    },
    '/api/loans/my-loans': {
      get: {
        summary: 'Get my loans',
        tags: ['Loans'],
        responses: {
          200: { description: 'Returns loans of the authenticated user' }
        }
      }
    },
    '/api/loans/{id}/return': {
      post: {
        summary: 'Return a borrowed book (Admin only)',
        tags: ['Loans'],
        parameters: [
          {
            name: 'id',
            in: 'path',
            required: true,
            schema: { type: 'integer' },
            description: 'Loan ID'
          }
        ],
        responses: {
          200: { description: 'Book returned successfully, stock incremented' },
          400: { description: 'Invalid loan status or not found' }
        }
      }
    }
  }
};

export default swaggerSpec;
