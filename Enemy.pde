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
    randBob = random(0.5, 2);
  }

  public void update()
  {
    shotTimer += delta;
    if (shotTimer > 0.1)
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

