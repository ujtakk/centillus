package centillus

import com.badlogic.gdx.{ Screen
                        , Gdx
                        }
import com.badlogic.gdx.graphics.{ Texture
                                 , Pixmap
                                 }
import com.badlogic.gdx.utils.{ TimeUtils
                              }

class PlayScreen(final val game: Centillus) extends Screen {
  lazy val input = new PlayInputProcessor(game, this)
  Gdx.input.setInputProcessor(input)

  val width: Float = Gdx.graphics.getWidth().toFloat
  val height: Float = Gdx.graphics.getHeight().toFloat

  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  val whiteW: Float = 44
  val whiteH: Float = 10
  val blackW: Float = 36
  val blackH: Float = 10
  val redW: Float = 80
  val redH: Float = 10
  val laneNum = game.getLaneNum()
  val laneLen: Float = 512
  val judgeW: Float = 3*blackW + 4*whiteW + redW
  val judgeH: Float = 1
  // val leftSide = true
  val leftSide = false
  def isLeftSide() = leftSide

  val fps = game.getFPS()
  val whole = game.getWhole()
  val speed = game.getSpeed()
  def bpm = game.fetchBPM()

  var barLinePos: Float = game.barTime.toFloat / game.laneTime.toFloat

  override def render(x: Float): Unit = {
    if (game.barStart()) {
      barLinePos = game.barTime.toFloat / game.laneTime.toFloat
      game.updateBar()
      if (game.isPlayingEnd()) {
        game.setScreen(new ResultScreen(game))
      }
    }

    initScreen()

    drawNotes()
    // drawAnime()
    drawJudge()
    // drawGauge()
    drawScore()

    // if (barCount == 6)
    //   Gdx.app.exit()
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
    // game.makeRect("808080", 0, 0, width, height)
    game.makeRect("000000", 0, 0, width, height)
  }

  def drawLanes() = drawLanes_cent()
  def drawLanes_iidx() = {
    var laneX: Float = 44
    val laneY: Float = 44
    if (leftSide) {
      game.makeRect("000000", laneX, laneY, redW, laneLen, "ffffff")
      laneX += redW
    }
    for (i <- 1 to laneNum/2) {
      game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
      laneX += whiteW
      game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
      laneX += blackW
    }
    game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
    laneX += whiteW
    if (!leftSide) {
      game.makeRect("000000", laneX, laneY, redW, laneLen, "ffffff")
      laneX += redW
    }
    game.makeLine("00ff00", laneX, laneY, laneX-judgeW, laneY, judgeH)
  }

  def drawLanes_cent() = {
    var laneX: Float = 44
    val laneY: Float = 44
    game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
    laneX += whiteW
    game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
    laneX += blackW
    game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
    laneX += whiteW
    if (leftSide) {
      game.makeRect("000000", laneX, laneY, redW, laneLen, "ffffff")
      laneX += redW
      game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
      laneX += blackW
    }
    else {
      game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
      laneX += blackW
      game.makeRect("000000", laneX, laneY, redW, laneLen, "ffffff")
      laneX += redW
    }
    game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
    laneX += whiteW
    game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
    laneX += blackW
    game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
    laneX += whiteW
    game.makeLine("00ff00", laneX, laneY, laneX-judgeW, laneY, judgeH)
  }

  def drawNotes() = {
    drawBarLine()
    for (lane <- 0 to laneNum) {
      val noteChan = game.notes.get(lane)
      val iter = noteChan.iterator()
      while (iter.hasNext()) {
        val note = iter.next()
        val notePos = note.getPos()
        if (0.0f < notePos && notePos < 1.0f)
          drawNote(lane, notePos)
      }
    }
    game.updateNotes()
  }

  def drawBarLine() = {
    if (0.0f <= barLinePos && barLinePos < 1.0f) {
      val barPos = laneLen * barLinePos
      game.makeLine("ffffff", laneX, laneY+barPos, laneX+judgeW, laneY+barPos, 1.0f)
    }

    barLinePos -= game.getSpeed()
    // if (barLinePos < 0.0f)
    //   barLinePos = barTime.toFloat / laneTime.toFloat - speed
  }

