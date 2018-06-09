package centillus

import com.badlogic.gdx.graphics.Texture

class Frame( val image: Texture
           , var speed: Float
           , val offset: Float
           ) {
  var pos: Float = offset
  def getPos() = pos
  def getSpeed() = speed

  var hitFlag: Boolean = false
  def hit() = {
    hitFlag = true
    speed = 0.0f
  }
  def isHit() = hitFlag

  def changeSpeed(nextSpeed: Float) = { speed = nextSpeed }

  def update() = {
    pos -= speed
    // if (pos < 0.0f)
    //   pos = 0.0f
  }

  def show() = image
}

