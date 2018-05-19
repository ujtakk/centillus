package centillus

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Input.Keys

class PlayInputProcessor(val game: Centillus, val screen: PlayScreen)
extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case Keys.A =>
      case Keys.S =>
      case Keys.D =>
      case Keys.F =>
      case Keys.SPACE =>
      case Keys.J =>
      case Keys.K =>
      case Keys.L =>
      case Keys.SEMICOLON =>
      case Keys.ENTER =>
        screen.playStart()
      case _ =>
    }
    // val result = game.judgeInput(keycode)
    // screen.setJudge(result)
    return false
  }

  override def keyUp(keycode: Int): Boolean = {
    false
  }

  override def keyTyped(character: Char): Boolean = {
    false
  }

  override def touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = {
    false
  }

  override def touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean = {
    false
  }

  override def touchDragged(x: Int, y: Int, pointer: Int): Boolean = {
    false
  }

  override def mouseMoved(x: Int, y: Int): Boolean = {
    false
  }

  override def scrolled(amount: Int): Boolean = {
    false
  }
}
