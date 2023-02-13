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
  
  void onDead()
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
