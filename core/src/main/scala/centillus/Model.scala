package centillus

import java.nio.file.Paths
import scala.collection.mutable.{HashMap, Map => MMap}

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.{
  Texture
}

// TODO: fix class name
class Model {
  private val parser = new BMSParser
  // TODO: consider moving to Centillus (Controller class)
  private val manager = new AssetManager
  private var prefix = ""

  private var player: Int = 0
  private var genre: String = ""
  private var title: String = ""
  private var artist: String = ""
  private var bpm: Float = 0.0f
  private var playlevel: Int = 0
  private var rank: Int = 0
  private var total: Int = 0
  private var stagefile: String = ""
  private var stagefileTexture: Texture = null

  type Id = String
  type Path = String
  val wavPathMap = new HashMap[Id, Path]
  val bmpPathMap = new HashMap[Id, Path]
  type Bar = Int
  type Chan = Int
  type Time = Float
  type Data = Seq[(Time, Id)]
  var dataMap = MMap.empty[Bar, MMap[Chan, Data]]

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
      val chanMap = dataMap.getOrElseUpdate(bar, MMap.empty[Chan, Data])
      chanMap(chan) = readObjs(objs)
    }
  }

  def readObjs(objs: String): Seq[(Time, Id)] = {
    val barSec: Float = 240 / bpm
    val numNotes: Int = objs.length / 2
    val noteNanos: Float = barSec / numNotes * 1e9.toFloat
    (0 until objs.length by 2).map(i => {
      val pos = i/2 + 1
      val time: Time = noteNanos * pos
      val body: Id = objs.substring(i, i+2)
      (time, body)
    })
  }

  def loadSounds() = {
    for ((_, filename) <- wavPathMap) {
      val path = Paths.get(prefix, filename).toString()
      // manager.load(path, classOf[Sound])
    }
  }

  def loadImages() = {
    for ((_, filename) <- bmpPathMap) {
      val path = Paths.get(prefix, filename).toString()
      manager.load(path, classOf[Texture])
    }
  }

  def updateManager(): Boolean = manager.update()

  def getBPM(bar: Int) = bpm
  def getData(bar: Int) = dataMap(bar)
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
}
