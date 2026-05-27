module.exports = {
  migrate: {
    connection: process.env.DATABASE_URL || 'file:./dev.db',
  },
};
