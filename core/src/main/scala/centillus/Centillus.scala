package centillus

import com.badlogic.gdx.{
  Game,
  Gdx
}
import com.badlogic.gdx.graphics.{
  GL20,
  Color,
  Texture
}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Centillus(val bmsPath: String) extends Game {
  lazy val batch = new SpriteBatch
  lazy val shape = new ShapeRenderer
  lazy val model = new Model

  override def create(): Unit = {
    model.readBMS(bmsPath)
    model.loadSounds()
    model.loadImages()

    this.setScreen(new LoadScreen(this))
  }

  override def render(): Unit = super.render()

  override def dispose(): Unit = {}

  def isLoadingEnd() = model.updateManager()
  def fetchBPM(barCount: Int) = model.getBPM(barCount)
  def fetchData(barCount: Int) = model.getData(barCount)
  def fetchSound(number: String) = model.getSound(number)
  def fetchImage(number: String): Texture = model.getImage(number)
  def fetchStageFile() = model.getStageFile()

  def judgeInput(keycode: Int): Judgement = {
    Perfect
  }

  def initScreen() = {
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
  }

  def makeLine(color: String, x1: Float, y1: Float, x2: Float, y2: Float,
               width: Float) = {
    shape.begin(ShapeType.Filled)
    shape.setColor(Color.valueOf(color))
    shape.rectLine(x1, y1, x2, y2, width)
    shape.end()
  }

  def makeRect(color: String, x: Float, y: Float, w: Float, h: Float) = {
    shape.begin(ShapeType.Filled)
    shape.setColor(Color.valueOf(color))
    shape.rect(x, y, w, h)
    shape.setColor(Color.valueOf("ffffff"))
    shape.rectLine(x,   y,   x,   y+h, 1)
    shape.rectLine(x,   y+h, x+w, y+h, 1)
    shape.rectLine(x+w, y+h, x+w, y,   1)
    shape.rectLine(x+w, y,   x,   y,   1)
    shape.end()
  }

  def drawImage(image: Texture, x: Float, y: Float, w: Float, h: Float) = {
    batch.begin()
    batch.draw(image, x, y, w, h)
    batch.end()
  }
}
