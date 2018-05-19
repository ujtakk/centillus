package centillus

import com.badlogic.gdx.{
  Screen,
  Gdx
}

class PlayScreen(final val game: Centillus) extends Screen {
  lazy val input = new PlayInputProcessor(game, this)
  Gdx.input.setInputProcessor(input)

  val width: Float = Gdx.graphics.getWidth()
  val height: Float = Gdx.graphics.getHeight()

  // private var bpm = game.fetchBPM()

  private var frameCount: Int = 0
  private var barCount: Int = 0

  var playStarted: Boolean = false
  var playFinished: Boolean = false
  def playStart() = {
    playStarted = true
  }
  def playFinish() = {
    playFinished = true
  }

  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  override def render(x: Float): Unit = {
    initScreen()
    if (!playStarted)
      return

    // val notes = game.fetchNotes()
    // val sounds = game.fetchSounds()
    // val image = game.fetchImage()

    // drawNotes(notes)
    drawAnime()
    // drawGauge()

    // if (playFinished)
    //   game.setScreen(new ResultScreen(game))
    // else
    //   game.nextFrame()
  }

  def nextFrame() = {
    frameCount += 1
    // if (procTime > barTime)
    //   nextBar()
  }
  def nextBar() = {
    barCount += 1
    // drawBar(procTime-barTime)
  }

  override def resize(x: Int, y: Int): Unit = {
  }

  override def resume(): Unit = {
  }

  override def show(): Unit = {
  }

  def initScreen() = {
    game.initScreen()
    drawBackground()
    drawLanes()
  }

  def drawBackground() = {
    game.makeRect("808080", 0, 0, width, height)
  }

  val whiteW: Float = 44
  val whiteH: Float = 10
  val blackW: Float = 36
  val blackH: Float = 10
  val redW: Float = 80
  val redH: Float = 10
  val laneNum = 7
  val laneLen: Float = 512
  val judgeW: Float = 3*blackW + 4*whiteW + redW
  val judgeH: Float = 10
  def drawLanes() = {
    var laneX: Float = 44
    val laneY: Float = 44
    game.makeRect("000000", laneX, laneY, redW, laneLen)
    laneX += redW
    for (i <- 1 to laneNum/2) {
      game.makeRect("181818", laneX, laneY, whiteW, laneLen)
      laneX += whiteW
      game.makeRect("000000", laneX, laneY, blackW, laneLen)
      laneX += blackW
    }
    game.makeRect("181818", laneX, laneY, whiteW, laneLen)
    laneX += whiteW
    game.makeLine("00ff00", laneX, laneY, laneX-judgeW, laneY, judgeH)
  }

  def drawNotes() = {
  }

  val animeX: Float = 44 + 36 + judgeW
  val animeY: Float = 44
  val animeW: Float = 512
  val animeH: Float = 512
  // def drawAnime(image: Texture) = {
  def drawAnime() = {
    game.makeRect("000000", animeX, animeY, animeW, animeH)
  }

  var currentJudge: Judgement = Poor
  def setJudge(judge: Judgement) = { currentJudge = judge }

  def drawJudge(judge: Judgement) = judge match {
    case Perfect =>
    case Great =>
    case Good =>
    case Bad =>
    case Poor =>
  }

  def drawGauge() = {
  }

  def drawBar() = {
  }
}
