CREATE TABLE IF NOT EXISTS tasks(
  id SERIAL PRIMARY KEY,
  userId INT NOT NULL,
  task_name VARCHAR(100) NOT NULL,
  description TEXT,
  status VARCHAR(20),
  created_at TIMESTAMP NOT NULL,
  FOREIGN KEY (userId) REFERENCES Users(id)
);