INSERT INTO tree_species (id, name, image_url, required_minutes, coin_cost) VALUES
(1, 'Cây mầm xanh', 'sapling.png', 0, 0),
(2, 'Cây hướng dương', 'sunflower.png', 25, 50),
(3, 'Cây thông Noel', 'pine.png', 50, 100),
(4, 'Cây sồi cổ thụ', 'oak.png', 100, 200),
(5, 'Cây hoa anh đào', 'sakura.png', 200, 500),
(6, 'Cây tre xanh', 'bamboo.png', 60, 150)
ON DUPLICATE KEY UPDATE name=VALUES(name);
