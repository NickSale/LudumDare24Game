public void drawWall(PImage tex, float x, float y, float z)
{
  pushMatrix();
  translate(x-tileWidth/2, y-boxHeight/2, z-tileWidth/2);
  if (tex == portal || tex == portalTwo)
  {
    translate(-5, 0, -5);
    tileWidth += 10;
  }
  textureMode(NORMALIZED);
  beginShape(QUADS);
  texture(tex);
  vertex(0, 0, 0, 1, 0);
  vertex(tileWidth, 0, 0, 0, 0);
  vertex(tileWidth, boxHeight, 0, 0, 1);
  vertex(0, boxHeight, 0, 1, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(0, 0, 0, 0, 0);
  vertex(0, 0, tileWidth, 1, 0);
  vertex(0, boxHeight, tileWidth, 1, 1);
  vertex(0, boxHeight, 0, 0, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(tileWidth, 0, tileWidth, 1, 0);
  vertex(0, 0, tileWidth, 0, 0);
  vertex(0, boxHeight, tileWidth, 0, 1);
  vertex(tileWidth, boxHeight, tileWidth, 1, 1);
  endShape();
  beginShape(QUADS);
  texture(tex);
  vertex(tileWidth, 0, 0, 1, 0);
  vertex(tileWidth, 0, tileWidth, 0, 0);
  vertex(tileWidth, boxHeight, tileWidth, 0, 1);
  vertex(tileWidth, boxHeight, 0, 1, 1);
  endShape();
  if (tex == portal || tex == portalTwo)
  {
    tileWidth -= 10;
  }
  popMatrix();
}
