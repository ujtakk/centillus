package centillus

import com.badlogic.gdx.graphics.GL20
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
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    val result = game.getResult()
    val resultStr = f"""
    PERFECT: ${result(0)}%5d
    GREAT:   ${result(1)}%5d
    GOOD:    ${result(2)}%5d
    BAD:     ${result(3)}%5d
    POOR:    ${result(4)}%5d
    """
    game.makeFont("ffffff", resultStr, 200.0f, 500.0f)

    if (Gdx.input.isKeyPressed(Keys.ENTER))
      Gdx.app.exit()
  }

  override def resize(x: Int, y: Int): Unit = {
  }

  override def resume(): Unit = {
  }

  override def show(): Unit = {
  }
}
