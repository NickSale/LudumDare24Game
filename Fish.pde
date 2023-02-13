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
  
  void onDead()
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
