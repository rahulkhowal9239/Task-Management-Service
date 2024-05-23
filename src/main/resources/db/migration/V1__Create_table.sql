-- Create the users table
CREATE TABLE IF NOT EXISTS users(
  user_id VARCHAR(255) PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL
);

-- Create the tasks table
CREATE TABLE IF NOT EXISTS tasks(
  task_id VARCHAR(255) PRIMARY KEY,
  task_name VARCHAR(100) NOT NULL,
  active_status BOOLEAN,
  created_at TIMESTAMP NOT NULL
);

-- Create the user_tasks table
CREATE TABLE IF NOT EXISTS user_tasks(
  id SERIAL PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  task_id VARCHAR(255) NOT NULL,
  description TEXT,
  status VARCHAR(20),
  assign_date TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

-- Insert sample data into the users table
INSERT INTO users (user_id, username, email) VALUES
('8e9d1b82-dcc3-4a89-afcf-87a46c65b4a1', 'John Doe', 'john@example.com'),
('37f28e6a-6a36-4ff6-98d5-5fcb8e100e35', 'Jane Smith', 'jane@example.com'),
('4e5c0904-981a-47cc-b3ee-208a1920d174', 'Alice Johnson', 'alice@example.com');

-- Insert sample data into the tasks table
INSERT INTO tasks (task_id, task_name, active_status, created_at) VALUES
('4fc63419-6f22-4794-b1b8-c2f63c8592b0', 'Complete Project A', true, CURRENT_TIMESTAMP),
('d8486ae3-02a7-40cd-8360-b2ed07367284', 'Review Document B', false, CURRENT_TIMESTAMP),
('c27d1d54-6e9a-4df5-94b3-2b1fb28e4ed3', 'Prepare Presentation C', true, CURRENT_TIMESTAMP);

-- Insert sample data into the user_tasks table
INSERT INTO user_tasks (user_id, task_id, description, status, assign_date) VALUES
('8e9d1b82-dcc3-4a89-afcf-87a46c65b4a1', '4fc63419-6f22-4794-b1b8-c2f63c8592b0', 'Finish by end of week', 'In Progress', CURRENT_TIMESTAMP),
('37f28e6a-6a36-4ff6-98d5-5fcb8e100e35', 'd8486ae3-02a7-40cd-8360-b2ed07367284', 'Review and provide feedback', 'Pending', CURRENT_TIMESTAMP),
('4e5c0904-981a-47cc-b3ee-208a1920d174', 'c27d1d54-6e9a-4df5-94b3-2b1fb28e4ed3', 'Create slides and outline', 'In Progress', CURRENT_TIMESTAMP);

