import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PImage;

public class Sketch extends PApplet {

  // Images
  PImage imgWizardMonkey;
  PImage imgProjectile;
  PImage imgBackground;
  PImage imgBlueProjectile;
  PImage imgRedProjectile;

  // Initilizing variables/positions
  long startTime = System.currentTimeMillis();
  boolean boolMouse = false;
  boolean boolBlueAmmo = false;

  float fltMonkeyX = 100;
  float fltMonkeyY = 100;
  boolean boolUp = false;
  boolean boolRight = false;
  boolean boolDown = false;
  boolean boolLeft = false;

  // Projectiles to be drawn are added to a list
  Deque<Float> projectileX = new LinkedList<>();
  Deque<Float> projectileY = new LinkedList<>();

  Deque<Float> angles = new LinkedList<>();
  Deque<Integer> timeOfSpawn = new LinkedList<>();

  Deque<Float> speedX = new LinkedList<>();
  Deque<Float> speedY = new LinkedList<>();

  // Time
  // Timer mouseCheck = new Timer();
  int lastTime = 0;
  Timer projectileCheck = new Timer();

  public void settings() {
    // put your size call here
    size(1000, 700);

  }

  /**
   * Called once at the beginning of execution. Add initial set up
   * values here i.e background, stroke, fill etc.
   */
  public void setup() {
    imgBackground = loadImage("MonkeyMeadow.png");
    imgProjectile = loadImage("Projectile.png");
    imgWizardMonkey = loadImage("WizardMonkey.png");
    imgRedProjectile = loadImage("Projectile.png");
    imgBlueProjectile = loadImage("BlueFire.png");
  }

  /**
   * Description: main loop to run the program
   * Note: some methods are not called as they are called automatically by the
   * program, such as keyPressed(), keyReleased() etc.
   * 
   */
  public void draw() {
    image(imgBackground, 0, 0);

    movement();
    image(imgWizardMonkey, fltMonkeyX - 40, fltMonkeyY - 40);

    if (mousePressed) {
      ifMousePressed();
    }
    drawProjectile();
  }

  /**
   * Description: draws all projectiles currently in the list, 
   * also removes projectiles which has been exsisting for too long
   * 
   * No param
   * No return 
   * 
   * @author: Gordon Z
   */

  public void drawProjectile() {

    // remove projectiles which are spawned for too long
    Iterator<Integer> iteratorSpawn = timeOfSpawn.iterator();
    while (iteratorSpawn.hasNext()) {
      long spawnTime = iteratorSpawn.next();

      // Check if the element should be removed
      if (System.currentTimeMillis() - startTime - spawnTime > 2500) {
        iteratorSpawn.remove(); // Remove using the iterator's remove method
        // Also remove corresponding elements from other Deques

        projectileX.pop();
        projectileY.pop();
        angles.pop();
        speedX.pop();
        speedY.pop();
      }
    }

    // draw rest of projectiles
    Iterator<Float> iteratorX = projectileX.iterator();
    Iterator<Float> iteratorY = projectileY.iterator();
    Iterator<Float> iteratorAngles = angles.iterator();
    Iterator<Float> iteratorSpeedX = speedX.iterator();
    Iterator<Float> iteratorSpeedY = speedY.iterator();
    iteratorSpawn = timeOfSpawn.iterator();

    while (iteratorX.hasNext()) {
      float x = iteratorX.next();
      float y = iteratorY.next();
      float angle = iteratorAngles.next();
      float speedXValue = iteratorSpeedX.next();
      float speedYValue = iteratorSpeedY.next();

      int temp = (int) (System.currentTimeMillis() - startTime - iteratorSpawn.next());

      pushMatrix();
      translate(x + (temp * speedXValue / 5), y + (temp * speedYValue / 5));
      rotate(angle - PI / 2);
      translate(-12, -15);
      image(imgProjectile, 0, 0);
      popMatrix();

    }
  }

