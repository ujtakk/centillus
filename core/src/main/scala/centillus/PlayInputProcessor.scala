package centillus

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Input.Keys

class PlayInputProcessor(val game: Centillus, val screen: PlayScreen)
extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    val (pos, speed): (Float, Float) =
      if (screen.isLeftSide()) keycode match {
        case Keys.SPACE => game.playNote(0)
        case Keys.A => return false
        case Keys.S => game.playNote(1)
        case Keys.D => game.playNote(2)
        case Keys.F => game.playNote(3)
        case Keys.J => game.playNote(4)
        case Keys.K => game.playNote(5)
        case Keys.L => game.playNote(6)
        case Keys.SEMICOLON => game.playNote(7)
        case Keys.ENTER => return false
        case _ => return false
      }
      else keycode match {
        case Keys.SPACE => game.playNote(0)
        case Keys.A => game.playNote(1)
        case Keys.S => game.playNote(2)
        case Keys.D => game.playNote(3)
        case Keys.F => game.playNote(4)
        case Keys.J => game.playNote(5)
        case Keys.K => game.playNote(6)
        case Keys.L => game.playNote(7)
        case Keys.SEMICOLON => return false
        case Keys.ENTER => return false
        case _ => return false
      }

    game.judgeInput(pos, speed)

    return true
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
