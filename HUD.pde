public void drawHUD()
{
  fill(40, 40, 220);
  rect(0, 0, width, height/8);
  textMode(SCREEN);
  fill(0, 0, 0);
  textFont(hudFont);
  text("FPS: " + fps, width/1.5, height/22);
  text("Nickolai51", 11.5*width/14, height/22);
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
      image(heart, width/3.8, height/15, width/20, height/22);
    else
      image(emptyHeart, width/3.8, height/15, width/20, height/22);
  }
  else
  {
    image(emptyHeart, width/5, height/16, width/20, height/20);
    image(emptyHeart, width/3.8, height/16, width/20, height/20);
  }
  text("Enemy Health: ", width/2.3, height/10);
  fill(255, 0, 0);
  if (paused)
    text("PAUSED", width/2.4, height/6);
  rect(4*width/11, height/55, (player.reloadTimer/player.reloadTime)*(width/4), height/28);
  if (lastEnemy != -1)
    rect(7.5*width/11, height/14, ((float)level[currentLevel].enemies.get(lastEnemy).health
      /(float)level[currentLevel].enemies.get(lastEnemy).maxHealth)*(width/4), height/28);
  noFill(); stroke(0, 0, 0);
  rect(7.45*width/11, height/14.1, (width/3.95), height/28.1);
  rect(4*width/11, height/55, (width/3.95), height/28.1);
}
