package centillus

import com.badlogic.gdx.{ Screen
                        , Gdx
                        }
import com.badlogic.gdx.graphics.{ Texture
                                 , Pixmap
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

  val whiteW: Float = 50
  val whiteH: Float = 10
  val blackW: Float = 40
  val blackH: Float = 10
  val redW: Float = 80
  val redH: Float = 10
  val laneNum = game.getLaneNum()
  val laneLen: Float = 580
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
    drawAnime()
    drawJudge()
    drawGauge()
    drawScore()
    drawMeter()

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

  val laneX: Float = 440
  val laneY: Float = 120
  def drawLanes() = drawLanes_cent()
  def drawLanes_iidx() = {
    var offsetX: Float = laneX
    val offsetY: Float = laneY
    if (leftSide) {
      game.makeRect("000000", offsetX, offsetY, redW, laneLen, "ffffff")
      offsetX += redW
    }
    for (i <- 1 to laneNum/2) {
      game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
      offsetX += whiteW
      game.makeRect("000000", offsetX, offsetY, blackW, laneLen, "ffffff")
      offsetX += blackW
    }
    game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
    offsetX += whiteW
    if (!leftSide) {
      game.makeRect("000000", offsetX, offsetY, redW, laneLen, "ffffff")
      offsetX += redW
    }
    game.makeLine("00ff00", offsetX, offsetY, offsetX-judgeW, offsetY, judgeH)
  }

  def drawLanes_cent() = {
    var offsetX: Float = laneX
    val offsetY: Float = laneY
    game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
    offsetX += whiteW
    game.makeRect("000000", offsetX, offsetY, blackW, laneLen, "ffffff")
    offsetX += blackW
    game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
    offsetX += whiteW
    if (leftSide) {
      game.makeRect("000000", offsetX, offsetY, redW, laneLen, "ffffff")
      offsetX += redW
      game.makeRect("000000", offsetX, offsetY, blackW, laneLen, "ffffff")
      offsetX += blackW
    }
    else {
      game.makeRect("000000", offsetX, offsetY, blackW, laneLen, "ffffff")
      offsetX += blackW
      game.makeRect("000000", offsetX, offsetY, redW, laneLen, "ffffff")
      offsetX += redW
    }
    game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
    offsetX += whiteW
    game.makeRect("000000", offsetX, offsetY, blackW, laneLen, "ffffff")
    offsetX += blackW
    game.makeRect("181818", offsetX, offsetY, whiteW, laneLen, "ffffff")
    offsetX += whiteW
    game.makeLine("00ff00", offsetX, offsetY, offsetX-judgeW, offsetY, judgeH)
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

  val animeX: Float = laneX + judgeW + 70
  val animeY: Float = 400
  val animeW: Float = 300
  val animeH: Float = 300
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
    // game.drawImage(cacheImage, animeX, animeY, animeW, animeH)
    game.makeRect("000000", animeX, animeY, animeW, animeH, "ffffff")
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

    val judgeX: Float = laneX + 80
    val judgeY: Float = laneY + 40
    game.makeFont("ffffff", f"$judgeStr%-8s $combo%4d", judgeX, judgeY)

    val result = game.getResult()
    val comboBreaks = game.getComboBreak()
    game.makeFont("ffffff", f"PERFECT:      ${result(0)}%4d", 920.0f, 340.0f)
    game.makeFont("ffffff", f"GREAT:        ${result(1)}%4d", 920.0f, 300.0f)
    game.makeFont("ffffff", f"GOOD:         ${result(2)}%4d", 920.0f, 260.0f)
    game.makeFont("ffffff", f"BAD:          ${result(3)}%4d", 920.0f, 220.0f)
    game.makeFont("ffffff", f"POOR:         ${result(4)}%4d", 920.0f, 180.0f)
    game.makeFont("ffffff", f"COMBO BREAK:  $comboBreaks%4d", 920.0f, 140.0f)
  }

  def drawGauge() = {
    game.makeRect("000000", 460, 40, 360, 40, "ffffff")
  }

  def drawScore() = {
    val score = game.calcScore()
    game.makeFont("ffffff", f"SCORE:   $score%6d", 80.0f, 80.0f)
    val maxCombo = game.getMaxCombo()
    game.makeFont("ffffff", f"MAX COMBO: $maxCombo%4d", 80.0f, 40.0f)
    val herebpm: Int = game.fetchBPM().toInt
    game.makeFont("ffffff", f"       BPM: $herebpm%3d", 80.0f, 0.0f)
    val level = game.fetchLevel()
    game.makeFont("ffffff", f"LEVEL: $level%2d", 920.0f, 80.0f)
    val hiSpeed = game.getHiSpeed()
    game.makeFont("ffffff", f"HISPEED: $hiSpeed%4.2f", 920.0f, 40.0f)
    val rank = game.calcRank()
    game.makeFont("ffffff", f"RANK: $rank%3s", 920.0f, 0.0f)
  }

  def drawMeter() = {
    game.makeRect("000000", 70, 120, 300, 580, "ffffff")
  }
}
