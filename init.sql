CREATE TABLE public.squares (
                                id serial PRIMARY KEY,
                                x int NOT NULL,
                                y int NOT NULL,
                                size int NOT NULL,
                                text varchar,
                                color_hex varchar
);

CREATE TABLE public.connections (
                                    id serial PRIMARY KEY,
                                    point1_x int NOT NULL,
                                    point1_y int NOT NULL,
                                    point2_x int NOT NULL,
                                    point2_y int NOT NULL
);

