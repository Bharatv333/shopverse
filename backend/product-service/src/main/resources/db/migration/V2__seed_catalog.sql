-- Starter data: the same 6 products you've used since Lesson 1.

INSERT INTO categories (id, name, slug)
VALUES (1, 'Electronics', 'electronics'),
       (2, 'Books', 'books'),
       (3, 'Fashion', 'fashion'),
       (4, 'Home', 'home');

INSERT INTO products (id, sku, name, slug, category_id, price, avg_rating, review_count)
VALUES (1, 'EL-LAP-001', 'Laptop Pro 14', 'laptop-pro-14', 1, 85000.00, 4.60, 0),
       (2, 'EL-MOU-002', 'Wireless Mouse', 'wireless-mouse', 1, 799.00, 4.20, 0),
       (3, 'BK-CLC-003', 'Clean Code', 'clean-code', 2, 650.00, 4.80, 0),
       (4, 'BK-SIA-004', 'Spring in Action', 'spring-in-action', 2, 900.00, 4.50, 0),
       (5, 'FA-DNM-005', 'Denim Jacket', 'denim-jacket', 3, 2499.00, 3.90, 0),
       (6, 'HM-MUG-006', 'Coffee Mug', 'coffee-mug', 4, 299.00, 4.00, 0);

INSERT INTO product_images (product_id, url, alt_text, sort_order)
VALUES (1, 'https://picsum.photos/seed/laptop/600/400', 'Laptop Pro 14 front', 0),
       (1, 'https://picsum.photos/seed/laptop2/600/400', 'Laptop Pro 14 side', 1),
       (3, 'https://picsum.photos/seed/cleancode/600/400', 'Clean Code cover', 0);

