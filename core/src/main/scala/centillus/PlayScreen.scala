package centillus

import com.badlogic.gdx.{ Screen
                        , Gdx
                        }
import com.badlogic.gdx.audio.{ Sound
                              }
import com.badlogic.gdx.graphics.{ Texture
                                 , Pixmap
                                 }
import com.badlogic.gdx.utils.{ Array
                              , TimeUtils
                              }

class PlayScreen(final val game: Centillus) extends Screen {
  lazy val input = new PlayInputProcessor(game, this)
  Gdx.input.setInputProcessor(input)

  val width: Float = Gdx.graphics.getWidth().toFloat
  val height: Float = Gdx.graphics.getHeight().toFloat

  private def bpm = game.fetchBPM(barCount)

  override def dispose(): Unit = {
  }

  override def hide(): Unit = {
  }

  override def pause(): Unit = {
  }

  val fps = 60.0f
  val whole = 1.0f
  val speed = 2.5f/60.0f
  val notes = new Array[Note]
  var baseTime: Long = 0
  def barTime: Long = (240.0 * 1e9 / bpm).toLong
  val laneTime = (whole/speed) * (1e9f/fps)
  var barLinePos = barTime.toFloat / laneTime.toFloat

  var barCount: Int = 0
  var barData = game.fetchData(barCount)

  def barStart(): Boolean = {
    if (baseTime == 0) {
      barLinePos = barTime.toFloat / laneTime.toFloat
      baseTime = TimeUtils.nanoTime()
      return true
    }

    val currTime = TimeUtils.nanoTime()
    if (currTime - baseTime > barTime) {
      barLinePos = barTime.toFloat / laneTime.toFloat
      barCount += 1
      baseTime = currTime

      return true
    }

    return false
  }