  val laneX: Float = 44
  val laneY: Float = 44
  def drawNote(noteLane: Int, notePos: Float) = drawNote_cent(noteLane, notePos)
  private def drawNote_iidx(noteLane: Int, notePos: Float) =
    noteLane match {
      case 0 => {
        val redPos = if (leftSide) laneX else laneX+judgeW-redW
        game.makeRect("ff0000", redPos, laneY+notePos*laneLen, redW, redH)
      }
      case 1 | 3 | 5 | 7 => {
        val whitePos = if (leftSide)
                         laneX+redW+((noteLane-1)/2)*(whiteW+blackW)
                       else
                         laneX+((noteLane-1)/2)*(whiteW+blackW)
        game.makeRect("ffffff", whitePos, laneY+notePos*laneLen, whiteW, whiteH)
      }
      case 2 | 4 | 6 => {
        val blackPos =
          if (leftSide)
            laneX+redW+((noteLane-1)/2)*(whiteW+blackW)+whiteW
          else
            laneX+((noteLane-1)/2)*(whiteW+blackW)+whiteW
        game.makeRect("0000ff", blackPos, laneY+notePos*laneLen, blackW, blackH)
      }
      case _ =>
    }
  private def drawNote_cent(noteLane: Int, notePos: Float) =
    noteLane match {
      case 0 => {
        val redPos = if (leftSide) laneX + 2*whiteW + blackW
                     else laneX + 2*whiteW + 2*blackW
        game.makeRect("ff0000", redPos, laneY+notePos*laneLen, redW, redH)
      }
      case 1 | 3 | 5 | 7 => {
        val whitePos = if (noteLane == 1 || noteLane == 3)
                         laneX+((noteLane-1)/2)*(whiteW+blackW)
                       else
                         laneX+redW+((noteLane-1)/2)*(whiteW+blackW)
        game.makeRect("ffffff", whitePos, laneY+notePos*laneLen, whiteW, whiteH)
      }
      case 2 | 4 | 6 => {
        val blackPos = if (leftSide) {
                         if (noteLane == 4 || noteLane == 6)
                           laneX+redW+((noteLane-1)/2)*(whiteW+blackW)+whiteW
                         else
                           laneX+((noteLane-1)/2)*(whiteW+blackW)+whiteW
                       }
                       else {
                         if (noteLane == 2 || noteLane == 4)
                           laneX+((noteLane-1)/2)*(whiteW+blackW)+whiteW
                         else
                           laneX+redW+((noteLane-1)/2)*(whiteW+blackW)+whiteW
                       }
        game.makeRect("0000ff", blackPos, laneY+notePos*laneLen, blackW, blackH)
      }
      case _ =>
    }

  val animeX: Float = 44 + 36 + judgeW
  val animeY: Float = 44
  val animeW: Float = 512
  val animeH: Float = 512
  val animeChan: Int = 4
  var cacheImage: Texture = new Texture(animeW.toInt, animeH.toInt,
                                        Pixmap.Format.RGB888)
  def drawAnime() = {
    // if (game.barData.contains(animeChan)) {
    //   val currTime = TimeUtils.timeSinceNanos(baseTime)
    //   val animeData = game.barData(animeChan)(0)
    //   val (targetTime, targetBGA) = animeData.head
    //   if (targetTime < currTime) {
    //     game.barData(animeChan)(0) = animeData.tail
    //     if (targetBGA != "00") {
    //       cacheImage = game.fetchImage(targetBGA)
    //     }
    //   }
    // }
    game.drawImage(cacheImage, animeX, animeY, animeW, animeH)
  }

  var currentJudge: Judgement = Poor
  def setJudge(judge: Judgement) = { currentJudge = judge }

  def drawJudge(): Unit = {
    val combo = game.getCombo()
    val judge = game.getJudge()
    val judgeStr = judge match {
      case Perfect => "PERFECT"
      case Great => "GREAT"
      case Good => "GOOD"
      case Bad => "BAD"
      case Poor => "POOR"
      case Miss => "POOR"
      case Space => return
    }

    game.makeFont("ffffff", f"$judgeStr%-8s $combo%4d", 80.0f, 160.0f)
  }

  def drawGauge() = {
  }

  def drawScore() = {
    val score = game.calcScore()
    val maxCombo = game.getMaxCombo()
    val scoreStr = f"""
      SCORE:     $score%6d
      MAX COMBO: $maxCombo%6d
    """
    game.makeFont("ffffff", scoreStr, 400.0f, 400.0f)
  }
}
