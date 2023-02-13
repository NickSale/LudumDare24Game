import processing.core.*; 
import processing.xml.*; 

import java.awt.AWTException; 
import java.awt.Robot; 
import ddf.minim.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class natural_selection extends PApplet {





AudioPlayer musicPlayer;
AudioPlayer gunPlayer;
AudioPlayer sfxPlayer;
Minim minim;
Robot robot;
float lastMillis;
float delta; //in seconds
int fps;
int currentFrames;
float fpsTimer;
Player player;
Level[] level;
int currentLevel = 0, deaths = 0;
float tileWidth = 160, boxHeight = 140;
int idCounter = 0; //player id == -1;
int lastEnemy = -1;
boolean completeLevel, paused, gameComplete;
// 0 =splashscreen, 1 =intro, 2 =game, 3 =ending, 4 =died
int gameState = 0, comicFrame = 0;
float comicTimer, comicX, comicY, scrollSpeed = 2000;
boolean[] complete;
boolean butterflyCloud = false;
float cloudTimer = -0.5f, cloudTime = 1;

public void setup()
{
  size(700, 700, P3D);
  noSmooth();
  frame.setTitle("Natural Selection");
  player = new Player();
  level = new Level[6];
  complete = new boolean[5];
  minim = new Minim(this);
  musicPlayer = minim.loadFile("sounds/small/dinosaur.mp3", 2048);
  gunPlayer = minim.loadFile("sounds/gun.wav", 512);
  sfxPlayer = minim.loadFile("sounds/butterfly.wav", 512);
  resetEverything();
  try
  {
    robot = new Robot();
  }
  catch (AWTException e)
  {
    e.printStackTrace();
  }
  loadResources();
  musicPlayer.loop();
}

public void stop()
{
  gunPlayer.close();
  sfxPlayer.close();
  musicPlayer.close();
  minim.stop();  
  super.stop();
}

public void update()
{
  updateDeltaFrames();

  if (gameState == 1)
  {
    comicTimer += delta;
    float targetX = 0;
    float targetY = 0;
    if (comicTimer > 2.5f && comicFrame != 7)
    {
      targetX = ((comicFrame+1) % 2) * 700;
      targetY = floor((float)(comicFrame+1)/2) * 700;
      float dx = targetX - comicX, dy = targetY - comicY;
      float distance = 1;
      if (dx != 0 || dy != 0)
      {
        distance = sqrt(sq(dx)+sq(dy));
        if (distance <= 15)
        {
          comicX = targetX;
          comicY = targetY;
        }
        else
        {
          dx *= delta * scrollSpeed / distance; 
          dy *= delta * scrollSpeed / distance;
          comicX += dx; 
          comicY += dy;
        }
      }
    }
    if (comicTimer > 3)
    {
      comicX = targetX;
      comicY = targetY;
      comicTimer = 0;
      comicFrame++;
      if (comicFrame == 8)
      {
        gameState = 2;
        musicPlayer.close();
        musicPlayer = minim.loadFile("sounds/small/tick.mp3", 2048);
        musicPlayer.loop();
      }
    }
  }

  if (gameState == 2 && !paused)
  {
    level[currentLevel].update();
    player.update();
    if (completeLevel)
    {
      completeLevel = false;
      currentLevel = 0;
      musicPlayer.close();
      musicPlayer = minim.loadFile("sounds/small/tick.mp3", 2048);
      musicPlayer.loop();
      if (complete[0] && complete[1] && complete[2] && complete[3] && complete[4])
        gameComplete = true;
      resetLevel();
      player.placePlayer(level[0]);
    }
    if (player.health <= 0)
    {
      gameState = 4;
      deaths++;
    }
  }
}

public void draw()
{
  update();

  noCursor();

  if (gameState == 2)
  {
    //3d
    hint(ENABLE_DEPTH_TEST);
    noStroke();
    lights();
    directionalLight(230, 230, 190, -2, 0.5f, -2);
    background(180, 180, 200);  
    player.setCamera();
    level[currentLevel].draw();
    if (butterflyCloud)
    {
      cloudTimer += delta;
      if (cloudTimer >= cloudTime)
      {
        cloudTimer = -0.5f;
        butterflyCloud = false;
      }
      else
      {
        for (float i = 0; i < 2*PI; i += PI/8)
          for (float j = 0; j < 2*PI; j += PI/8)
          {
            float posX = player.posX + sin(player.theta)*60 + cos(i)*cos(j)*100*(cloudTimer/cloudTime);
            float posY = player.posY + sin(i)*100*(cloudTimer/cloudTime);
            float posZ = player.posZ + cos(player.theta)*60 + cos(i)*sin(j)*100*(cloudTimer/cloudTime);
            float sideX = player.posZ + i - player.posZ;
            float sideZ = -(posX - player.posX);
            float magnitude = sqrt(sideX*sideX+sideZ*sideZ);
            sideX /= magnitude; 
            sideZ /= magnitude;
            pushMatrix();
            translate(posX, posY, posZ);
            textureMode(NORMALIZED);
            beginShape(QUADS);
            texture(butterfly);
            vertex(-sideX*5, 0, -sideZ*5, 0, 0);
            vertex(sideX*5, 0, sideZ*5, 1, 0);
            vertex(sideX*5, 10, sideZ*5, 1, 1);
            vertex(-sideX*5, 10, -sideZ*5, 0, 1);
            endShape();
            popMatrix();
          }
      }
    }

    //2d
    hint(DISABLE_DEPTH_TEST);
    camera();  
    player.draw();
    drawHUD();
    if (currentLevel == 1)
    {
      fill(30, 40, 180, 180);
      rect(0, height/8, width, 7*height/8);
    }
  }
  else if (gameState == 4)
  {
    hint(DISABLE_DEPTH_TEST);
    camera();
    background(0, 0, 0);
    image(deathScreen, 0, 0, width, height);
  }
  else if (gameState == 0)
  {
    hint(DISABLE_DEPTH_TEST);
    camera();
    background(0, 0, 0);
    image(splashScreen, 0, 0, width, height);
  }
  else if (gameState == 1)
  {
    hint(DISABLE_DEPTH_TEST);
    camera();
    background(0, 0, 0);
    image(introScreen, -comicX, -comicY, 1400, 2800);
  }
  else if (gameState == 3)
  {
    hint(DISABLE_DEPTH_TEST);
    camera();
    background(0, 0, 0);
    image(endScreen, 0, 0, width, height);
  }
}


public void updateDeltaFrames()
{
  float currentMillis = millis();
  delta = (currentMillis - lastMillis) / 1000; //seconds
  lastMillis = currentMillis;
  fpsTimer += delta;
  if (fpsTimer >= 1)
  {
    fpsTimer = 0;
    fps = currentFrames;
    currentFrames = 0;
  }
  currentFrames++;
}

public void resetLevel()
{
  level[currentLevel] = getLevel(currentLevel);
}
public void resetEverything()
{
  for (int i = 0; i < 6; i++)
    level[i] = getLevel(i);
  currentLevel = 0;
  player = new Player();
  player.resetPlayer();
  comicX = 0; 
  comicY = 0;
  comicFrame = 0;
  idCounter = 0; 
  lastEnemy = -1;
  complete = new boolean[5];
  gameComplete = false;
  deaths = 0;
}

public class Butterfly
{
  float posX, posZ;
  float posY = -140;
  float wide = 40, high = 40;
  boolean decorative;
  
  public Butterfly(int x, int z, boolean dec)
  {
    posX = x * tileWidth;
    posZ = z * tileWidth;
    decorative = dec;
  }
  
  public void update()
  {
    float distance = sqrt(sq(posX-player.posX)+sq(posZ-player.posZ));
    if (distance < player.collisionWidth && !decorative)
    {
      completeLevel = true;
      complete[currentLevel-1] = true;
      sfxPlayer.close();
      sfxPlayer = minim.loadFile("sounds/butterfly.wav", 512);
      sfxPlayer.play();
    }
  }
  
  public void draw()
  {
    float sideX = posZ - player.posZ;
    float sideZ = -(posX - player.posX);
    float magnitude = sqrt(sideX*sideX+sideZ*sideZ);
    sideX /= magnitude; 
    sideZ /= magnitude;
    pushMatrix();
    translate(posX, posY, posZ);
    if (!paused)
      translate(0, sin(millis()/(18*PI))*2, 0);
    textureMode(NORMALIZED);
    beginShape(QUADS);
    texture(butterfly);
    vertex(-sideX*wide/2, 0, -sideZ*wide/2, 0, 0);
    vertex(sideX*wide/2, 0, sideZ*wide/2, 1, 0);
    vertex(sideX*wide/2, high, sideZ*wide/2, 1, 1);
    vertex(-sideX*wide/2, high, -sideZ*wide/2, 0, 1);
    endShape();
    popMatrix();
  }
}
public class Dinosaur extends Enemy
{
  public Dinosaur(int x, int z)
  {
    super(x, z);
    wide = 50; high = 90;
    posY = -160;
    health = 5; maxHealth = 4;
    damage = 1;
    speed = 90;
    huntRange = 4 * tileWidth;
  }
  
  public void update()
  {
    super.update();
  }
  
  public void onDead()
  {
    posY = -100;
    high = 40;
    wide = 90;
  }
  
  public void draw()
  {
    if (dead)
      super.drawBillboard(dinosaurDead);
    else if (shot)
      super.drawBillboard(dinosaurShot);
    else      
      super.drawBillboard(dinosaur);
  }
}
public class Enemy
{
  float posX, posZ;
  float posY; //fixed height
  float wide, high;
  int health, maxHealth;
  boolean shot;
  float shotTimer;
  boolean dead = false;
  int damage, id;
  float speed, huntRange;
  float randBob;

  public Enemy(int x, int z)
  {
    posX = x * tileWidth;
    posZ = z * tileWidth;
    id = idCounter;
    idCounter++;
    randBob = random(0.5f, 2);
  }

  public void update()
  {
    shotTimer += delta;
    if (shotTimer > 0.1f)
    {
      shot = false;
    }    
    if (!dead)
    {      
      float distance = sqrt(sq(posX-player.posX)+sq(posZ-player.posZ));
      if (distance < huntRange)
        {
          float dx = speed * delta * (player.posX - posX) / distance;
          float dz = speed * delta * (player.posZ - posZ) / distance;
          if (!level[currentLevel].isLevelCollision(posX+dx, posZ, wide/2))
            if (level[currentLevel].getEnemyDamage(posX+dx, posZ, wide/2, id) == 0)
              posX += dx;
          if (!level[currentLevel].isLevelCollision(posX, posZ+dz, wide/2))
            if (level[currentLevel].getEnemyDamage(posX, posZ+dz, wide/2, id) == 0)
              posZ += dz;
        }
      if (health <= 0)
      {
        lastEnemy = -1;
        dead = true;
        onDead();
      }
    }
  }

  public void onDead()
  {
  }
  public void draw()
  {
  }

  public boolean shot(float x, float y, float z, float theta, float phi, float damage)
  {
    if (dead)
      return false;
    fill(255, 255, 255);
    theta = theta % (2*PI);
    if (theta < 0)
      theta += 2*PI;
    float distance = sqrt((posZ-z)*(posZ-z)+(posX-x)*(posX-x));
    float angT = atan2(posX - x, posZ - z);
    if (angT < 0)
      angT += 2*PI;
    float angP = atan2(posY - (y - high/2), distance);
    float thetaThreshold = atan2(wide/2, distance);
    float phiThreshold = atan2(high/2, distance);
    if (angT > theta - thetaThreshold && angT < theta + thetaThreshold
      && angP > phi - phiThreshold && angP < phi + phiThreshold)
    {
      shot = true;
      shotTimer = 0;
      health -= damage;
      return true;
    }
    return false;
  }

  public void drawBillboard(PImage tex)
  {
    float sideX = posZ - player.posZ;
    float sideZ = -(posX - player.posX);
    float magnitude = sqrt(sideX*sideX+sideZ*sideZ);
    sideX /= magnitude; 
    sideZ /= magnitude;
    pushMatrix();
    translate(posX, posY, posZ);
    if (!paused && !dead)
      translate(0, sin(millis()/(18*PI*randBob))*2, 0);
    textureMode(NORMALIZED);
    beginShape(QUADS);
    texture(tex);
    vertex(-sideX*wide/2, 0, -sideZ*wide/2, 1, 0);
    vertex(sideX*wide/2, 0, sideZ*wide/2, 0, 0);
    vertex(sideX*wide/2, high, sideZ*wide/2, 0, 1);
    vertex(-sideX*wide/2, high, -sideZ*wide/2, 1, 1);
    endShape();
    popMatrix();
  }
}

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

public class Fish extends Enemy
{
  public Fish(int x, int z)
  {
    super(x, z);
    wide = 40; high = 40;
    posY = -160;
    health = 3; maxHealth = 3;
    damage = 1;
    speed = 170;
    huntRange = 4 * tileWidth;
  }
  
  public void update()
  {
    super.update();
  }
  
  public void onDead()
  {
    posY = -100;
    high = 30;
    wide = 40;
  }
  
  public void draw()
  {
    if (dead || shot)
      super.drawBillboard(fishShot);
    else      
      super.drawBillboard(fish);
  }
}
public void drawHUD()
{
  fill(40, 40, 220);
  rect(0, 0, width, height/8);
  textMode(SCREEN);
  fill(0, 0, 0);
  textFont(hudFont);
  text("FPS: " + fps, width/1.5f, height/22);
  text("Nickolai51", 11.5f*width/14, height/22);
  text("Lives: ", width/50, height/10);
  text("Bullets: ", width/50, height/22);
  for (int i = 0; i < 6; i++)
  {
    if (player.ammo > i)
      image(bullet, width/7+i*(width/30), height/80, width/33, height/22);
  }
  image(heart, width/7, height/15, width/20, height/22);
  if (player.health > 1)
  {
    image(heart, width/5, height/15, width/20, height/22);
    if (player.health > 2)
      image(heart, width/3.8f, height/15, width/20, height/22);
    else
      image(emptyHeart, width/3.8f, height/15, width/20, height/22);
  }
  else
  {
    image(emptyHeart, width/5, height/16, width/20, height/20);
    image(emptyHeart, width/3.8f, height/16, width/20, height/20);
  }
  text("Enemy Health: ", width/2.3f, height/10);
  fill(255, 0, 0);
  if (paused)
    text("PAUSED", width/2.4f, height/6);
  rect(4*width/11, height/55, (player.reloadTimer/player.reloadTime)*(width/4), height/28);
  if (lastEnemy != -1)
    rect(7.5f*width/11, height/14, ((float)level[currentLevel].enemies.get(lastEnemy).health
      /(float)level[currentLevel].enemies.get(lastEnemy).maxHealth)*(width/4), height/28);
  noFill(); stroke(0, 0, 0);
  rect(7.45f*width/11, height/14.1f, (width/3.95f), height/28.1f);
  rect(4*width/11, height/55, (width/3.95f), height/28.1f);
}
final float xSensitivity = 0.3f, ySensitivity = 0.2f;

public void keyPressed()
{
  if (key == 'w' || key == 'W')
    player.w = true;
  else if (key == 's' || key == 'S')
    player.s = true;
  else if (key == 'a' || key == 'A')
    player.a = true;
  else if (key == 'd' || key == 'D')
    player.d = true;
  else if ((key == 'p' || key == 'P') && gameState == 2)
    paused = !paused;
  else if ((key == 'r' || key == 'R') && gameState == 2 && player.ammo != 6 && !player.reloading)
  {
    player.reloading = true;
    sfxPlayer.close();
    sfxPlayer = minim.loadFile("sounds/reload_1.wav", 512);
    sfxPlayer.play();
  }
  if (key == ENTER || key == RETURN)
  {
    if (gameState == 0)
    {
      gameState = 1;
    }
    else if (gameState == 4)
    {
      gameState = 2;
      player.resetPlayer();
      resetLevel();
    }
    else if (gameState == 3)
    {
      gameState = 0;
      resetEverything();
      musicPlayer.close();
      musicPlayer = minim.loadFile("sounds/small/dinosaur.mp3", 2048);
      musicPlayer.loop();
    }
    else if (gameState == 1)
    {
      gameState = 2;
      butterflyCloud = true;
      sfxPlayer.close();
      sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
      sfxPlayer.play();
      musicPlayer.close();
      musicPlayer = minim.loadFile("sounds/small/tick.mp3", 2048);
      musicPlayer.loop();
    }
  }
}

public void keyReleased()
{
  if (key == 'w' || key == 'W')
    player.w = false;
  else if (key == 's' || key == 'S')
    player.s = false;
  else if (key == 'a' || key == 'A')
    player.a = false;
  else if (key == 'd' || key == 'D')
    player.d = false;
}

public void mouseMoved()
{
  if (!paused && gameState == 2)
    handleMouseMove();
}
public void mouseDragged()
{
  if (!paused && gameState == 2)
    handleMouseMove();
}

public void handleMouseMove()
{
  if (mouseX > width/2)
    player.theta -= xSensitivity * delta;
  else if (mouseX < width/2)
    player.theta += xSensitivity * delta;
  if (mouseY > height/2 + 1  && player.phi < 1)
    player.phi += ySensitivity * delta;
  else if (mouseY < height/2 - 1 && player.phi > -0.5f)
    player.phi -= ySensitivity * delta;
  robot.mouseMove(screen.width/2, screen.height/2);
}

public void mousePressed()
{
  if (!player.shooting && gameState == 2 && player.ammo > 0 && !player.reloading)
  {
    player.shooting = true;
    player.ammo--;
    if (player.ammo == 0)
    {
      player.reloading = true;
      sfxPlayer.close();
      sfxPlayer = minim.loadFile("sounds/reload_1.wav", 512);
      sfxPlayer.play();
    }
    player.shootTimer = 0;
    level[currentLevel].updateShot();
    gunPlayer.rewind();
    gunPlayer.play();
  }
}

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

  public void updateShot()
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

public class Lizard extends Enemy
{
  public Lizard(int x, int z)
  {
    super(x, z);
    wide = 80; high = 40;
    posY = -110;
    health = 4; maxHealth = 4;
    damage = 1;
    speed = 90;
    huntRange = 4 * tileWidth;
  }
  
  public void update()
  {
    super.update();
  }
  
  public void onDead()
  {
    posY = -110;
    high = 40;
    wide = 80;
  }
  
  public void draw()
  {     
    if (dead)
      super.drawBillboard(lizardDead);
    else if (shot)
      super.drawBillboard(lizardShot);
    else
      super.drawBillboard(lizard);
  }
}
public class Man extends Enemy
{
  public Man(int x, int z)
  {
    super(x, z);
    wide = 40; high = 90;
    posY = -160;
    health = 6; maxHealth = 6;
    damage = 1;
    speed = 130;
    huntRange = 4 * tileWidth;
  }
  
  public void update()
  {
    super.update();
  }
  
  public void onDead()
  {
    posY = -100;
    high = 40;
    wide = 90;
  }
  
  public void draw()
  {
    if (dead)
      super.drawBillboard(manDead);
    else if (shot)
      super.drawBillboard(manShot);
    else      
      super.drawBillboard(man);
  }
}
public class Monkey extends Enemy
{
  public Monkey(int x, int z)
  {
    super(x, z);
    wide = 30; high = 60;
    posY = -130;
    health = 4; maxHealth = 4;
    damage = 1;
    speed = 140;
    huntRange = 4 * tileWidth;
  }
  
  public void update()
  {
    super.update();
  }
  
  public void onDead()
  {
    posY = -100;
    high = 30;
    wide = 60;
  }
  
  public void draw()
  {
    if (dead)
      super.drawBillboard(monkeyDead);
    else if (shot)
      super.drawBillboard(monkeyShot);
    else      
      super.drawBillboard(monkey);
  }
}
public class Player
{
  float posX, posZ, theta, phi;
  float posY = -140; //fixed height
  float normalPosY = -140;
  boolean w, a, s, d, shooting, reloading;
  float shootTimer;
  float speed = 180;
  float collisionWidth = 40;
  int health = 3, maxHealth = 3, ammo = 6;
  float reloadTimer, reloadTime = 1.5f;
  
  public Player()
  {
    
  }
  
  public void update()
  {
    if (reloading)
    {       
      reloadTimer += delta;
      if (reloadTimer >= reloadTime)
      {
        ammo = 6;
        reloadTimer = 0;
        reloading = false;
        sfxPlayer.close();
        sfxPlayer = minim.loadFile("sounds/reload_2.wav", 512);
        sfxPlayer.play();
      }
    }
    shootTimer += delta;
    if (shootTimer > 0.1f)
    {
      shooting = false;
    }
    
    float dx = 0, dz = 0;
    if (w)
    {
      dx += sin(theta) * delta * speed;
      dz += cos(theta) * delta * speed;
    }
    if (a)
    {
      dx += sin(theta+PI/2) * delta * speed;
      dz += cos(theta+PI/2) * delta * speed;
    }
    if (s)
    {
      dx -= sin(theta) * delta * speed;
      dz -= cos(theta) * delta * speed;
    }
    if (d)
    {
      dx -= sin(theta+PI/2) * delta * speed;
      dz -= cos(theta+PI/2) * delta * speed;
    }
    if (!level[currentLevel].isLevelCollision(posX + dx, posZ, collisionWidth))
      posX += dx;
    if (!level[currentLevel].isLevelCollision(posX, posZ + dz, collisionWidth))
      posZ += dz;
    if ((dx + dz) != 0)
      posY = normalPosY + sin(millis()/(20*PI)*(speed/180));
    
    health -= level[currentLevel].getEnemyDamage(posX, posZ, collisionWidth, -1);
  }
  
  public void draw()
  {
    image(crosshair, 8*width/17, 8*height/17, width/17, height/17);
    if (!shooting)
      image(pistol, width/2, height/2.5f, width/2, 2*height/3);
    else
      image(pistolFire, width/2, height/2.5f, width/2, 2*height/3);
  }
  
  public void setCamera()
  {
    camera(posX, posY, posZ,
          posX+sin(theta), posY+phi, posZ+cos(theta),
          0, 1, 0);
  }
  
  public void resetPlayer()
  {
    placePlayer(level[currentLevel]);
    health = maxHealth;
    ammo = 6;
  }
  
  public void placePlayer(Level l)
  {
    posX = l.playerStartX * tileWidth;
    posZ = l.playerStartZ * tileWidth;
    theta = l.playerStartT;
    phi = 0;
  }
}
public PImage pistol;
public PImage pistolFire;
public PImage crosshair;
public PImage dinosaur;
public PImage dinosaurShot;
public PImage dinosaurDead;
public PImage monkey;
public PImage monkeyShot;
public PImage monkeyDead;
public PImage lizard;
public PImage lizardShot;
public PImage lizardDead;
public PImage prof;
public PImage man;
public PImage manShot;
public PImage manDead;
public PImage fish;
public PImage fishShot;
public PImage butterfly;
public PImage deathScreen;
public PImage splashScreen;
public PImage introScreen;
public PImage endScreen;
public PImage jungleWall;
public PImage coralWall;
public PImage weedWall;
public PImage cityOne;
public PImage cityTwo;
public PImage cityThree;
public PImage cityFour;
public PFont hudFont;
public PImage heart;
public PImage emptyHeart;
public PImage bullet;
public PImage emptyBullet;
public PImage cliffWall;
public PImage ruinWall;
public PImage labWall;
public PImage lab;
public PImage labWallOne;
public PImage labWallTwo;
public PImage labWallThree;
public PImage labLevelOne;
public PImage labLevelTwo;
public PImage labLevelThree;
public PImage labLevelFour;
public PImage labLevelFive;
public PImage portal;
public PImage portalTwo;

public void loadResources()
{
  pistol = loadImage("weapons/pistol.png");
  pistolFire = loadImage("weapons/pistol_fire.png");
  crosshair = loadImage("weapons/crosshair.png");
  dinosaur = loadImage("enemies/dinosaur.png");
  dinosaurShot = loadImage("enemies/dinosaur_shot.png");
  dinosaurDead = loadImage("enemies/dinosaur_dead.png");
  monkey = loadImage("enemies/monkey.png");
  monkeyShot = loadImage("enemies/monkey_shot.png");
  monkeyDead = loadImage("enemies/monkey_dead.png");
  lizard = loadImage("enemies/lizard.png");
  lizardShot = loadImage("enemies/lizard_shot.png");
  lizardDead = loadImage("enemies/lizard_dead.png");
  prof = loadImage("enemies/prof.png");
  man = loadImage("enemies/man.png");
  manShot = loadImage("enemies/man_shot.png");
  manDead = loadImage("enemies/man_dead.png");
  fish = loadImage("enemies/fish.png");
  fishShot = loadImage("enemies/fish_shot.png");
  hudFont = loadFont("misc/BellMT-26.vlw");
  butterfly = loadImage("misc/butterfly.png");
  deathScreen = loadImage("misc/death_screen.png");
  splashScreen = loadImage("misc/splash_screen.png");
  introScreen = loadImage("misc/intro_comic.png");
  emptyHeart = loadImage("misc/empty_heart.png");
  heart = loadImage("misc/heart.png");
  emptyBullet = loadImage("misc/empty_bullet.png");
  bullet = loadImage("misc/bullet.png");
  endScreen = loadImage("misc/end_screen.png");
  jungleWall = loadImage("level/jungly.png");
  coralWall = loadImage("level/coral.png");
  cliffWall = loadImage("level/cliff.png");
  ruinWall = loadImage("level/ruin_wall.png");
  weedWall = loadImage("level/sea_weed.png");
  cityOne = loadImage("level/city1.png");
  cityTwo = loadImage("level/city2.png");
  cityThree = loadImage("level/city3.png");
  cityFour = loadImage("level/city4.png");
  labWallOne = loadImage("level/lab_wall_1.png");
  labWallTwo = loadImage("level/lab_wall_2.png");
  labWallThree = loadImage("level/lab_wall_3.png");
  labWall = loadImage("level/lab_wall.png");
  lab = loadImage("level/lab.png");
  portal = loadImage("level/portal.png");
  portalTwo = loadImage("level/portal2.png");
  labLevelOne = loadImage("level/lab_level_1.png");
  labLevelTwo = loadImage("level/lab_level_2.png");
  labLevelThree = loadImage("level/lab_level_3.png");
  labLevelFour = loadImage("level/lab_level_4.png");
  labLevelFive = loadImage("level/lab_level_5.png");
}

public void drawWall(PImage tex, float x, float y, float z)
{
  pushMatrix();
  translate(x-tileWidth/2, y-boxHeight/2, z-tileWidth/2);
  if (tex == portal || tex == portalTwo)
  {
    translate(-5, 0, -5);
    tileWidth += 10;
  }
  textureMode(NORMALIZED);
  beginShape(QUADS);
  texture(tex);
  vertex(0, 0, 0, 1, 0);
  vertex(tileWidth, 0, 0, 0, 0);
  vertex(tileWidth, boxHeight, 0, 0, 1);
  vertex(0, boxHeight, 0, 1, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(0, 0, 0, 0, 0);
  vertex(0, 0, tileWidth, 1, 0);
  vertex(0, boxHeight, tileWidth, 1, 1);
  vertex(0, boxHeight, 0, 0, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(tileWidth, 0, tileWidth, 1, 0);
  vertex(0, 0, tileWidth, 0, 0);
  vertex(0, boxHeight, tileWidth, 0, 1);
  vertex(tileWidth, boxHeight, tileWidth, 1, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(tileWidth, 0, 0, 1, 0);
  vertex(tileWidth, 0, tileWidth, 0, 0);
  vertex(tileWidth, boxHeight, tileWidth, 0, 1);
  vertex(tileWidth, boxHeight, 0, 1, 1);
  endShape();
  if (tex == portal || tex == portalTwo)
  {
    tileWidth -= 10;
  }
  popMatrix();
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--hide-stop", "natural_selection" });
  }
}
