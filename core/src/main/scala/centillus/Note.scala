package centillus

import com.badlogic.gdx.audio.Sound

class Note( val lane: Int
          , val sample: Sound
          , var speed: Float
          , val offset: Float
          ) {
  var pos: Float = offset
  def getPos() = pos
  def getLane() = lane
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

  def play(volume: Float = 0.5f) = {
    sample.play(volume)
  }
}
