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
  float reloadTimer, reloadTime = 1.5;
  
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
    if (shootTimer > 0.1)
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
      image(pistol, width/2, height/2.5, width/2, 2*height/3);
    else
      image(pistolFire, width/2, height/2.5, width/2, 2*height/3);
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
