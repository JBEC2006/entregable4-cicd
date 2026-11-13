MERGE INTO juego (id, nombre, genero, plataforma, imagen_url) KEY (id) VALUES
(1, 'Hollow Knight', 'Aventura', 'PC', 'https://cdn.cloudflare.steamstatic.com/steam/apps/367520/library_600x900.jpg'),
(2, 'Celeste', 'Plataformas', 'Switch', 'https://cdn.cloudflare.steamstatic.com/steam/apps/504230/library_600x900.jpg'),
(3, 'Hades', 'Accion', 'PC', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1145360/library_600x900.jpg'),
(4, 'Stardew Valley', 'Simulacion', 'PC', 'https://cdn.cloudflare.steamstatic.com/steam/apps/413150/library_600x900.jpg'),
(5, 'The Witcher 3', 'Rpg', 'PS4', 'https://cdn.cloudflare.steamstatic.com/steam/apps/292030/library_600x900.jpg'),
(6, 'Doom Eternal', 'Accion', 'Xbox', 'https://cdn.cloudflare.steamstatic.com/steam/apps/782330/library_600x900.jpg'),
(7, 'Super Mario Odyssey', 'Plataformas', 'Switch', 'https://upload.wikimedia.org/wikipedia/en/8/8d/Super_Mario_Odyssey.jpg'),
(8, 'Skyrim', 'Rpg', 'PC', 'https://cdn.cloudflare.steamstatic.com/steam/apps/489830/library_600x900.jpg');

UPDATE juego SET imagen_url = '/sin-portada.svg' WHERE imagen_url LIKE 'https://placehold.co/%';
