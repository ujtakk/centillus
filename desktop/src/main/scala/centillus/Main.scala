package centillus

import com.badlogic.gdx.backends.lwjgl.{
  LwjglApplication,
  LwjglApplicationConfiguration
}

object Main extends App {
  // val path = "Nank04_dragonlady/normal.bms"
  // val path = "Nank04_dragonlady/hard.bms"
  // val path = "nm23_nm05/littlehearts.bme"
  // val path = "nm23_nm05/littlehearts(another7).bme"
  // val path = "nm23_nm05/littlehearts(bt4god).bme"
  val path = "マイアミベース/miamibass.bms"
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Centillus"
  cfg.height = 600
  cfg.width = 1000
  cfg.forceExit = false
  new LwjglApplication(new Centillus(path), cfg)
}
