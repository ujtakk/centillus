package centillus

import com.badlogic.gdx.{Screen, Gdx}

class ResultScreen(final val game: Centillus) extends Screen {
  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  override def render(x: Float): Unit = {
    Gdx.app.exit()
  }

  override def resize(x: Int, y: Int): Unit = {
  }

  override def resume(): Unit = {
  }

  override def show(): Unit = {
  }
}
