package centillus

import com.badlogic.gdx.{Screen, Gdx}
import com.badlogic.gdx.Input.Keys

class ResultScreen(final val game: Centillus) extends Screen {
  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  override def render(x: Float): Unit = {
    game.initScreen()

    val totalNotes = game.fetchTotalNotes()
    val maxCombo = game.getMaxCombo()
    val comboBreak = game.getComboBreak()
    val stageScore = game.calcScore()
    val result = game.getResult()
    val resultStr = f"""
    STAGE SCORE:  $stageScore%8d
    TOTAL NOTES:  $totalNotes%8d
    MAX COMBO:    $maxCombo%8d

    PERFECT:      ${result(0)}%8d
    GREAT:        ${result(1)}%8d
    GOOD:         ${result(2)}%8d
    BAD:          ${result(3)}%8d
    POOR:         ${result(4)}%8d
    COMBO BREAK:  $comboBreak%8d
    """
    game.makeFont("ffffff", resultStr, 200.0f, 500.0f)

    val playRank = game.calcRank()
    game.makeFont("ffffff", playRank, 400.0f, 550.0f)

    if (Gdx.input.isKeyPressed(Keys.ENTER)) {
      game.barReset()
      game.setScreen(new PlayScreen(game))
    }
  }

  override def resize(x: Int, y: Int): Unit = {
  }

  override def resume(): Unit = {
  }

  override def show(): Unit = {
  }
}
