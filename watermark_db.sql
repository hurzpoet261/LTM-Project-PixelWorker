CREATE DATABASE watermark_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE watermark_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE image_jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    
    job_type VARCHAR(50) NOT NULL DEFAULT 'WATERMARK', 
    job_params VARCHAR(255) NULL, 
    
    input_filename VARCHAR(255) NOT NULL,
    input_path VARCHAR(500) NOT NULL,
    
    output_path VARCHAR(500) NULL,
    output_filename VARCHAR(255) NULL, 

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id)
);

SELECT 'Database đã được tạo mới thành công!' AS status;