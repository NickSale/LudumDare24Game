public Level getLevel(int number)
{
  if (number == 0)
  {
    Level ret = new Level(14, 9);
    ret.floorMap = new int[]
    {
      7, 0, 1, 0, 3, 0, 4, 0, 5, 0, 6, 0, 0, 0,
      7,12,11,13,17,14,18,15,19,16,20, 7, 7, 0,
      7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,21, 7,
      7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0,
      7, 0, 0, 7, 7, 7, 7, 7, 7, 7, 7, 0, 0, 0,
     10, 0, 0, 7, 7, 0, 0, 7, 0, 0, 0, 0, 0, 0,
      9, 0, 0, 7,22, 0, 0, 7, 0, 0, 0, 0, 0, 0,
      8, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0,
      7, 7, 7, 7, 7, 7, 7, 7, 0, 0, 0, 0, 0, 0
    };
    ret.playerStartX = 6;
    ret.playerStartZ = 5;
    ret.playerStartT = -PI/4;
    ret.floorR = 90; ret.floorG = 90; ret.floorB = 90;
    for (int i = 0; i < 5; i++)
      if (complete[i])
        ret.butterfly[i] = new Butterfly(2*(i+1), 1, true);
    return ret;
  }
  if (number == 1) //fish
  {
    Level ret = new Level(21, 11);
    ret.floorMap = new int[]
    {
      0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0,
      1, 0, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1,
      1, 0, 0, 0, 2, 0, 1, 0, 0, 0, 1, 2, 1, 0, 2, 0, 0, 0, 0, 0, 1,
      1, 1, 1, 2, 2, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1,
      1, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 0, 0, 1, 1, 1, 1, 1, 1,
      1, 0, 2, 0, 0, 0, 0, 1, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 2, 2, 0, 1, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 1, 0, 1,
      1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 1, 1, 1, 1, 1, 1,
      0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0
    };
    ret.playerStartX = 1;
    ret.playerStartZ = 1;
    ret.playerStartT = PI/4;
    ret.enemies.add(new Fish(5, 1)); ret.enemies.add(new Fish(5, 5)); ret.enemies.add(new Fish(2, 5));
    ret.enemies.add(new Fish(9, 1)); ret.enemies.add(new Fish(11, 2)); ret.enemies.add(new Fish(16, 2));
    ret.enemies.add(new Fish(4, 7)); ret.enemies.add(new Fish(4, 9)); ret.enemies.add(new Fish(5, 9));
    ret.enemies.add(new Fish(4, 7)); ret.enemies.add(new Fish(13, 5)); ret.enemies.add(new Fish(5, 15));
    ret.butterfly[0] = new Butterfly(19, 8, false);
    ret.floorR = 90; ret.floorG = 90; ret.floorB = 130;
    return ret;
  }
  else if (number == 2) //lizards
  {
    Level ret = new Level(12, 12);
    ret.floorMap = new int[]
    {
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3,
      3, 3, 3, 3, 0, 3, 0, 0, 0, 0, 0, 3,
      3, 0, 0, 0, 0, 3, 0, 3, 3, 0, 3, 3,
      3, 0, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 
      3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 3, 
      3, 0, 0, 0, 3, 0, 3, 0, 3, 3, 0, 3, 
      3, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 3,
      3, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 3, 
      3, 0, 0, 0, 3, 0, 3, 0, 3, 3, 3, 3, 
      3, 3, 3, 0, 0, 0, 3, 0, 0, 0, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3
    };
    ret.playerStartX = 1;
    ret.playerStartZ = 1;
    ret.playerStartT = PI/2;
    ret.enemies.add(new Lizard(4, 1)); ret.enemies.add(new Lizard(3, 3)); ret.enemies.add(new Lizard(2, 5));
    ret.enemies.add(new Lizard(1, 6)); ret.enemies.add(new Lizard(3, 7)); ret.enemies.add(new Lizard(5, 9));
    ret.enemies.add(new Lizard(6, 5)); ret.enemies.add(new Lizard(7, 6)); ret.enemies.add(new Lizard(9, 10));
    ret.enemies.add(new Lizard(9, 5)); ret.enemies.add(new Lizard(10, 2));
    ret.butterfly[0] = new Butterfly(6, 2, false);
    ret.floorR = 76; ret.floorG = 153; ret.floorB = 0;
    return ret;
  }
  else if (number == 3) //dinosaurs
  {
    Level ret = new Level(12, 12);
    ret.floorMap = new int[]
    {
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
      4, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 3,
      4, 4, 3, 3, 3, 0, 0, 0, 0, 3, 0, 3,
      3, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 4,
      3, 0, 0, 0, 0, 0, 0, 3, 3, 3, 4, 4,
      3, 0, 4, 4, 0, 3, 3, 0, 0, 0, 0, 4,
      3, 0, 4, 4, 0, 0, 3, 0, 0, 0, 0, 4,
      3, 0, 0, 4, 4, 0, 3, 0, 3, 0, 0, 3,
      3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3,
      3, 0, 0, 0, 0, 0, 3, 3, 0, 0, 3, 3,
      3, 4, 4, 4, 4, 4, 3, 0, 0, 0, 3, 3,
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3
    };
    ret.playerStartX = 1;
    ret.playerStartZ = 1;
    ret.playerStartT = PI/2;
    ret.enemies.add(new Dinosaur(5, 2)); ret.enemies.add(new Dinosaur(4, 4)); ret.enemies.add(new Dinosaur(1, 8));
    ret.enemies.add(new Dinosaur(3, 8)); ret.enemies.add(new Dinosaur(3, 9)); ret.enemies.add(new Dinosaur(5, 7));
    ret.enemies.add(new Dinosaur(7, 7)); ret.enemies.add(new Dinosaur(11, 5)); ret.enemies.add(new Dinosaur(8, 5));
    ret.butterfly[0] = new Butterfly(7, 10, false);
    ret.floorR = 200; ret.floorG = 200; ret.floorB = 120;
    return ret;
  }
  else if (number == 4) //monkeys
  {
    Level ret = new Level(14, 12);
    ret.floorMap = new int[]
    {
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
      5, 5, 5, 0, 0, 0, 5, 5, 0, 0, 0, 0, 5, 5,
      5, 5, 5, 0, 5, 5, 5, 5, 5, 0, 5, 0, 5, 5,
      5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 5,
      5, 0, 5, 0, 5, 5, 0, 5, 5, 0, 5, 5, 5, 5,
      5, 0, 5, 0, 5, 0, 0, 0, 5, 0, 5, 5, 5, 5,
      5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
      5, 5, 5, 0, 5, 0, 0, 0, 5, 0, 5, 5, 0, 5,
      5, 5, 5, 0, 5, 5, 0, 5, 5, 0, 5, 0, 0, 5,
      5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5,
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5
    };
    ret.playerStartX = 1;
    ret.playerStartZ = 4;
    ret.playerStartT = 0;
    ret.butterfly[0] = new Butterfly(12, 3, false);
    ret.enemies.add(new Monkey(3, 6)); ret.enemies.add(new Monkey(5, 5)); ret.enemies.add(new Monkey(4, 3));
    ret.enemies.add(new Monkey(5, 1)); ret.enemies.add(new Monkey(3, 9)); ret.enemies.add(new Monkey(6, 8));
    ret.enemies.add(new Monkey(6, 4)); ret.enemies.add(new Monkey(8, 1)); ret.enemies.add(new Monkey(12, 7));
    ret.floorR = 200; ret.floorG = 200; ret.floorB = 120;
    return ret;
  }
  else if (number == 5) //humans - NOT DONE
  {
    Level ret = new Level(12, 15);
    ret.floorMap = new int[]
    {
      6, 6,23,24,25, 6,23,24,25, 6,23,24,
      6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,24,
      6, 0, 6, 6,23, 0, 0, 6, 6,23, 0,25,
      6, 0,23,24,25, 0, 0,24,25, 6, 0, 6,
      6, 0, 6,23,24, 0, 0,23,24,25, 6, 6,
      6, 0, 0, 0, 0, 0, 0, 0, 0, 0,25, 6,
      6, 6,23,24,25, 0, 0, 6, 6, 0,24,25,
      6, 6, 6, 0, 0, 0, 0,24,25, 0,23,24,
      6, 0, 6,23,24, 0, 0, 0, 0, 0,25, 6,
      6, 0, 0, 0,24, 0, 0, 6, 6, 6,23,24,
      6, 6,23, 0,25, 0, 0, 0, 0,25, 0, 6,
      6, 0, 6, 0,23, 0, 0, 6, 0,23, 0,25,
      6, 0,23, 0,25, 0, 0, 6,23, 0, 0, 6,
      6, 0, 0, 0, 0, 0, 0, 0, 0, 0,24, 6,
      6, 6, 6,23,24,25, 6, 6,23,24,25, 6
    };
    ret.playerStartX = 10;
    ret.playerStartZ = 3;
    ret.playerStartT = PI;
    ret.enemies.add(new Man(6, 1)); ret.enemies.add(new Man(1, 2)); ret.enemies.add(new Man(3, 5));
    ret.enemies.add(new Man(7, 5)); ret.enemies.add(new Man(9, 6)); ret.enemies.add(new Man(3, 7));
    ret.enemies.add(new Man(8, 11)); ret.enemies.add(new Man(9, 12)); ret.enemies.add(new Man(3, 12));
    ret.enemies.add(new Man(1, 11)); ret.enemies.add(new Man(2, 9));
    ret.butterfly[0] = new Butterfly(1, 8, false);
    ret.floorR = 150; ret.floorG = 150; ret.floorB = 150;
    return ret;
  }
  else
  {
   print("LEVEL " + number + " NOT FOUND.");
   exit();
   return null; //keep the method happy...
  }
}

