import express from 'express';
import cors from 'cors';
import userRoutes from './infrastructure/routes/userRoutes';
import bookRoutes from './infrastructure/routes/bookRoutes';
import loanRoutes from './infrastructure/routes/loanRoutes';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

import swaggerUi from 'swagger-ui-express';
import swaggerSpec from './swagger';

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.use(express.static('public'));

app.use('/api/users', userRoutes);
app.use('/api/books', bookRoutes);
app.use('/api/loans', loanRoutes);

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
