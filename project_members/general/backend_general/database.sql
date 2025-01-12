CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    last_login DATETIME
);

CREATE TABLE employees (
    user_id VARCHAR(36) PRIMARY KEY,
    role VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE'),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE project_managers (
    user_id VARCHAR(36) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE projects (
    project_id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(100),
    description TEXT,
    status VARCHAR(20), -- might be removed later on
    start_date DATE,
    due_date DATE,
    completed_date DATE
);

-- breaking the many to many relationship with a middle relation
CREATE TABLE employee_projects (
    employee_id VARCHAR(36),
    project_id VARCHAR(36),
    join_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES employees(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- breaking the many to many relationship with a middle relation
CREATE TABLE manager_projects (
    manager_id VARCHAR(36),
    project_id VARCHAR(36),
    PRIMARY KEY (manager_id, project_id),
    FOREIGN KEY (manager_id) REFERENCES project_managers(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
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
    FOREIGN KEY (assigned_to) REFERENCES employees(user_id)
);

CREATE TABLE notifications (
    notification_id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(20),  -- COMMENT or EMAIL
    sender_id VARCHAR(36),
    title VARCHAR(100),
    content TEXT,
    task_id VARCHAR(36),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);