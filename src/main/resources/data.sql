INSERT INTO tree_species (id, name, image_url, cost_points, growth_time, unlocked_at_minutes, buy_cost_coins) VALUES
(1, 'Cây mầm xanh', 'sapling.png', 0, 25, 0, 0),
(2, 'Cây hướng dương', 'sunflower.png', 50, 25, 50, 20),
(3, 'Cây thông Noel', 'pine.png', 100, 50, 100, 50),
(4, 'Cây sồi cổ thụ', 'oak.png', 200, 50, 200, 100),
(5, 'Cây hoa anh đào', 'sakura.png', 500, 90, 500, 200),
(6, 'Cây xương rồng sa mạc', 'cactus.png', 150, 30, 150, 40)
ON DUPLICATE KEY UPDATE name=VALUES(name), buy_cost_coins=VALUES(buy_cost_coins);