  override def render(x: Float): Unit = {
    // val time = TimeUtils.millis()
    if (barStart) {
      if (game.canFetchData(barCount)) {
        barData = game.fetchData(barCount)
        makeBar()
      }
      else if (notes.size == 0)
        game.setScreen(new ResultScreen(game))
    }

    initScreen()

    drawNotes()
    drawAnime()
    // drawJudge()
    // drawGauge()
    // drawScore()

    // println(TimeUtils.timeSinceMillis(time))
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
  val judgeH: Float = 1
  // val leftSide = true
  val leftSide = false

  // def drawLanes() = {
  //   var laneX: Float = 44
  //   val laneY: Float = 44
  //   game.makeRect("000000", laneX, laneY, redW, laneLen, "ffffff")
  //   laneX += redW
  //   for (i <- 1 to laneNum/2) {
  //     game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
  //     laneX += whiteW
  //     game.makeRect("000000", laneX, laneY, blackW, laneLen, "ffffff")
  //     laneX += blackW
  //   }
  //   game.makeRect("181818", laneX, laneY, whiteW, laneLen, "ffffff")
  //   laneX += whiteW
  //   game.makeLine("00ff00", laneX, laneY, laneX-judgeW, laneY, judgeH)
  // }

  def drawLanes() = {
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

  // def playChorus() = {
  //   val chorusChan = 1
  //   if (barData.contains(chorusChan)) {
  //     val currTime = TimeUtils.timeSinceNanos(baseTime)
  //     val chorusDataSeq = barData(chorusChan)
  //     for (chorusDataIdx <- 0 until chorusDataSeq.length) {
  //       val chorusData = chorusDataSeq(chorusDataIdx)
  //       val (targetTime, targetChorus) = chorusData.head
  //       if (targetTime < currTime) {
  //         barData(chorusChan)(chorusDataIdx) = chorusData.tail
  //         if (targetChorus != "00") {
  //           val sample = game.fetchSound(targetChorus)
  //           notes.add(new Note(-1, sample, speed))
  //         }
  //       }
  //     }
  //   }
  // }

  def drawNotes() = {
    // emitNotes(0)
    // emitNotes(1)
    // emitNotes(2)
    // emitNotes(3)
    // emitNotes(4)
    // emitNotes(5)
    // emitNotes(6)
    // emitNotes(7)
    // playChorus()
    updateNotes()
    drawBarLine()
  }

  // val laneChan = Seq(16, 11, 12, 13, 14, 15, 18, 19)
  // def emitNotes(lane: Int) = {
  //   val chan = laneChan(lane)
  //   if (barData.contains(chan)) {
  //     val currTime = TimeUtils.timeSinceNanos(baseTime)
  //     val chanData = barData(chan)(0)
  //     val (targetTime, targetChan) = chanData.head
  //     if (targetTime < currTime) {
  //       val tmp = TimeUtils.nanosToMillis(currTime-targetTime)
  //       if (tmp > 10)
  //         println(chan, tmp)
  //       barData(chan)(0) = chanData.tail
  //       if (targetChan != "00") {
  //         val sample = game.fetchSound(targetChan)
  //         notes.add(new Note(lane, sample, speed))
  //       }
  //     }
  //   }
  // }

  def makeBar() = {
    val laneChan = Seq(16, 11, 12, 13, 14, 15, 18, 19)
    for ((chan, lane) <- laneChan.zipWithIndex) {
      if (barData.contains(chan)) {
        val chanData = barData(chan)(0)
        for ((targetTime, targetChan) <- chanData) {
          if (targetChan != "00") {
            val laneTime = (whole/speed) * (1e9f/fps)
            val ratio: Float = barTime.toFloat / laneTime.toFloat
            val offset: Float = ratio + targetTime.toFloat / barTime.toFloat * ratio
            val sample = game.fetchSound(targetChan)
            notes.add(new Note(lane, sample, speed, offset))
          }
        }
      }
    }
    val chorusChan = 1
    if (barData.contains(chorusChan)) {
      val chorusDataSeq = barData(chorusChan)
      for (chorusDataIdx <- 0 until chorusDataSeq.length) {
        val chorusData = chorusDataSeq(chorusDataIdx)
        for ((targetTime, targetChorus) <- chorusData) {
          if (targetChorus != "00") {
            val laneTime = (whole/speed) * (1e9f/fps)
            val ratio: Float = barTime.toFloat / laneTime.toFloat
            // val offset: Float = targetTime.toFloat / barTime.toFloat * ratio
            val offset: Float = ratio + targetTime.toFloat / barTime.toFloat * ratio
            val sample = game.fetchSound(targetChorus)
            notes.add(new Note(-1, sample, speed, offset))
          }
        }
      }
    }
  }

  def drawBarLine() = {
    // println(barLinePos)
    barLinePos -= speed
    if (barLinePos <= 0.0f)
      barLinePos = barTime.toFloat / laneTime.toFloat

    if (0.0f <= barLinePos && barLinePos < 1.0f) {
      val barPos = laneLen * barLinePos
      game.makeLine("ffffff", laneX, laneY+barPos, laneX+judgeW, laneY+barPos, 1.0f)
    }
  }

  def updateNotes() = {
    val iter = notes.iterator()
    while (iter.hasNext()) {
      val note = iter.next()
      val noteLane = note.getLane()
      val notePos = note.getPos()
      if (0.0f < notePos && notePos < 1.0f)
        drawNote(noteLane, notePos)
      note.update()
      if (notePos <= 0.0f) {
        note.play()
        iter.remove()
      }
    }
  }

  val laneX: Float = 44
  val laneY: Float = 44
  // private def drawNote(noteLane: Int, notePos: Float): Unit =
  //   noteLane match {
  //     case 0 => {
  //       val redPos = if (leftSide) laneX else laneX+judgeW-redW
  //       game.makeRect("ff0000", redPos, laneY+notePos*laneLen, redW, redH)
  //     }
  //     case 1 | 3 | 5 | 7 => {
  //       val whitePos = if (leftSide)
  //                        laneX+redW+((noteLane-1)/2)*(whiteW+blackW)
  //                      else
  //                        laneX+((noteLane-1)/2)*(whiteW+blackW)
  //       game.makeRect("ffffff", whitePos, laneY+notePos*laneLen, whiteW, whiteH)
  //     }
  //     case 2 | 4 | 6 => {
  //       val blackPos =
  //         if (leftSide)
  //           laneX+redW+((noteLane-1)/2)*(whiteW+blackW)+whiteW
  //         else
  //           laneX+((noteLane-1)/2)*(whiteW+blackW)+whiteW
  //       game.makeRect("0000ff", blackPos, laneY+notePos*laneLen, blackW, blackH)
  //     }
  //     case _ =>
  //   }
  private def drawNote(noteLane: Int, notePos: Float): Unit =
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
    if (barData.contains(animeChan)) {
      val currTime = TimeUtils.timeSinceNanos(baseTime)
      val animeData = barData(animeChan)(0)
      val (targetTime, targetBGA) = animeData.head
      if (targetTime < currTime) {
        // barData(animeChan)(0) = animeData.tail
        if (targetBGA != "00") {
          cacheImage = game.fetchImage(targetBGA)
        }
      }
    }
    game.drawImage(cacheImage, animeX, animeY, animeW, animeH)
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