  /**
   * Description: detects movement from keys to move the wizard monkey.
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void movement() {

    if (boolUp)
      fltMonkeyY -= 3;
    fltMonkeyY = Math.max(0, fltMonkeyY);

    if (boolLeft)
      fltMonkeyX -= 3;
    fltMonkeyX = Math.max(0, fltMonkeyX);

    if (boolDown)
      fltMonkeyY += 3;
    fltMonkeyY = Math.min(height, fltMonkeyY);

    if (boolRight)
      fltMonkeyX += 3;
    fltMonkeyX = Math.min(width, fltMonkeyX);
  }

  /**
   * Description: After each mouse clicked, it will check if 70 milliseconds have
   * passed. If so, it will push information into the queue for projectile to be
   * drawn later on.
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void ifMousePressed() {
    // check if 70 milliseconds has passed so there aren't too much projectiles.
    if (System.currentTimeMillis() - startTime - lastTime > 70) {

      // calculations of distance from mouse to center of wizard monkey.
      float fltCurrentHorizontal = (mouseX - (fltMonkeyX));
      float fltCurrentVertical = (mouseY - (fltMonkeyY));
      float fltHyp = (float) Math.sqrt(fltCurrentHorizontal * fltCurrentHorizontal
          + fltCurrentVertical * fltCurrentVertical);

      // Adding details for the projectile to be drawn later
      projectileX.addLast(fltMonkeyX);
      projectileY.addLast(fltMonkeyY);

      speedX.addLast(fltCurrentHorizontal / fltHyp);
      speedY.addLast(fltCurrentVertical / fltHyp);

      /*
       * given the triangle formed by the position of wizardMonkey,
       * mouse position, and the line y = WizardMonkeyX, calculate the angle.
       * since arcsin only returns angles of [-pi/2, pi/2], additional modification
       * is required to account for full 2pi rotation.
       */
      float fltAng = asin(fltCurrentVertical / fltHyp);
      if (mouseX < fltMonkeyX + 40) {
        // if the mouse is to the left of center of wizard, modify the angle
        fltAng = (float) (3.14159265358979323846 - fltAng);
      }
      angles.addLast(fltAng);

      // push the time of new projectile spawn and update the last time a projectile
      // has spawned
      timeOfSpawn.addLast((int) (System.currentTimeMillis() - startTime));
      lastTime = (int) (System.currentTimeMillis() - startTime);
    }
  }

  /**
   * Description: Called automatically. If the right button is clicked, character will teleport
   * to current mouse location. Projectiles will change to blue
   * 
   * No param
   * No return 
   * 
   * @author: Gordon Z
   */
  public void mousePressed() {
    
    if (mouseButton == RIGHT) {
      fltMonkeyX = mouseX;
    fltMonkeyY = mouseY;
      imgProjectile = imgBlueProjectile;
    }
  }

  /**
   * Description: when mouse is released, switch projectile back to red. 
   * 
   * No param 
   * No return 
   * 
   * @author: Gordon Z
   */
  public void mouseReleased() {
    imgProjectile = imgRedProjectile;
  }

  /**
   * Description: when keys are pressed, respective keys will have their associated movement boolean changed to true
   * 
   * No param 
   * No return 
   * 
   * @author: Gordon Z
   */
  public void keyPressed() {
    if (key == 'w'||keyCode== UP) {
      boolUp = true;
    }
    if (key == 'a'||keyCode==LEFT) {
      boolLeft = true;
    }
    if (key == 's'||keyCode==DOWN) {
      boolDown = true;
    }
    if (key == 'd'||keyCode==RIGHT) {
      boolRight = true;
    }
  }

  /**
   * Description: when key is released, change movement boolean to false.  
   * 
   * No param 
   * No return 
   * 
   * @author: Gordon Z
   */
  public void keyReleased() {
    if (key == 'w'||keyCode==UP) {
      boolUp = false;
    }
    if (key == 'a'||keyCode==LEFT) {
      boolLeft = false;
    }
    if (key == 's'||keyCode==DOWN) {
      boolDown = false;
    }
    if (key == 'd'||keyCode==RIGHT) {
      boolRight = false;
    }
  }

}
