-- Вставка данных с проверкой через WHERE NOT EXISTS
INSERT INTO item (title, description, img_path, count, price)
SELECT 'Тапки', 'обычные резиновые тапки', 'image/slippers.jpg', 0, 350.00
WHERE NOT EXISTS (SELECT 1 FROM item WHERE title = 'Тапки');

INSERT INTO item (title, description, img_path, count, price)
SELECT 'Кепка', 'чистый 100% хлопок', 'image/cap.jpg', 0, 500.00
WHERE NOT EXISTS (SELECT 1 FROM item WHERE title = 'Кепка');