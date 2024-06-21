CREATE DATABASE IF NOT EXISTS `db_auth`;
CREATE DATABASE IF NOT EXISTS `db_customer`;
CREATE DATABASE IF NOT EXISTS `db_inventory`;
CREATE DATABASE IF NOT EXISTS `db_order`;

CREATE USER 'user_app'@'%' IDENTIFIED BY 'user_pass';

GRANT ALL ON db_auth.* TO 'user_app'@'%';
GRANT ALL ON db_customer.* TO 'user_app'@'%';
GRANT ALL ON db_inventory.* TO 'user_app'@'%';
GRANT ALL ON db_order.* TO 'user_app'@'%';