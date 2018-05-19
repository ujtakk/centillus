package centillus

import com.badlogic.gdx.backends.lwjgl.{
  LwjglApplication,
  LwjglApplicationConfiguration
}

object Main extends App {
  val path = "Nank04_dragonlady/normal.bms"
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Centillus"
  cfg.height = 600
  cfg.width = 1000
  cfg.forceExit = false
  new LwjglApplication(new Centillus(path), cfg)
}
