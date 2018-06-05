package centillus

import java.nio.file.Paths
import scala.collection.mutable.{Map => MMap, Seq => MSeq}

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.{
  Texture,
  Pixmap
}

// TODO: fix class name
class Model {
  private val parser = new BMSParser
  // TODO: consider moving to Centillus (Controller class)
  private val manager = new AssetManager(new InternalFileHandleResolver)
  private var prefix = ""

  private var player: Int = 0
  private var genre: String = ""
  private var title: String = ""
  private var artist: String = ""
  private var bpm: Double = 0.0f
  private var playlevel: Int = 0
  private var rank: Int = 0
  private var total: Int = 0
  private var stagefile: String = ""
  private var stagefileTexture: Texture =
    new Texture(0, 0, Pixmap.Format.RGB888)

  type Id = String
  type Path = String
  val wavPathMap = MMap.empty[Id, Path]
  val bmpPathMap = MMap.empty[Id, Path]
  type Bar = Int
  type Chan = Int
  type Time = Long
  type Data = Seq[(Time, Id)]
  type DataMap = MMap[Chan, MSeq[Data]]
  var dataMap = MMap.empty[Bar, DataMap]

  // parse the source bms file and load obtained ASTs to the inner DB.
  // header information and sequence data may be stored separately.
  def readBMS(filename: String) = {
    prefix = Paths.get(filename).getParent().toString()

    val fileHandle = Gdx.files.internal(filename)
    val dataStr = fileHandle.readString()
    val data: List[BMSObject] = parser.parse(dataStr) match {
      case Right(msg) => msg
      case Left(err) => {
        println(err)
        Gdx.app.exit()
        List(BMSNil())
      }
    }

    for (msg <- data) {
      annotate(msg)
    }
  }

  def annotate(msg: BMSObject) = msg match {
    case BMSNil() => {}
    case Player(number) => { player = number }
    case Genre(name) => { genre = name }
    case Title(name) => { title = name }
    case Artist(name) => { artist = name }
    case BPM(tempo) => { bpm = tempo }
    case Playlevel(level) => { playlevel = level }
    case Rank(number) => { rank = number }
    case Total(amount) => { total = amount }
    case Stagefile(filename) => {
      stagefile = filename
      val stagefilePath = Paths.get(prefix, stagefile).toString()
      stagefileTexture = new Texture(Gdx.files.internal(stagefilePath))
    }
    case WAV(number, filename) => { wavPathMap += (number -> filename) }
    case BMP(number, filename) => { bmpPathMap += (number -> filename) }
    case Data(bar, chan, objs) => {
      val chanMap = dataMap.getOrElseUpdate(bar, MMap.empty[Chan, MSeq[Data]])
      // chanMap(chan) = readObjs(objs)
      val dataSeq = chanMap.getOrElseUpdate(chan, MSeq())
      chanMap(chan) = dataSeq :+ readObjs(bar, chan, objs)
    }
  }

  val laneChan = Seq(16, 11, 12, 13, 14, 15, 18, 19)
  private var maxBar = 0
  private var totalNotes = 0
  def readObjs(bar: Int, chan: Int, objs: String): Seq[(Time, Id)] = {
    if (maxBar < bar)
      maxBar = bar + 1

    if (objs.contains(".")) {
      val time: Time = 0
      return Seq((time, objs))
    }

    val barSec: Double = 240.0 * 1e9 / bpm
    val numNotes: Double = objs.length / 2.0
    val noteNanos: Double = barSec / numNotes
    (0 until objs.length by 2).map(i => {
      // val pos: Double = i/2.0 + 1.0
      val pos: Double = i/2.0
      val time: Time = (noteNanos * pos).toLong
      val body: Id = objs.substring(i, i+2)
      if (laneChan.contains(chan) && body != "00")
        totalNotes += 1
      (time, body)
    })
  }

  def loadSounds() = {
    for ((_, filename) <- wavPathMap) {
      val path = Paths.get(prefix, filename).toString()
      manager.load(path, classOf[Sound])
    }
  }

  def loadImages() = {
    for ((_, filename) <- bmpPathMap) {
      val path = Paths.get(prefix, filename).toString()
      // manager.load(path, classOf[Texture])
    }
  }

  def updateManager(): Boolean = manager.update()

  def hasData(bar: Int) = dataMap.contains(bar)
  def getData(bar: Int) = dataMap(bar)
  def getBPM(bar: Int) = bpm
  def getSound(number: String) = {
    val filename = wavPathMap(number)
    val path = Paths.get(prefix, filename).toString()
    manager.get(path, classOf[Sound])
  }
  def getImage(number: String) = {
    val filename = bmpPathMap(number)
    val path = Paths.get(prefix, filename).toString()
    manager.get(path, classOf[Texture])
  }
  def getStageFile(): Texture = stagefileTexture
  def getMaxBar() = maxBar
  def getTotalNotes() = totalNotes

  def dispose() = {
    manager.dispose()
  }
}
