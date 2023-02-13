final float xSensitivity = 0.3, ySensitivity = 0.2;

void keyPressed()
{
  if (key == 'w' || key == 'W')
    player.w = true;
  else if (key == 's' || key == 'S')
    player.s = true;
  else if (key == 'a' || key == 'A')
    player.a = true;
  else if (key == 'd' || key == 'D')
    player.d = true;
  else if ((key == 'p' || key == 'P') && gameState == 2)
    paused = !paused;
  else if ((key == 'r' || key == 'R') && gameState == 2 && player.ammo != 6 && !player.reloading)
  {
    player.reloading = true;
    sfxPlayer.close();
    sfxPlayer = minim.loadFile("sounds/reload_1.wav", 512);
    sfxPlayer.play();
  }
  if (key == ENTER || key == RETURN)
  {
    if (gameState == 0)
    {
      gameState = 1;
    }
    else if (gameState == 4)
    {
      gameState = 2;
      player.resetPlayer();
      resetLevel();
    }
    else if (gameState == 3)
    {
      gameState = 0;
      resetEverything();
      musicPlayer.close();
      musicPlayer = minim.loadFile("sounds/small/dinosaur.mp3", 2048);
      musicPlayer.loop();
    }
    else if (gameState == 1)
    {
      gameState = 2;
      butterflyCloud = true;
      sfxPlayer.close();
      sfxPlayer = minim.loadFile("sounds/flap.wav", 512);
      sfxPlayer.play();
      musicPlayer.close();
      musicPlayer = minim.loadFile("sounds/small/tick.mp3", 2048);
      musicPlayer.loop();
    }
  }
}

void keyReleased()
{
  if (key == 'w' || key == 'W')
    player.w = false;
  else if (key == 's' || key == 'S')
    player.s = false;
  else if (key == 'a' || key == 'A')
    player.a = false;
  else if (key == 'd' || key == 'D')
    player.d = false;
}

void mouseMoved()
{
  if (!paused && gameState == 2)
    handleMouseMove();
}
void mouseDragged()
{
  if (!paused && gameState == 2)
    handleMouseMove();
}

void handleMouseMove()
{
  if (mouseX > width/2)
    player.theta -= xSensitivity * delta;
  else if (mouseX < width/2)
    player.theta += xSensitivity * delta;
  if (mouseY > height/2 + 1  && player.phi < 1)
    player.phi += ySensitivity * delta;
  else if (mouseY < height/2 - 1 && player.phi > -0.5)
    player.phi -= ySensitivity * delta;
  robot.mouseMove(screen.width/2, screen.height/2);
}

void mousePressed()
{
  if (!player.shooting && gameState == 2 && player.ammo > 0 && !player.reloading)
  {
    player.shooting = true;
    player.ammo--;
    if (player.ammo == 0)
    {
      player.reloading = true;
      sfxPlayer.close();
      sfxPlayer = minim.loadFile("sounds/reload_1.wav", 512);
      sfxPlayer.play();
    }
    player.shootTimer = 0;
    level[currentLevel].updateShot();
    gunPlayer.rewind();
    gunPlayer.play();
  }
}

