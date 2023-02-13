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
  
  void onDead()
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
