import java.awt.AWTException;
import java.awt.Robot;
import ddf.minim.*;

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
float cloudTimer = -0.5, cloudTime = 1;

void setup()
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

void stop()
{
  gunPlayer.close();
  sfxPlayer.close();
  musicPlayer.close();
  minim.stop();  
  super.stop();
}

void update()
{
  updateDeltaFrames();

  if (gameState == 1)
  {
    comicTimer += delta;
    float targetX = 0;
    float targetY = 0;
    if (comicTimer > 2.5 && comicFrame != 7)
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

void draw()
{
  update();

  noCursor();

  if (gameState == 2)
  {
    //3d
    hint(ENABLE_DEPTH_TEST);
    noStroke();
    lights();
    directionalLight(230, 230, 190, -2, 0.5, -2);
    background(180, 180, 200);  
    player.setCamera();
    level[currentLevel].draw();
    if (butterflyCloud)
    {
      cloudTimer += delta;
      if (cloudTimer >= cloudTime)
      {
        cloudTimer = -0.5;
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


void updateDeltaFrames()
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

void resetLevel()
{
  level[currentLevel] = getLevel(currentLevel);
}
void resetEverything()
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

