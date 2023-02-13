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
  
  void onDead()
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
