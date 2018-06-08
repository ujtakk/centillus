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
                                     }
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.{ Array
                              , Queue
                              , TimeUtils
                              }

class Centillus(val bmsPath: String) extends Game {
  lazy val batch = new SpriteBatch
  lazy val shape = new ShapeRenderer
  lazy val model = new Model

  val fps: Float = 60.0f
  val whole: Float = 1.0f
  val hispeed: Float = 2.0f
  val speed: Float = hispeed * whole/fps
  def getWhole() = whole
  def getSpeed() = speed
  def getHiSpeed() = hispeed
  def getFPS() = fps
  val whiteN: Int = 4
  val blackN: Int = 3
  val laneNum = whiteN + blackN
  def getLaneNum() = laneNum
  var barData: model.DataMap = null
  var randLane = 0 :: scala.util.Random.shuffle((1 to laneNum).toList)

  val notes = new Array[Queue[Note]](true, laneNum+1)
  for (i <- 1 to laneNum+1)
      notes.add(new Queue[Note])
  val notesBGM = new Array[Note]

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
  def isPlayingEnd() = barCount > model.getMaxBar() + 1

  def canFetchData() = model.hasData(barCount)
  def fetchData() = model.getData(barCount)
  def fetchGenre() = model.getGenre()
  def fetchTitle() = model.getTitle()
  def fetchArtist() = model.getArtist()
  def fetchLevel() = model.getLevel()
  def fetchBPM() = model.getBPM(barCount)
  def fetchTotalNotes() = model.getTotalNotes()
  def fetchSound(number: String) = model.getSound(number)
  def fetchImage(number: String) = model.getImage(number)
  def fetchStageFile() = model.getStageFile()

  var maxCombo: Int = 0
  var combo: Int = 0
  var comboBreak: Int = 0
  var judge: Judgement = Space
  var result = ArrayBuffer(0, 0, 0, 0, 0)
  def judgeInput(note: Note, missed: Boolean = false) = {
    val lane = note.getLane()
    val pos = note.getPos()
    val speed = note.getSpeed()

    val perfectPos =  2*speed
    val greatPos   =  4*speed
    val goodPos    =  8*speed
    val badPos     = 12*speed
    val poorPos    = 16*speed
    judge = pos match {
      case p if missed => {result(4) += 1; Miss}
      case p if -perfectPos < p && p < perfectPos => {result(0) += 1; Perfect}
      case p if -  greatPos < p && p <   greatPos => {result(1) += 1; Great}
      case p if -   goodPos < p && p <    goodPos => {result(2) += 1; Good}
      case p if -    badPos < p && p <     badPos => {result(3) += 1; Bad}
      case p if -   poorPos < p && p <    poorPos => {result(4) += 1; Poor}
      case _ => Space
    }

    combo = judge match {
      case Perfect | Great | Good => combo + 1
      case Poor | Space => combo
      case Bad | Miss => {comboBreak += 1; 0}
    }

    if (maxCombo < combo)
      maxCombo = combo

    judge match {
      case Perfect | Great | Good | Bad | Miss => notes.get(lane).removeFirst()
      case _ =>
    }

    updateScore(combo, judge)
  }

  def calcScore(): Int = (comboScore + judgeScore).toInt
  var comboScore: Float = 0
  var judgeScore: Float = 0
  def updateScore(combo: Int, judge: Judgement) = {
    val totalNotes: Int = fetchTotalNotes()
    val comboCoef: Float = 50000.0f / (10 * totalNotes - 55)
    comboScore += (combo match {
      case _ if combo < 2 => 0
      case _ if combo < 11 => (combo-1) * comboCoef
      case _ => 10 * comboCoef
    })
    judgeScore += (judge match {
      case Perfect => 150000.0f / totalNotes
      case Great => 100000.0f / totalNotes
      case Good => 20000.0f / totalNotes
      case Bad => 0
      case Poor => 0
      case Miss => 0
      case Space => 0
    })
  }

  def getMaxCombo() = maxCombo
  def getCombo() = combo
  def getComboBreak() = comboBreak
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

  val fontSize = 32
  lazy val fontFile = Gdx.files.internal("fonts/UbuntuMono-Regular.ttf")
  lazy val fontGen = new FreeTypeFontGenerator(fontFile)
  lazy val param = new FreeTypeFontGenerator.FreeTypeFontParameter
  param.size = fontSize
  param.color = Color.valueOf("ffffff")
  lazy val font = fontGen.generateFont(param)
  def makeFont(color: String, str: String, x: Float, y: Float) = {
    batch.begin()
    font.draw(batch, str, x, y+fontSize)
    batch.end()
  }

