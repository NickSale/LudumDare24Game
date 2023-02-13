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
  
  void onDead()
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
