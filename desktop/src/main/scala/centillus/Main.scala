package centillus

import com.badlogic.gdx.backends.lwjgl.{
  LwjglApplication,
  LwjglApplicationConfiguration
}

object Main extends App {
  val path = args(0)
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Centillus"
  cfg.foregroundFPS = 60
  cfg.height = 720
  cfg.width = 1280
  cfg.forceExit = false
  new LwjglApplication(new Centillus(path), cfg)
}