  def drawImage(image: Texture, x: Float, y: Float, w: Float, h: Float) = {
    batch.begin()
    batch.draw(image, x, y, w, h)
    batch.end()
  }

  val noteMap = new ObjectMap[Int, Note]
  // playNote)
  def playNote(lane: Int): Note = {
    // val note = noteMap.get(lane, null)
    // if (note == null) {
    //   return null
    // }
    val note = noteMap.get(lane, null)
    if (note != null)
      note.play()

    val noteChan = notes.get(lane)
    if (noteChan.size == 0) {
      return null
    }

    // val note = noteChan.first()
    // note.play()

    return note
  }

  def setNote(lane: Int, note: Note) = noteMap.put(lane, note)

  def updateNotes() = {
    for (lane <- 0 to laneNum) {
      val noteChan = notes.get(lane)
      val iter = noteChan.iterator()
      while (iter.hasNext()) {
        val note = iter.next()
        note.update()
      }
      if (noteChan.size != 0) {
        setNote(lane, noteChan.first)
        val noteFirst = noteChan.first()
        // if (noteFirst.getPos() < 0.0f) {
        //   playNote(noteFirst.getLane())
        //   judgeInput(noteFirst)
        // }
        if (noteFirst.getPos() < -16*speed) {
          judgeInput(noteFirst, missed=true)
        }
      }
      // val iter = notes.get(lane).iterator()
      // while (iter.hasNext()) {
      //   note.update()
      //   if (notePos <= 0.0f) {
      //     note.play()
      //     iter.remove()
      //   }
      // }
    }
    val iter = notesBGM.iterator()
    while (iter.hasNext()) {
      val note = iter.next()
      val notePos = note.getPos()
      note.update()
      if (notePos <= 0.0f) {
        note.play(0.5f)
        iter.remove()
      }
    }
  }

  private def bpm = fetchBPM()
  var baseTime: Long = 0
  def barTime: Long = (240.0 * 1e9 / bpm).toLong
  def laneTime: Float = (whole/speed) * (1e9f/fps)
  var barCount: Int = -1

  def barReset() = {
    barData = null
    barCount = -1
    baseTime = 0
    maxCombo = 0
    combo = 0
    comboBreak = 0
    judge = Space
    result = ArrayBuffer(0, 0, 0, 0, 0)
    comboScore = 0
    judgeScore = 0
    randLane = 0 :: scala.util.Random.shuffle((1 to laneNum).toList)
  }

  def barStart(): Boolean = {
    if (baseTime == 0) {
      baseTime = TimeUtils.nanoTime()
      return true
    }

    val currTime = TimeUtils.nanoTime()
    if (currTime - baseTime > barTime) {
      baseTime = currTime
      return true
    }

    return false
  }

  def makeBar() = {
    barData = fetchData()

    val laneChan = Seq(16, 11, 12, 13, 14, 15, 18, 19)
    for ((chan, laneSeed) <- laneChan.zipWithIndex) {
      val lane = randLane(laneSeed)
      if (barData.contains(chan)) {
        val chanData = barData(chan)(0)
        val noteChan = notes.get(lane)
        for ((targetTime, targetChan) <- chanData) {
          if (targetChan != "00") {
            val laneTime = (whole/speed) * (1e9f/fps)
            val ratio: Float = barTime.toFloat / laneTime.toFloat
            val offset: Float = ratio + targetTime.toFloat / barTime.toFloat * ratio
            val sample = fetchSound(targetChan)
            noteChan.addLast(new Note(lane, sample, speed, offset))
          }
        }
        setNote(lane, noteChan.first)
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
            val offset: Float = ratio + targetTime.toFloat / barTime.toFloat * ratio
            val sample = fetchSound(targetChorus)
            notesBGM.add(new Note(-1, sample, speed, offset))
          }
        }
      }
    }
  }

  def updateBar() = {
    barCount += 1
    if (canFetchData()) {
      makeBar()
    }
  }

  def calcRank(): String = {
    val totalNotes: Int = fetchTotalNotes()
    val totalScore: Float = 2.0f * totalNotes
    val resultScore: Float = 2.0f * result(0) + 1.0f * result(1)

    resultScore match {
      case _ if 8.0f/9.0f * totalScore < resultScore => "AAA"
      case _ if 7.0f/9.0f * totalScore < resultScore => "AA"
      case _ if 6.0f/9.0f * totalScore < resultScore => "A"
      case _ if 5.0f/9.0f * totalScore < resultScore => "B"
      case _ if 4.0f/9.0f * totalScore < resultScore => "C"
      case _ if 3.0f/9.0f * totalScore < resultScore => "D"
      case _ if 2.0f/9.0f * totalScore < resultScore => "E"
      case _ => "F"
    }
  }
}
