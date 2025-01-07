CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100) UNIQUE,
    full_name VARCHAR(100),
    password VARCHAR(255),
    profile_image VARCHAR(255),
    last_login DATETIME,
    is_active BOOLEAN
);

CREATE TABLE projects (
    project_id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(100),
    description TEXT,
    manager_id VARCHAR(36),
    status VARCHAR(20),
    start_date DATE,
    due_date DATE,
    completed_date DATE,
    FOREIGN KEY (manager_id) REFERENCES users(user_id)
);

CREATE TABLE project_members (
    project_id VARCHAR(36),
    user_id VARCHAR(36),
    role VARCHAR(50),
    join_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE tasks (
    task_id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36),
    assigned_to VARCHAR(36),
    title VARCHAR(100),
    description TEXT,
    status VARCHAR(20),
    priority VARCHAR(20),
    due_date DATE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (assigned_to) REFERENCES users(user_id)
);
