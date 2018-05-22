package centillus

import com.badlogic.gdx.{Screen, Gdx}

class LoadScreen(final val game: Centillus) extends Screen {
  val image = game.fetchStageFile()
  var winWidth = Gdx.graphics.getWidth()
  var winHeight = Gdx.graphics.getHeight()
  val imgWidth = image.getWidth()
  val imgHeight = image.getHeight()

  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  // TODO: support assets loading
  override def render(x: Float): Unit = {
    game.initScreen()

    // val offsetX = (winWidth-imgWidth) / 2
    // val offsetY = (winHeight-imgHeight) / 2
    game.drawImage(image, 0, 0, winWidth, winHeight)

    if (game.isLoadingEnd()) {
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
