package centillus

import scala.collection.mutable.ArrayBuffer

import com.badlogic.gdx.{
  Game,
  Gdx
}
import com.badlogic.gdx.graphics.{
  GL20,
  Color,
  Texture
}
import com.badlogic.gdx.graphics.g2d.{ SpriteBatch
                                     , BitmapFont
                                     }
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.ObjectMap

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

  override def dispose(): Unit = {
    batch.dispose()
    shape.dispose()
    model.dispose()
  }

  def isLoadingEnd() = model.updateManager()
  def isPlayingEnd(bar: Int) = bar > model.getMaxbar() + 1

  def canFetchData(barCount: Int) = model.hasData(barCount)
  def fetchData(barCount: Int) = model.getData(barCount)
  def fetchBPM(barCount: Int) = model.getBPM(barCount)
  def fetchSound(number: String) = model.getSound(number)
  def fetchImage(number: String): Texture = model.getImage(number)
  def fetchStageFile() = model.getStageFile()

  var combo: Int = 0
  var judge: Judgement = Space
  var result = ArrayBuffer(0, 0, 0, 0, 0)
  def judgeInput(pos: Float, speed: Float) = {
    judge = pos match {
      case p if - 2*speed < p && p <  2*speed => {result(0) += 1; Perfect}
      case p if - 4*speed < p && p <  4*speed => {result(1) += 1; Great}
      case p if - 8*speed < p && p <  8*speed => {result(2) += 1; Good}
      case p if -16*speed < p && p < 16*speed => {result(3) += 1; Bad}
      case p if -32*speed < p && p < 32*speed => {result(4) += 1; Poor}
      case _ => Space
    }

    combo = judge match {
      case Perfect | Great | Good => combo + 1
      case Space => combo
      case _ => 0
    }
  }
  def getCombo() = combo
  def getJudge() = judge
  def getResult() = result

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

  def makeRect(color: String, x: Float, y: Float, w: Float, h: Float,
               lineColor: String = "") = {
    shape.begin(ShapeType.Filled)
    shape.setColor(Color.valueOf(color))
    shape.rect(x, y, w, h)
    if (lineColor != "") {
      shape.setColor(Color.valueOf("ffffff"))
      shape.rectLine(x,   y,   x,   y+h, 1)
      shape.rectLine(x,   y+h, x+w, y+h, 1)
      shape.rectLine(x+w, y+h, x+w, y,   1)
      shape.rectLine(x+w, y,   x,   y,   1)
    }
    shape.end()
  }

  lazy val fontFile = Gdx.files.internal("fonts/UbuntuMono-Regular.ttf")
  lazy val fontGen = new FreeTypeFontGenerator(fontFile)
  lazy val param = new FreeTypeFontGenerator.FreeTypeFontParameter
  param.size = 48
  param.color = Color.valueOf("ffffff")
  lazy val font = fontGen.generateFont(param)
  def makeFont(color: String, str: String, x: Float, y: Float) = {
    batch.begin()
    font.draw(batch, str, x, y)
    batch.end()
  }

  def drawImage(image: Texture, x: Float, y: Float, w: Float, h: Float) = {
    batch.begin()
    batch.draw(image, x, y, w, h)
    batch.end()
  }

  val noteMap = new ObjectMap[Int, Note]
  // playNote)
  def playNote(lane: Int): (Float, Float) = {
    val note = noteMap.get(lane, null)
    if (note == null) {
      return (1.0f, -1.0f)
    }

    return note.play()
  }

  def setNote(lane: Int, note: Note) = noteMap.put(lane, note)
}
