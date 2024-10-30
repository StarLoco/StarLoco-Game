USE starloco_game;

-- 17 instead of 16.
-- 16 means more enemy than allies
-- 1 means more than 1 allies
-- So 16 + 1 = 17 , that means more enemy than allies + need more than 1 player.
UPDATE challenge SET conditions = 17 WHERE id = 44;