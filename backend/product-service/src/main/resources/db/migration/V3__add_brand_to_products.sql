ALTER TABLE products ADD COLUMN brand varchar(100) NULL AFTER name;

UPDATE products set brand = 'ShopVerse Basics' where id IN (2, 6);