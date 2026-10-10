CREATE DATABASE IF NOT EXISTS product_db;
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS inventory_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS payment_db;

GRANT ALL PRIVILEGES ON product_db.*   TO 'shop'@'%';
GRANT ALL PRIVILEGES ON user_db.*      TO 'shop'@'%';
GRANT ALL PRIVILEGES ON auth_db.*      TO 'shop'@'%';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'shop'@'%';
GRANT ALL PRIVILEGES ON order_db.*     TO 'shop'@'%';
GRANT ALL PRIVILEGES ON payment_db.*   TO 'shop'@'%';