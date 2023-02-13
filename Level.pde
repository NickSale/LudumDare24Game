public class Level
{
  int sizeX, sizeZ;
  int[] floorMap;
  ArrayList<Enemy> enemies;
  Butterfly butterfly[];
  int floorR, floorG, floorB;

  int playerStartX, playerStartZ; //tile number
  float playerStartT;

  public Level(int x, int z) //called only from Factory
  {
    sizeX = x; 
    sizeZ = z;
    enemies = new ArrayList<Enemy>();
    butterfly = new Butterfly[5];
  }

  public void update()
  {
    for (Enemy e : enemies)
      if (!e.dead)
        e.update();
    if (butterfly[0] != null)
      butterfly[0].update();
  }

  public void draw()
  {
    for (int i = 0; i < sizeX; i++)
      for (int j = 0; j < sizeZ; j++)
      {
        if (sqrt(sq(i-player.posX/tileWidth)+sq(j-player.posZ/tileWidth)) < 15)
        {
          int wallType = floorMap[j*sizeX+i];
          if (wallType == 1)
            drawWall(weedWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 2)
            drawWall(coralWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 3)
            drawWall(jungleWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 4)
            drawWall(cliffWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 5)
            drawWall(ruinWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 6)
            drawWall(cityOne, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 7)
            drawWall(labWall, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 8)
            drawWall(labWallOne, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 9)
            drawWall(labWallTwo, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 10)
            drawWall(labWallThree, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 11)
            drawWall(portal, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 12)
            drawWall(labLevelOne, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 13)
            drawWall(labLevelTwo, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 14)
            drawWall(labLevelThree, i*tileWidth, -boxHeight, j*tileWidth);            
          else if (wallType == 15)
            drawWall(labLevelFour, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 16)
            drawWall(labLevelFive, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 17)
            drawWall(portal, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 18)
            drawWall(portal, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 19)
            drawWall(portal, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 20)
            drawWall(portal, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 21)
            drawWall(portalTwo, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 22)
            drawWall(lab, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 23)
            drawWall(cityTwo, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 24)
            drawWall(cityThree, i*tileWidth, -boxHeight, j*tileWidth);
          else if (wallType == 25)
            drawWall(cityFour, i*tileWidth, -boxHeight, j*tileWidth);
        }
      }
    fill(floorR, floorG, floorB);
    beginShape(QUADS);
    vertex(0, -boxHeight/2, 0);
    vertex(sizeX*tileWidth, -boxHeight/2, 0);
    vertex(sizeX*tileWidth, -boxHeight/2, sizeZ*tileWidth);
    vertex(0, -boxHeight/2, sizeZ*tileWidth);
    endShape();

    for (Enemy e : enemies)
      if (sqrt(sq(e.posX-player.posX)+sq(e.posZ-player.posZ)) < tileWidth * 15)
        e.draw();
    for (int i = 0; i < 5; i++)
      if (butterfly[i] != null)
        butterfly[i].draw();
    if (currentLevel == 0) //1, 4
    {
      float sideX = 4*tileWidth - player.posZ;
      float sideZ = -(tileWidth - player.posX);
      float magnitude = sqrt(sideX*sideX+sideZ*sideZ);
      sideX /= magnitude; 
      sideZ /= magnitude;
      pushMatrix();
      translate(tileWidth, -175, 4*tileWidth);
      textureMode(NORMALIZED);
      beginShape(QUADS);
      texture(prof);
      vertex(-sideX*22, 0, -sideZ*22, 0, 0);
      vertex(sideX*22, 0, sideZ*22, 1, 0);
      vertex(sideX*22, 105, sideZ*22, 1, 1);
      vertex(-sideX*22, 105, -sideZ*22, 0, 1);
      endShape();
      popMatrix();
    }
  }

  void updateShot()
  {
    for (int i = 0; i < enemies.size(); i++)
      if (enemies.get(i).shot(player.posX, player.posY, player.posZ, player.theta, player.phi, 1))
        lastEnemy = i;
  }

  public int getEnemyDamage(float x, float z, float r, int id)
  {
    for (int i = 0; i < enemies.size(); i++)
      if (!enemies.get(i).dead && enemies.get(i).id != id)
        if (sqrt((x-enemies.get(i).posX)*(x-enemies.get(i).posX)+(z-enemies.get(i).posZ)*(z-enemies.get(i).posZ)) < r)
        {
          if (id == -1)
          {
            enemies.get(i).dead = true;
            enemies.get(i).onDead();
            sfxPlayer.close();
            sfxPlayer = minim.loadFile("sounds/hit.wav", 512);
            sfxPlayer.play();
            if (i == lastEnemy)
              lastEnemy = -1;
          }
          return enemies.get(i).damage;
        }
    return 0;
  }

  public boolean isLevelCollision(float x, float z, float r) //optimise use of 'r' - times instead
  {
    for (float i = -r; i <= r; i += r)
      for (float j = -r; j <= r; j += r)
      {
        int tileX = round((x+i)/tileWidth);
        int tileZ = round((z+j)/tileWidth);
        if (floorMap[tileZ*sizeX+tileX] == 11 && !complete[0])
        {
          currentLevel = 1;
          butterflyCloud = true;
          sfxPlayer.close();
          sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
          sfxPlayer.play();
          musicPlayer.close();
          musicPlayer = minim.loadFile("sounds/small/underwater.mp3", 2048);
          musicPlayer.loop();
          resetLevel();
          player.placePlayer(level[1]);
        }
        else if (floorMap[tileZ*sizeX+tileX] == 17 && !complete[1] && complete[0])
        {
          currentLevel = 2;
          butterflyCloud = true;
          sfxPlayer.close();
          sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
          sfxPlayer.play();
          musicPlayer.close();
          musicPlayer = minim.loadFile("sounds/small/lizard.mp3", 2048);
          musicPlayer.loop();
          resetLevel();
          player.placePlayer(level[2]);
        }
        else if (floorMap[tileZ*sizeX+tileX] == 18 && !complete[2])// && complete[1])
        {
          currentLevel = 3;
          butterflyCloud = true;
          sfxPlayer.close();
          sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
          sfxPlayer.play();
          musicPlayer.close();
          musicPlayer = minim.loadFile("sounds/small/dinosaur.mp3", 2048);
          musicPlayer.loop();
          resetLevel();
          player.placePlayer(level[3]);
        }
        else if (floorMap[tileZ*sizeX+tileX] == 19 && !complete[3] && complete[2])
        {
          currentLevel = 4;
          butterflyCloud = true;
          sfxPlayer.close();
          sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
          sfxPlayer.play();
          musicPlayer.close();
          musicPlayer = minim.loadFile("sounds/small/monkey.mp3", 2048);
          musicPlayer.loop();
          resetLevel();
          player.placePlayer(level[4]);
        }
        else if (floorMap[tileZ*sizeX+tileX] == 20 && !complete[4] && complete[3])
        {
          currentLevel = 5;
          butterflyCloud = true;
          sfxPlayer.close();
          sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
          sfxPlayer.play();
          musicPlayer.close();
          musicPlayer = minim.loadFile("sounds/small/city.mp3", 2048);
          musicPlayer.loop();
          resetLevel();
          player.placePlayer(level[5]);
        }
        else if (floorMap[tileZ*sizeX+tileX] == 21 && gameComplete)
        {
          gameState = 3;
        }
        else if (floorMap[tileZ*sizeX+tileX] != 0)
          return true;
      }
    return false;
  }
}

