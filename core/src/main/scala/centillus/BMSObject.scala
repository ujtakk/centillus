package centillus

sealed trait BMSObject
case class BMSNil() extends BMSObject
case class Player(number: Int) extends BMSObject
case class Genre(name: String) extends BMSObject
case class Title(name: String) extends BMSObject
case class Artist(name: String) extends BMSObject
case class BPM(tempo: Float) extends BMSObject
case class Playlevel(level: Int) extends BMSObject
case class Rank(number: Int) extends BMSObject
case class Total(amount: Int) extends BMSObject
case class Stagefile(filename: String) extends BMSObject
case class WAV(number: String, filename: String) extends BMSObject
case class BMP(number: String, filename: String) extends BMSObject
case class Data(bar: Int, chan: Int, objs: String) extends BMSObject
