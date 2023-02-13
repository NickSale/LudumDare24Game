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
