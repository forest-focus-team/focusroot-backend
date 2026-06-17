INSERT INTO tree_species (id, name, image_url, cost_points, growth_time, unlocked_at_minutes) VALUES
(1, 'Cây mầm xanh', 'sapling.png', 0, 25, 0),
(2, 'Cây hướng dương', 'sunflower.png', 50, 25, 50),
(3, 'Cây thông Noel', 'pine.png', 100, 50, 100),
(4, 'Cây sồi cổ thụ', 'oak.png', 200, 50, 200),
(5, 'Cây hoa anh đào', 'sakura.png', 500, 90, 500)
ON DUPLICATE KEY UPDATE name=VALUES(name);